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
import pojo.TextDetails
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

    fun getText(textHash: String): TextDetails? {
        return transaction(db) {
            TextsEntry.find { Texts.textHash eq textHash }.singleOrNull()?.let { TextDetails(it.textHash, it.textName) }
        }
    }

    fun createText(textName: String): TextDetails {
        return transaction(db) {
            TextsEntry.new {
                this.textName = textName
                this.textHash = createUniqueTextHash()
            }.let { TextDetails(it.textHash, it.textName) }
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
            textHash
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