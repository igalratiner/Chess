import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.koin.core.module.Module
import org.koin.dsl.module
import java.util.*
import javax.sql.DataSource

fun getExposedModule(dbName: String): Module {
    return module(createdAtStart = true) {
        factory {
            getJDBCDataSource(dbName)
        }
    }
}


fun getJDBCDataSource(dbName: String): DataSource {
    val props = Properties()
    props.setProperty("driverClassName", "com.mysql.jdbc.Driver")
    props.setProperty("dataSource.user", "root")
    props.setProperty("dataSource.password", "password")
    props.setProperty("jdbcUrl", "jdbc:mysql://localhost:5113/$dbName")
    props.setProperty("dataSource.databaseName", dbName)
    val config = HikariConfig(props)
    return HikariDataSource(config)
}