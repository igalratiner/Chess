package di

import com.google.inject.AbstractModule
import io.ktor.application.Application
import rest.AccessResource
import service.ServerService

class AccessModule(private val application: Application) : AbstractModule() {
    override fun configure() {
        bind(Application::class.java).toInstance(application)
        bind(AccessResource::class.java).asEagerSingleton()
        bind(ServerService::class.java).asEagerSingleton()
    }
}