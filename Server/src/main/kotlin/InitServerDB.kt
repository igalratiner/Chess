import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.transaction

fun initEngineDB(dbURL: String = "localhost:5113", driver: String = "com.mysql.jdbc.Driver", username: String = "root", password: String ="password") {
    Database.connect("jdbc:mysql://$dbURL/db", driver = driver, user = username, password = password)
    transaction {
        SchemaUtils.create(UserCredentials, SessionTokens)
    }
}

object UserCredentials : Table() {
    val userId = integer("id").autoIncrement().primaryKey()
    val username = varchar("username", 50)
    val password = varchar("password", 50)
}

object SessionTokens : Table() {
    val userId = integer("id").autoIncrement().primaryKey()
    val sessionToken = varchar("session_token", 50)
}
