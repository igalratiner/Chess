import authentication.accountAuthenticatedModule
import authentication.textAccessAuthenticatedModule
import di.CommitsModule
import io.ktor.application.Application
import io.ktor.application.install
import org.koin.Logger.slf4jLogger
import org.koin.ktor.ext.Koin

fun Application.module() {
    baseModule()
    accountAuthenticatedModule()
    textAccessAuthenticatedModule()
    install(Koin) {
        slf4jLogger()
        modules(CommitsModule, getExposedModule("commits"))
    }
}