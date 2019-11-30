package dao

import com.google.inject.Inject
import mu.KLogging
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import pojo.Account
import javax.sql.DataSource

class AccountsDao @Inject constructor(dataSource: DataSource) {
    companion object : KLogging()

    private val db: Database = Database.connect(dataSource)

    init {
        transaction {
            SchemaUtils.create(Accounts)
        }
    }

    fun getAccountById(id: Int): Account? {
        return transaction(db) {
            AccountEntry.findById(id)?.let { accountEntry -> Account(accountEntry.id.value, accountEntry.username) }
        }
    }

    fun getAccountByUsername(username: String): Account? = transaction(db) {
        AccountEntry.find { Accounts.username eq username }
                .let {
                    if (it.empty()) {
                        null
                    } else {
                        it.elementAt(0).let { accountEntry -> Account(accountEntry.id.value, accountEntry.username) }
                    }
                }
    }

    fun createAccount(username: String) : Account {
        return transaction(db) {
            AccountEntry.new {
                this.username = username
            }
                    .let { accountEntry -> Account(accountEntry.id.value, accountEntry.username) }
        }
    }
}

object Accounts : IntIdTable() {
    val username = varchar("username", 50).uniqueIndex()
}


class AccountEntry(id: EntityID<Int>) : Entity<Int>(id) {
    companion object : EntityClass<Int, AccountEntry>(Accounts)

    var username by Accounts.username
}