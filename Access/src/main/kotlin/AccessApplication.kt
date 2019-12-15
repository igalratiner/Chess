import com.google.inject.Guice
import di.AccessModule
import io.ktor.application.Application

fun Application.module() {
    Guice.createInjector(AccessModule(this), ExposedModule("access"))
    baseModule()
}