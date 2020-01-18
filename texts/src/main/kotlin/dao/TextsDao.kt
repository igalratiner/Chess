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
import javax.sql.DataSource
import kotlin.random.Random

class TextsDao @Inject constructor(dataSource: DataSource) {
    companion object : KLogging()

    private val db: Database = Database.connect(dataSource)

    init {
        transaction {
            SchemaUtils.create(Texts)
        }
    }

    fun getTextName(textHash: String): String? {
        return transaction(db) {
            TextsEntry.find { Texts.textHash eq textHash }.singleOrNull()?.textName
        }
    }

    fun getTextHash(textName: String): String? {
        return transaction(db) {
            TextsEntry.find { Texts.textName eq textName }.singleOrNull()?.textHash
        }
    }

    fun createText(textName: String) {
        transaction(db) {
            TextsEntry.new {
                this.textName = textName
                this.textHash = createUniqueTextHash()
            }
        }
    }

    fun deleteText(textHash: String) {
        return transaction(db) {
            TextsEntry.find { Texts.textHash eq textHash }.singleOrNull()?.delete()
        }
    }

    private fun createUniqueTextHash(): String {
        var textHash: String
        return transaction(db) {
            do  {
                textHash = Random.nextLong().toString().md5()

            } while (!TextsEntry.find { Texts.textHash eq textHash }.empty())
            return@transaction textHash
        }
    }
}

object Texts : IntIdTable() {
    val textHash = varchar("text_hash", 50).uniqueIndex()
    val textName = varchar("text_name", 100)
}

class TextsEntry(id: EntityID<Int>) : Entity<Int>(id) {
    companion object : EntityClass<Int, TextsEntry>(Texts)

    var textHash by Texts.textHash
    var textName by Texts.textName
}