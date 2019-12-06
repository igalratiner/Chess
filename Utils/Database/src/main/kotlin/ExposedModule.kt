import com.google.inject.AbstractModule
import com.google.inject.Provides
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.util.Properties
import javax.sql.DataSource


class ExposedModule(private val dbName: String) : AbstractModule() {

    @Provides
    fun getJDBCDataSource(): DataSource {
        val props = Properties()
        props.setProperty("driverClassName", "com.mysql.jdbc.Driver")
        props.setProperty("dataSource.user", "root")
        props.setProperty("dataSource.password", "password")
        props.setProperty("jdbcUrl", "jdbc:mysql://localhost:5113/$dbName")
        props.setProperty("dataSource.databaseName", dbName)
        val config = HikariConfig(props)
        return HikariDataSource(config)
    }
}