package rest

import client.AccessClient.Companion.LOGIN_PATH
import client.AccessClient.Companion.SIGNUP_PATH
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
import requests.Credentials

import service.AccessService


class AccessResource @Inject constructor(application: Application, service: AccessService)  {
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
            post(LOGIN_PATH) {
                val messageReceived: Credentials = call.receive()
                logger.info("$messageReceived")
                call.respond(Bla("Server", service.sayHello()))
            }
            post(SIGNUP_PATH) {
                val messageReceived: Credentials = call.receive()
                logger.info("$messageReceived")
                call.respond(Bla("Server", service.sayHello()))
            }
        }
    }
}