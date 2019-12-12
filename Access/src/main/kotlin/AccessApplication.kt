import com.google.inject.Guice
import di.AccessModule
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.gson.gson
import org.slf4j.event.Level
import java.text.DateFormat

fun Application.module() {
    Guice.createInjector(AccessModule(this), ExposedModule("access"))

    baseModule()
}