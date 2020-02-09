import authentication.accountAuthenticatedModule
import authentication.textAccessAuthenticatedModule
import di.CommitsModule
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.routing.routing
import io.ktor.websocket.WebSockets
import org.koin.Logger.slf4jLogger
import org.koin.ktor.ext.Koin
import rest.commits

fun Application.module() {
    baseModule()
    textAccessAuthenticatedModule()
    install(Koin) {
        slf4jLogger()
        modules(CommitsModule, getExposedModule("commits"))
    }
    install(WebSockets) {
        maxFrameSize = Long.MAX_VALUE // Disabled (max value). The connection will be closed if surpassed this length.
        masking = false
    }
    routing {
        commits()
    }
}