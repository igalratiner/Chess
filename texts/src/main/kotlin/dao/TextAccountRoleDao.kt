package dao

import com.google.inject.Inject
import mu.KLogging
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import pojo.Account
import pojo.TextRole
import javax.sql.DataSource

class TextAccountRoleDao @Inject constructor(dataSource: DataSource) {
    companion object : KLogging()

    private val db: Database = Database.connect(dataSource)

    init {
        transaction {
            SchemaUtils.create(TextsDao.Texts, TextAccountRole)
        }
    }

    fun getTextAccounts(textHash: String): List<Int> {
        return transaction(db) {
            TextAccountRoleEntry.find{TextAccountRole.textHash eq textHash}.map { it.accountId }
        }
    }

    fun getTexts(accountId: Int): List<String> {
        return transaction(db) {
            TextAccountRoleEntry.find{TextAccountRole.accountId eq accountId}.map { it.textHash }
        }
    }

    fun getTextRole(accountId: Int, textHash: String): TextRole? {
        return transaction(db) {
            TextAccountRoleEntry.find{ (TextAccountRole.accountId eq accountId) and (TextAccountRole.textHash eq textHash)}.singleOrNull()?.role
        }
    }

    fun createTextToAccount(textHash: String, accountId: Int) {
        updateTextToAccount(textHash, TextRole.OWNER, accountId)
    }

    fun updateTextToAccount(textHash: String, role: TextRole, accountId: Int) {
        transaction(db) {
            TextAccountRoleEntry.find { (TextAccountRole.accountId eq accountId) and (TextAccountRole.textHash eq textHash) }
                    .singleOrNull()
                    ?.apply {
                        this.role = role
                    }
                    ?: TextAccountRoleEntry.new {
                        this.accountId = accountId
                        this.textHash = textHash
                        this.role = role
                    }
        }
    }

    fun deleteTextPermissions(textHash: String) {
        deleteTextPermissions{ TextAccountRole.textHash eq textHash }
    }

    fun deleteTextPermissions(accountId: Int) {
        deleteTextPermissions{ TextAccountRole.accountId eq accountId }
    }

    private fun deleteTextPermissions(op: SqlExpressionBuilder.()-> Op<Boolean>) {
        transaction(db) {
            TextAccountRoleEntry.find { op.invoke(this) }
                    .forEach(TextAccountRoleEntry::delete)
        }
    }

    object TextAccountRole : IntIdTable() {
        val textHash = varchar("text_hash", 50).references(TextsDao.Texts.textHash)
        val accountId = integer("account_id")
        val role = enumerationByName("role", 25, TextRole::class)
        init {
            this.uniqueIndex(textHash, accountId)
        }
    }

    class TextAccountRoleEntry(id: EntityID<Int>) : Entity<Int>(id) {
        companion object : EntityClass<Int, TextAccountRoleEntry>(TextAccountRole)

        var textHash by TextAccountRole.textHash
        var accountId by TextAccountRole.accountId
        var role by TextAccountRole.role
    }
}