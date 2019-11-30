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
import java.math.BigInteger
import java.security.MessageDigest
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalUnit
import javax.sql.DataSource
import kotlin.random.Random

class SessionDao @Inject constructor(dataSource: DataSource) {
    companion object : KLogging()

    private val db: Database = Database.connect(dataSource)

    init {
        transaction {
            SchemaUtils.create(UserCredentials, SessionTokens)
        }
    }

//    fun getSession(userId: Int, token: String): SessionToken? = transaction(db) {
//        SessionTokenEntry.find { (SessionTokens.userId eq userId) and (SessionTokens.token eq token) }
//    }.let {
//        return if (it.empty()) {
//            null
//        } else {
//            it.elementAt(0).let { sessionTokenEntry -> SessionToken(sessionTokenEntry.userId, sessionTokenEntry.token, Instant.ofEpochMilli(sessionTokenEntry.createdAt.millis)) }
//        }
//    }
//
//    fun createSession(userId: Int, token: String): SessionToken {
//        return transaction(db) {
//            SessionTokenEntry.new {
//                this.userId = userId
//                this.token = token
//                this.createdAt = DateTime.now()
//            }.let { sessionTokenEntry -> SessionToken(sessionTokenEntry.userId, sessionTokenEntry.token, Instant.ofEpochMilli(sessionTokenEntry.createdAt.millis)) }
//        }
//    }

    fun getUserSession(userId: Int): SessionToken {
        return transaction(db) {
            val sessionEntries = SessionTokenEntry.find { SessionTokens.userId eq userId }
            if (!sessionEntries.empty() && checkTokenCreatedAtLastDay(Instant.ofEpochMilli(sessionEntries.elementAt(0).createdAt.millis))) {
                return@transaction sessionEntries.elementAt(0).let { sessionTokenEntry -> SessionToken(sessionTokenEntry.userId, sessionTokenEntry.token, Instant.ofEpochMilli(sessionTokenEntry.createdAt.millis)) }
            }
            if (!sessionEntries.empty()) {
                sessionEntries.elementAt(0).delete()
            }
            return@transaction SessionTokenEntry.new {
                this.userId = userId
                this.token = Random.nextLong().toString().md5()
                this.createdAt = DateTime.now()
            }.let { sessionTokenEntry -> SessionToken(sessionTokenEntry.userId, sessionTokenEntry.token, Instant.ofEpochMilli(sessionTokenEntry.createdAt.millis)) }
        }
    }

    private fun String.md5(): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(toByteArray())).toString(16).padStart(32, '0')
    }

    private fun checkTokenCreatedAtLastDay(tokenCreationInstant: Instant) : Boolean {
        val now = Instant.now()
        val dayBefore = now.minus(24, ChronoUnit.HOURS)
        return tokenCreationInstant.isAfter(dayBefore)
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
}