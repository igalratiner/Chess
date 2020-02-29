package rest

import com.google.gson.Gson
import io.ktor.http.cio.websocket.Frame
import kotlinx.coroutines.channels.SendChannel
import pojo.Commit
import java.util.*
import kotlin.collections.HashMap

class CommitShipper(val textHash: String, private val outgoingChannel : SendChannel<Frame>) {
    private val gson = Gson()

    suspend fun shipCommit(commit: Commit) {
        outgoingChannel.send(Frame.Text(gson.toJson(commit)))
    }
}

//class CommitShippers {
//    private val instances = HashMap<String, MutableList<CommitShipper>>()
//
//    fun addShipper(commitShipper: CommitShipper) {
//        if (instances.containsKey(commitShipper.textHash)) {
//            instances[commitShipper.textHash]!!.add(commitShipper)
//        } else {
//            instances[commitShipper.textHash] = LinkedList()
//            instances[commitShipper.textHash]!!.add(commitShipper)
//        }
//    }
//
//    fun removeShipper(commitShipper: CommitShipper) {
//        if (instances.containsKey(commitShipper.textHash)) {
//            instances[commitShipper.textHash]!!.remove(commitShipper)
//        } else {
//            instances[commitShipper.textHash] = LinkedList()
//            instances[commitShipper.textHash]!!.remove(commitShipper)
//        }
//    }
//
//    suspend fun shipCommit(commit: Commit, textHash: String) {
//        if (instances.containsKey(textHash)) {
//            instances[textHash]!!.forEach {
//                it.shipCommit(commit)
//            }
//        }
//    }
//}