package dao

import com.google.inject.Inject
import mu.KLogging
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import pojos.SessionToken
import java.time.Instant
import javax.sql.DataSource

class SessionDao @Inject constructor(dataSource: DataSource) {
    companion object : KLogging()

    private val db: Database = Database.connect(dataSource)

    init {
        transaction {
            SchemaUtils.create(UserCredentials, SessionTokens)
        }
    }

    fun getSession(userId: Int, token: String) : SessionToken? = transaction(db) {
        SessionTokenEntry.find { (SessionTokens.userId eq userId) and (SessionTokens.token eq token)}
    }.let {
        return if (it.empty()) {
            null
        } else {
            it.elementAt(0).let { sessionTokenEntry -> SessionToken(sessionTokenEntry.userId, sessionTokenEntry.token, Instant.ofEpochMilli(sessionTokenEntry.createdAt.millis)) }
        }
    }

    fun createSession(userId: Int, token: String) : SessionToken {
        return transaction(db) {
            SessionTokenEntry.new {
                this.userId = this.userId
                this.token = this.token
                this.createdAt = DateTime.now()
            }.let { sessionTokenEntry -> SessionToken(sessionTokenEntry.userId, sessionTokenEntry.token, Instant.ofEpochMilli(sessionTokenEntry.createdAt.millis)) }
        }
    }
}

object SessionTokens : IntIdTable() {
    val userId = integer("user_id").uniqueIndex().references(UserCredentials.id)
    val token = varchar("token", 50).uniqueIndex()
    val createdAt = datetime("created_at")
}

class SessionTokenEntry(id: EntityID<Int>) : Entity<Int>(id) {
    companion object : EntityClass<Int, SessionTokenEntry>(SessionTokens)

    var userId by SessionTokens.userId
    var token by SessionTokens.token
    var createdAt by SessionTokens.createdAt
}