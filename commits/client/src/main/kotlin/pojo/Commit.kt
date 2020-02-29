package pojo

import java.io.Serializable

data class Commit(val metadata: CommitMetadata, val initIndex: Long, val endIndex: Long, val action: String): Serializable

data class CommitMetadata(val index: Int): Serializable

data class CommitRequest(val index: Int)