import com.google.inject.Guice
import di.TextsModule
import io.ktor.application.Application

fun Application.module() {
    Guice.createInjector(TextsModule(this), ExposedModule("texts"))
    baseModule()
    textAccessAuthenticatedModule()
}