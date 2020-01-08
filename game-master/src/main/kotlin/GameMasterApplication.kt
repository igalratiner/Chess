import com.google.inject.Guice
import di.GameMasterModule
import io.ktor.application.Application

fun Application.module() {
    Guice.createInjector(GameMasterModule(this))
    baseModule()
    accountAuthenticatedModule()
}
