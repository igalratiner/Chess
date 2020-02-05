import com.google.inject.AbstractModule
import com.google.inject.Provides
import javax.sql.DataSource


class ExposedGuiceModule(private val dbName: String) : AbstractModule() {

    @Provides
    fun getDataSource(): DataSource {
        return getJDBCDataSource(dbName)
    }
}