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
            SchemaUtils.create(UserCredentials, SessionTokens)
        }
    }

    fun getSessionForUserId(userId: Int): Session {
        return transaction(db) {
            val sessionEntries = SessionTokenEntry.find { SessionTokens.userId eq userId }
            if (!sessionEntries.empty() && checkTokenCreatedAtLastDay(Instant.ofEpochMilli(sessionEntries.elementAt(0).createdAt.millis))) {
                return@transaction sessionEntries.elementAt(0).let { sessionTokenEntry -> Session(sessionTokenEntry.token, Instant.ofEpochMilli(sessionTokenEntry.createdAt.millis)) }
            }
            if (!sessionEntries.empty()) {
                sessionEntries.elementAt(0).delete()
            }
            return@transaction SessionTokenEntry.new {
                this.userId = userId
                this.token = Random.nextLong().toString().md5()
                this.createdAt = DateTime.now()
            }.let { sessionTokenEntry -> Session(sessionTokenEntry.token, Instant.ofEpochMilli(sessionTokenEntry.createdAt.millis)) }
        }
    }
    
    fun getUserForSessionKey(sessionToken: String) : Int? {
        return transaction(db) {
            SessionTokenEntry.find { SessionTokens.token eq sessionToken }.singleOrNull()?.userId
        }
    }

    private fun checkTokenCreatedAtLastDay(tokenCreationInstant: Instant) : Boolean {
        val dayBefore = Instant.now().minus(24, ChronoUnit.HOURS)
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