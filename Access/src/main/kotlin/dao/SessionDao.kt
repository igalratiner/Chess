package dao

import com.google.inject.Inject
import crypto.md5
import mu.KLogging
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import responses.Session
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.sql.DataSource
import kotlin.random.Random

class SessionDao @Inject constructor(dataSource: DataSource) {
    companion object : KLogging()

    private val db: Database = Database.connect(dataSource)

    init {
        transaction {
            SchemaUtils.create(UserCredentials, Sessions)
        }
    }

    fun getSessionForUserId(userId: Int): Session {
        return transaction(db) {
            val sessionEntry = SessionEntry.find { Sessions.userId eq userId }.singleOrNull()
            if (sessionEntry != null) {
                if (checkTokenCreatedAtLastDay(Instant.ofEpochMilli(sessionEntry.createdAt.millis))) {
                    return@transaction Session(sessionEntry.token, sessionEntry.createdAt.millis)
                }  else {
                    sessionEntry.delete()
                }
            }
            return@transaction SessionEntry.new {
                this.userId = userId
                this.token = getUniqueSessionToken()
                this.createdAt = DateTime.now()
            }.let { sessionTokenEntry -> Session(sessionTokenEntry.token, sessionTokenEntry.createdAt.millis) }
        }
    }
    
    fun getUserForSessionKey(sessionToken: String) : Int? {
        return transaction(db) {
            SessionEntry.find { Sessions.token eq sessionToken }.singleOrNull()?.userId
        }
    }

    private fun getUniqueSessionToken(): String {
        var sessionToken: String
        return transaction(db) {
            do  {
                sessionToken = Random.nextLong().toString().md5()

            } while (!SessionEntry.find { Sessions.token eq sessionToken }.empty())
            return@transaction sessionToken
        }
    }

    private fun deleteSessionsBefore(timeInMilli: Long) {
        transaction(db) {
            SessionEntry.find { Sessions.createdAt less DateTime(Instant.now().minusMillis(timeInMilli)) }
                    .forEach(SessionEntry::delete)
        }
    }

    private fun checkTokenCreatedAtLastDay(tokenCreationInstant: Instant) : Boolean {
        val dayBefore = Instant.now().minus(24, ChronoUnit.HOURS)
        return tokenCreationInstant.isAfter(dayBefore)
    }

    object Sessions : IntIdTable() {
        val userId = integer("user_id").uniqueIndex().references(UserCredentials.id)
        val token = varchar("token", 50).uniqueIndex()
        val createdAt = datetime("created_at")
    }

    class SessionEntry(id: EntityID<Int>) : Entity<Int>(id) {
        companion object : EntityClass<Int, SessionEntry>(Sessions)

        var userId by Sessions.userId
        var token by Sessions.token
        var createdAt by Sessions.createdAt
    }
}