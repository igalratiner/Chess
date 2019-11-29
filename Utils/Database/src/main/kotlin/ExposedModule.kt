import com.google.inject.AbstractModule
import com.google.inject.Provides
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import javax.sql.DataSource


class ExposedModule : AbstractModule() {

    @Provides
    fun getJDBCDataSource(): DataSource {
        val config = HikariConfig("datasource.properties")
        return HikariDataSource(config)
    }
}