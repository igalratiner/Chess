package di

import com.google.inject.AbstractModule
import dao.SessionDao
import dao.UserCredentialsDao
import io.ktor.application.Application
import rest.AccessResource
import services.AccessService
import services.SigningService

class AccessModule(private val application: Application) : AbstractModule() {
    override fun configure() {
        bind(Application::class.java).toInstance(application)
        bind(AccessResource::class.java).asEagerSingleton()
        bind(AccessService::class.java).asEagerSingleton()
        bind(SigningService::class.java).asEagerSingleton()
        bind(SessionDao::class.java).asEagerSingleton()
        bind(UserCredentialsDao::class.java).asEagerSingleton()
    }
}