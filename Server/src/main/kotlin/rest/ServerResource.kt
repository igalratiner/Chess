package rest

import client.Bla
import com.google.inject.Inject
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import mu.KotlinLogging

import service.ServerService


class ServerResource @Inject constructor(application: Application, service: ServerService)  {
    init {
        val logger = KotlinLogging.logger {}
        application.routing {
            get("/hello") {
                logger.info("Return back a message from Server: ${service.sayHello()}")
                call.respond(Bla("Server", service.sayHello()))
            }
            post("/hello") {
                val messageReceived: Bla = call.receive()
                logger.info("A message from ${messageReceived.holder}: ${messageReceived.message}")
                logger.info("Return back a message from Server: ${service.sayHello()}")
                call.respond(Bla("Server", service.sayHello()))
            }
        }
    }
}