package dao

import com.google.inject.Inject
import md5
import mu.KLogging
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import javax.sql.DataSource

class UserCredentialsDao @Inject constructor(dataSource: DataSource) {
    companion object : KLogging()

    private val db: Database = Database.connect(dataSource)

    init {
        transaction {
            SchemaUtils.create(UserCredentials)
        }
    }

    fun getUserId(username: String, password: String): Int? {
        return transaction(db) {
            UserCredentialsEntry.find { (UserCredentials.username eq username) and (UserCredentials.password eq password.md5()) }
                    .firstOrNull()?.id?.value
        }
    }

    fun createUserCredentials(username: String, password: String) : Int {
        return transaction(db) {
            UserCredentialsEntry.new {
                this.username = username
                this.password = password.md5()
            }.id.value
        }
    }
}

object UserCredentials : IntIdTable() {
    val username = varchar("username", 50).uniqueIndex()
    val password = varchar("password", 50)
}

class UserCredentialsEntry(id: EntityID<Int>) : Entity<Int>(id) {
    companion object : EntityClass<Int, UserCredentialsEntry>(UserCredentials)

    var username by UserCredentials.username
    var password by UserCredentials.password
}