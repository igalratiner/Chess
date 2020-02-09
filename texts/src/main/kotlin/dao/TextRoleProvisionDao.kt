package dao

import com.google.inject.Inject
import crypto.md5
import mu.KLogging
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import pojo.TextAccess
import pojo.TextRole
import java.time.Instant
import javax.sql.DataSource
import kotlin.random.Random

class TextRoleProvisionDao @Inject constructor(dataSource: DataSource) {
    companion object : KLogging()

    private val db: Database = Database.connect(dataSource)

    init {
        transaction {
            SchemaUtils.create(TextsDao.Texts, TextRoleProvision)
        }
    }

    fun getTextAccess(textProvisionHash: String): TextAccess? {
        return transaction(db) {
            TextRoleProvisionEntry.find{TextRoleProvision.textProvisionHash eq textProvisionHash}
                    .singleOrNull()?.let { TextAccess(it.textHash, it.role) }
        }
    }

    fun addProvisionToText(textHash: String, role: TextRole): String {
        return transaction(db) {
            TextRoleProvisionEntry.new{
                this.textHash = textHash
                this.role = role
                this.textProvisionHash = createUniqueTextProvisionHash()
                this.createdAt = DateTime.now()
            }.textProvisionHash
        }
    }

    private fun createUniqueTextProvisionHash(): String {
        var textProvisionHash: String
        return transaction(db) {
            do  {
                textProvisionHash = Random.nextLong().toString().md5()

            } while (!TextRoleProvisionEntry.find { TextRoleProvision.textProvisionHash eq textProvisionHash }.empty())
            textProvisionHash
        }
    }

    fun deleteTextProvisions(textHash: String) {
//        transaction(db) {
//            TextAccountRoleEntry.find { TextRoleProvision.textHash eq textHash }
//                    .forEach(TextAccountRoleEntry::delete)
//        }
        deleteProvisions{ TextRoleProvision.textHash eq textHash }
    }

    private fun deleteProvisionsBefore(timeInMilli: Long) {
//        transaction(db) {
//            TextAccountRoleEntry.find { TextRoleProvision.createdAt less DateTime(Instant.now().minusMillis(timeInMilli)) }
//                    .forEach(TextAccountRoleEntry::delete)
//        }
        deleteProvisions{ TextRoleProvision.createdAt less DateTime(Instant.now().minusMillis(timeInMilli)) }
    }

    private fun deleteProvisions(op: SqlExpressionBuilder.()-> Op<Boolean>) {
        transaction(db) {
            TextRoleProvisionEntry.find { op.invoke(this) }
                    .forEach(TextRoleProvisionEntry::delete)
        }
    }

    object TextRoleProvision : IntIdTable() {
        val textHash = varchar("text_hash", 50).references(TextsDao.Texts.textHash)
        val role = enumerationByName("role", 25, TextRole::class)
        val textProvisionHash = varchar("text_provision", 50).uniqueIndex()
        val createdAt = datetime("created_at")
    }

    class TextRoleProvisionEntry(id: EntityID<Int>) : Entity<Int>(id) {
        companion object : EntityClass<Int, TextRoleProvisionEntry>(TextRoleProvision)

        var textHash by TextRoleProvision.textHash
        var role by TextRoleProvision.role
        var textProvisionHash by TextRoleProvision.textProvisionHash
        var createdAt by TextRoleProvision.createdAt
    }
}