package rest

import Bla
import di.serviceModule
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.gson.gson
import io.ktor.request.receive
import io.ktor.request.receiveChannel
import io.ktor.request.receiveText
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import org.koin.Logger.slf4jLogger
import org.koin.core.KoinApplication.Companion.logger
import org.koin.ktor.ext.Koin
import org.koin.ktor.ext.inject
import service.ServerService
import java.text.DateFormat


fun Application.module() {
    // Install Ktor features
    install(DefaultHeaders)
    install(CallLogging)
    install(Koin) {
        slf4jLogger()
        modules(serviceModule)
    }
    install(ContentNegotiation) {
        gson {
            setDateFormat(DateFormat.LONG)
            setPrettyPrinting()
        }
    }

// Lazy inject HelloService
    val service by inject<ServerService>()

// Routing section
    routing {
        get("/hello") {
            logger.info("Entered get endpoint 'hello'")
            call.respond(Bla(b="f"))
        }
        post("/hello") {
            logger.info("Entered post endpoint 'hello'")
            val some : Bla = call.receive()
            logger.info(some.toString() + ", b value=" + some.b)
            call.respond(Bla(b="f"))
        }
    }
}