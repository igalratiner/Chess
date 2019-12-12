package di

import com.google.inject.AbstractModule
import io.ktor.application.Application
import rest.GameMasterResource
import services.GameMasterService

class GameMasterModule(private val application: Application) : AbstractModule() {
    override fun configure() {
        bind(Application::class.java).toInstance(application)
        bind(GameMasterResource::class.java).asEagerSingleton()
        bind(GameMasterService::class.java).asEagerSingleton()
    }
}