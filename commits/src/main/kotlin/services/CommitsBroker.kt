package services

import org.redisson.api.RList
import org.redisson.api.RTopic
import org.redisson.api.RedissonClient
import pojo.Commit
import java.io.Serializable

class CommitsBroker internal constructor(private val textCommits: RList<Commit>, private val textTopic: RTopic): Serializable {

    fun addCommit(commit: Commit) {
        textCommits.add(commit)
        textTopic.publish(commit)
    }

    operator fun get(index: Int): Commit {
        return textCommits[index]
    }

    fun getAll(): List<Commit> {
        return ArrayList<Commit>(textCommits)
    }

    fun size(): Int {
        return textCommits.size
    }

    fun subscribe(listener: (Commit) -> Unit): Int {
        return textTopic.addListener(Commit::class.java) {
            _, commit -> listener(commit)
        }
    }

    fun unsubscribe(listenerId: Int) {
        textTopic.removeListener(listenerId)
    }

    fun unsubscribeAll() {
        textTopic.removeAllListeners()
    }
}

fun RedissonClient.getCommitsBroker(textHash: String): CommitsBroker {
    return CommitsBroker(getList(textHash), getTopic(textHash))
}