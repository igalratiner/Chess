package di

import com.google.inject.AbstractModule
import io.ktor.application.Application
import rest.ServerResource
import services.ServerService

class ServerModule(private val application: Application) : AbstractModule() {
    override fun configure() {
        bind(Application::class.java).toInstance(application)
        bind(ServerResource::class.java).asEagerSingleton()
        bind(ServerService::class.java).asEagerSingleton()
    }
}