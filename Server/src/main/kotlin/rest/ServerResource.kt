package rest

import di.serviceModule
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.DefaultHeaders
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import org.koin.Logger.slf4jLogger
import org.koin.core.KoinApplication.Companion.logger
import org.koin.ktor.ext.Koin
import org.koin.ktor.ext.inject
import service.ServerService


fun Application.module() {
    // Install Ktor features
    install(DefaultHeaders)
    install(CallLogging)
    install(Koin) {
        slf4jLogger()
        modules(serviceModule)
    }

// Lazy inject HelloService
    val service by inject<ServerService>()

// Routing section
    routing {
        get("/hello") {
            logger.info("Entered get endpoint 'hello'")
            call.respondText(service.sayHello())
        }
        post("/hello") {
            logger.info("Entered post endpoint 'hello'")
            call.respondText(call.request.toString()+" : " + service.sayHello())
        }
    }
}