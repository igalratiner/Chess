import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.transaction

fun initAccessDB(dbURL: String = "localhost:5113", driver: String = "com.mysql.jdbc.Driver", username: String = "root", password: String ="password") {
    Database.connect("jdbc:mysql://$dbURL/access", driver = driver, user = username, password = password)
    transaction {
        SchemaUtils.create(UserCredentials, SessionTokens)
    }
}

object UserCredentials : Table() {
    val id = integer("id").autoIncrement().primaryKey()
    val username = varchar("username", 50)
    val password = varchar("password", 50)
}

object SessionTokens : Table() {
    val id = integer("id").autoIncrement().primaryKey()
    val userId = integer("user_id").uniqueIndex().references(UserCredentials.id)
    val sessionToken = varchar("session_token", 50)
}
