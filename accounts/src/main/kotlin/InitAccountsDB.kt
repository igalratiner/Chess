import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.transaction

fun initAccountsDB(dbURL: String = "localhost:5113", driver: String = "com.mysql.jdbc.Driver", username: String = "root", password: String ="password") {
    Database.connect("jdbc:mysql://$dbURL/accounts", driver = driver, user = username, password = password)
    transaction {
        SchemaUtils.create(Accounts, AccountStatuses)
    }
}

object Accounts : Table() {
    val id = integer("id").autoIncrement().primaryKey()
    val username = varchar("username", 50)
}

object AccountStatuses : Table() {
    val id = integer("id").autoIncrement().primaryKey()
    val accountId = integer("account_id").uniqueIndex().references(Accounts.id)
    val rate = integer("rate").default(1500)
    val wins = integer("wins").default(0)
    val loses = integer("loses").default(0)
    val ties = integer("ties").default(0)
}
