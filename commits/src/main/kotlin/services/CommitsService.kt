package services

import com.google.inject.Inject
import crypto.md5
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import mu.KLogging
import org.redisson.api.*
import pojo.Commit
import pojo.CommitRequest
import services.exceptions.TextCommitsNotExistException
import java.lang.Exception
import java.util.*
import kotlin.collections.HashSet

@ExperimentalCoroutinesApi
class CommitsService @Inject constructor(private val redissonClient: RedissonClient) {

    companion object : KLogging()

    val commitIncomingChannel = Channel<Pair<String, Commit>>()

    private val outgoingNotificationChannels = HashMap<String, MutableSet<Channel<CommitsNotification>>>()

    private val shipNotificationsChannel = Channel<Triple<Channel<CommitsNotification>, CommitsNotification, String>>()

    val deleteOldChannelsChannel = Channel<Pair<String, ReceiveChannel<CommitsNotification>>>()

    private val textCommitsMap = redissonClient.getMap<String, RList<Commit>>("textCommitsMap")

    init {
        GlobalScope.distributeCommits()
    }

    @ExperimentalCoroutinesApi
    fun connect(textHash: String): ReceiveChannel<CommitsNotification> {
        return runBlocking {
            logger.info { "connection to textHash=$textHash requested" }
            textCommitsMap[textHash] ?: throw TextCommitsNotExistException()
            outgoingNotificationChannels.putIfAbsent(textHash, HashSet())
            val notificationChannel = Channel<CommitsNotification>()
            outgoingNotificationChannels[textHash]!!.add(notificationChannel)
            logger.info { "connection to textHash=$textHash commits established" }
            notificationChannel
        }
    }

    fun createTextCommits(textHash: String): Boolean {
        val textCommits = redissonClient.getList<Commit>(textHash.md5())
        if (textCommitsMap.putIfAbsent(textHash, textCommits) === textCommits) {
            logger.info { "commits of textHash=$textHash were created" }
            return true
        }
        logger.info { "commits of textHash=$textHash already exist" }
        return false
    }

    fun getCommits(textHash: String, commitRequests: List<CommitRequest>? = null): List<Commit> {
        val textCommits = textCommitsMap[textHash] ?: throw TextCommitsNotExistException()
        val commitsRequested = LinkedList<Commit>()
        if (commitRequests != null) {
            for (commitRequest in commitRequests) {
                commitsRequested.add(textCommits[commitRequest.index])
            }
        } else {
            commitsRequested.addAll(textCommits)
        }
        return commitsRequested
    }

    fun deleteCommits(textHash: String) {
        outgoingNotificationChannels.remove(textHash)
        textCommitsMap.remove(textHash)
        logger.info { "commits of textHash=$textHash were deleted" }
    }

    @ExperimentalCoroutinesApi
    private fun CoroutineScope.distributeCommits() {
        /*
        each of the functions below are run by 1 coroutines
         */
        repeat(1) { shipCommitsNotifications() }
        repeat(1) { addCommits() }
        repeat(1) { deleteOldNotificationChannels() }
    }

    @ExperimentalCoroutinesApi
    private fun CoroutineScope.shipCommitsNotifications() = launch {
        for ((notificationChannel, notificationCommit, textHash) in shipNotificationsChannel) {
            try {
                notificationChannel.send(notificationCommit)
            } catch (e: Exception) {
                logger.warn { "Can't send to notificationChannel due to ${e.cause}" }
            }
        }
    }

    private fun CoroutineScope.deleteOldNotificationChannels() = launch {
        for ((textHash, notificationChannel) in deleteOldChannelsChannel) {
            logger.info { "for textHash=$textHash notificationChannel=$notificationChannel is deleted" }
            outgoingNotificationChannels[textHash]?.remove(notificationChannel)
        }
    }

    private fun CoroutineScope.addCommits() = launch {
        for ((textHash, commit) in commitIncomingChannel) {
            try {
                logger.info { "commit=$commit is about to be sent";  }
                val textCommits = textCommitsMap[textHash] ?: throw TextCommitsNotExistException()
                if (validateNextCommit(commit, textCommits)) {
                    textCommits.add(commit)
                    sendNotificationToSubscribedOnText( CommitsNotification.NextCommit(commit), textHash)
                }
                logger.info { "commit=$commit was sent";  }
            } catch (e: TextCommitsNotExistException) {
                logger.error { "Text=$textHash commits are not found" }
                sendNotificationToSubscribedOnText(CommitsNotification.TextCommitsNotExist, textHash)
            }
        }
    }

    private suspend fun sendNotificationToSubscribedOnText(notification: CommitsNotification, textHash: String) {
        val outgoingTextNotificationChannels = outgoingNotificationChannels[textHash]
        if (outgoingTextNotificationChannels != null) {
            for (notificationChannel in outgoingTextNotificationChannels.iterator()) {
                logger.info { "commit=$notification is about to be sent";  }

                shipNotificationsChannel.send(Triple(notificationChannel, notification, textHash))
                logger.info { "commit=$notification sent";  }
            }
        }
    }

    private fun validateNextCommit(commit: Commit, textCommits: List<Commit>): Boolean {
        return commit.metadata.index == textCommits.size
    }
}

sealed class CommitsNotification {
    object TextCommitsNotExist: CommitsNotification()
    object IllegalCommit: CommitsNotification()
    data class NextCommit(val commit: Commit): CommitsNotification()
}