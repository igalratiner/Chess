import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.sql.Connection
import java.sql.SQLException

class ChessDataSource {
    private val config = HikariConfig("accountsdb.properties")

    private var ds: HikariDataSource? = null

    @get:Throws(SQLException::class)
    val connection: Connection
        get() = ds!!.connection

    init {
        ds = HikariDataSource(config)
    }
}