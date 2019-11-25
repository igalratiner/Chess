package di

import com.google.inject.AbstractModule
import io.ktor.application.Application
import rest.AccountsResource
import services.AccountsService

class AccountsModule(private val application: Application) : AbstractModule() {
    override fun configure() {
        bind(Application::class.java).toInstance(application)
        bind(AccountsResource::class.java).asEagerSingleton()
        bind(AccountsService::class.java).asEagerSingleton()
    }
}