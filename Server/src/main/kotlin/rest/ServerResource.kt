package rest

import client.Bla
import di.serviceModule
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.gson.gson
import io.ktor.request.receive
import io.ktor.response.respond
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
            logger.info("Return back a message from Server: ${service.sayHello()}")
            call.respond(Bla("Server", service.sayHello()))
        }
        post("/hello") {
            val messageReceived : Bla = call.receive()
            logger.info("A message from ${messageReceived.holder}: ${messageReceived.message}")
            logger.info("Return back a message from Server: ${service.sayHello()}")
            call.respond(Bla("Server", service.sayHello()))
        }
    }
}