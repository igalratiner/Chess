import io.ktor.application.Application
import security.securityModule

fun Application.securedBaseModule() {
    baseModule()
    securityModule()
}