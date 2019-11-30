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

import services.AccessService
import services.LoginService
import services.SignupService


class AccessResource @Inject constructor(application: Application, accessService: AccessService, loginService: LoginService,
                                         signupService: SignupService)  {
    init {
        val logger = KotlinLogging.logger {}
        application.routing {
            get("/hello") {
                logger.info("Return back a message from Server: ${accessService.sayHello()}")
                call.respond(Bla("Server", accessService.sayHello()))
            }
            post("/hello") {
                val messageReceived: Bla = call.receive()
                logger.info("A message from ${messageReceived.holder}: ${messageReceived.message}")
                logger.info("Return back a message from Server: ${accessService.sayHello()}")
                call.respond(Bla("Server", accessService.sayHello()))
            }
            post(LOGIN_PATH) {
                val credentials: Credentials = call.receive()
                logger.info("credentials received for login $credentials")
                val sessionToken = loginService.login(credentials.username, credentials.password)
                logger.info("sessionToken for login $sessionToken")
                call.respond(sessionToken)
            }
            post(SIGNUP_PATH) {
                val credentials: Credentials = call.receive()
                logger.info("credentials received for signup $credentials")
                val sessionToken = signupService.processSignup(credentials.username, credentials.password)
                logger.info("sessionToken for signup $sessionToken")
                call.respond(sessionToken)
            }
        }
    }
}