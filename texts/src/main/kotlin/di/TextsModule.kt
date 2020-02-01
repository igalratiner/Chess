package di

import com.google.inject.AbstractModule
import io.ktor.application.Application
import rest.TextsResource
import service.TextsService

class TextsModule(private val application: Application) : AbstractModule() {
    override fun configure() {
        bind(Application::class.java).toInstance(application)
        bind(TextsService::class.java).asEagerSingleton()
        bind(TextsResource::class.java).asEagerSingleton()
    }
}