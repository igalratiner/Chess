package services

import com.google.inject.Inject
import crypto.md5
import mu.KLogging
import org.redisson.api.*
import pojo.Commit

class CommitsService @Inject constructor(private val redissonClient: RedissonReactiveClient) {

    private val textCommitsMap = redissonClient.getMap<String, RQueueReactive<Commit>>("textCommitsMap")

    companion object : KLogging()

    fun connectToTextCommits(textHash: String) {

    }

    fun insertCommitToQueue(textHash: String, commit: Commit) {
        textCommitsMap[textHash].block()!!.offer(commit)
    }

    fun createTextCommits(textHash: String): Boolean {
        val textHashLock = textCommitsMap.getLock(textHash)
        textHashLock.lock()
        try {
            if (!textCommitsMap.containsKey(textHash).block()!!) {
                textCommitsMap.put(textHash, redissonClient.getBlockingDeque(textHash.md5()))
                return true
            }
        } finally {
            textHashLock.unlock()
        }
        return false
    }
}