import authentication.accountAuthenticatedModule
import authentication.textAccessAuthenticatedModule
import com.google.inject.Guice
import di.TextsModule
import io.ktor.application.Application

fun Application.module() {
    securedBaseModule()
    accountAuthenticatedModule()
    textAccessAuthenticatedModule()
    Guice.createInjector(TextsModule(this), ExposedGuiceModule("texts"))
}