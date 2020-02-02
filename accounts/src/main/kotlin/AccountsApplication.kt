import com.google.inject.Guice
import di.AccountsModule
import io.ktor.application.Application

fun Application.module() {
    Guice.createInjector(AccountsModule(this), ExposedGuiceModule("accounts"))
    baseModule()
}