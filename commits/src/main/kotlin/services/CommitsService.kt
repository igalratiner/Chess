package services

import com.google.inject.Inject
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import mu.KLogging
import org.redisson.api.*
import pojo.Commit
import pojo.CommitRequest
import services.exceptions.CommitNotInOrderException
import services.exceptions.TextCommitsNotExistException
import java.util.*

@ExperimentalCoroutinesApi
class CommitsService @Inject constructor(private val redissonClient: RedissonClient) {

    companion object : KLogging()

    val commitIncomingChannel = Channel<Triple<String, Commit, Channel<CommitsNotification>>>()

    private val textCommitsBrokersMap = HashMap<String, CommitsBroker>()

    private val textHashes = redissonClient.getSet<String>("texts")

    init {
        GlobalScope.distributeCommits()
    }

    @ExperimentalCoroutinesApi
    fun connect(textHash: String): Pair<Int, Channel<CommitsNotification>> {
        return runBlocking {
            logger.info { "connection to textHash=$textHash requested" }
            val textCommitsBroker = populateAndRetrieveCommitsBroker(textHash)

            val notificationChannel = Channel<CommitsNotification>()

            val listenerId = textCommitsBroker.subscribe {
                runBlocking {
                    notificationChannel.send(CommitsNotification.NextCommit(it))
                }
            }

            logger.info { "connection to textHash=$textHash commits established" }
            listenerId to notificationChannel
        }
    }

    fun createTextCommits(textHash: String): Boolean {
        if (textHashes.add(textHash)) {
            val textCommitsBroker = redissonClient.getCommitsBroker(textHash)
            textCommitsBrokersMap[textHash] = textCommitsBroker
            logger.info { "commits of textHash=$textHash were created" }
            return true
        }
        logger.info { "commits of textHash=$textHash already exist" }
        return false
    }

    fun getCommits(textHash: String, commitRequests: List<CommitRequest>? = null): List<Commit> {
        val textCommitsBroker = textCommitsBrokersMap[textHash]!!
        val commitsRequested = LinkedList<Commit>()
        if (commitRequests != null) {
            for (commitRequest in commitRequests) {
                commitsRequested.add(textCommitsBroker[commitRequest.index])
            }
            return commitsRequested
        }
        return textCommitsBroker.getAll()

    }

    private fun populateAndRetrieveCommitsBroker(textHash: String): CommitsBroker {
        if (!textHashes.contains(textHash)) {
            throw TextCommitsNotExistException()
        }
        val textCommitsBrokers = textCommitsBrokersMap[textHash]
        if (textCommitsBrokers == null) {
            val textCommitsBrokersFromDB = redissonClient.getCommitsBroker(textHash)
            textCommitsBrokersMap.putIfAbsent(textHash, textCommitsBrokersFromDB)
            return textCommitsBrokersFromDB
        }
        return textCommitsBrokers
    }

    fun deleteCommits(textHash: String) {
        textHashes.remove(textHash)
        textCommitsBrokersMap[textHash]?.unsubscribeAll()
        textCommitsBrokersMap.remove(textHash)
        logger.info { "commits of textHash=$textHash were deleted" }
    }

    @ExperimentalCoroutinesApi
    private fun CoroutineScope.distributeCommits() {
        /*
        each of the functions below are run by 1 coroutines
         */
        repeat(1) { addCommits() }
    }

    fun unsubscribeCommitsListener(textHash: String, listenerId: Int) {
        textCommitsBrokersMap[textHash]?.unsubscribe(listenerId)
    }

    private fun CoroutineScope.addCommits() = launch {
        for ((textHash, commit, notificationChannel) in commitIncomingChannel) {
            logger.info { "commit=$commit is about to be sent";  }
            try {
                val textCommits = textCommitsBrokersMap[textHash] ?: throw TextCommitsNotExistException()
                validateNextCommit(commit, textCommits)
                textCommits.addCommit(commit)
            } catch (e: TextCommitsNotExistException) {
                notificationChannel.send(CommitsNotification.TextCommitsNotExist)
            } catch (e: CommitNotInOrderException) {
                notificationChannel.send(CommitsNotification.CommitNotInOrder)
            }
            logger.info { "commit=$commit was sent";  }
        }
    }

    private fun validateNextCommit(commit: Commit, textCommits: CommitsBroker) {
        if (commit.metadata.index != textCommits.size()) {
            throw CommitNotInOrderException()
        }
    }
}

sealed class CommitsNotification {
    object TextCommitsNotExist: CommitsNotification()
    object CommitNotInOrder: CommitsNotification()
    data class IllegalCommit(val commit: Commit): CommitsNotification()
    data class NextCommit(val commit: Commit): CommitsNotification()
}