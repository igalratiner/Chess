package rest

import client.AccessClient.Companion.LOGIN_PATH
import client.AccessClient.Companion.SIGNUP_PATH
import client.AccessClient.Companion.ACCOUNT_PATH
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

import services.SigningService


class AccessResource @Inject constructor(application: Application, signingService: SigningService, accessService: AccessService)  {
    init {
        val logger = KotlinLogging.logger {}
        application.routing {
            post(LOGIN_PATH) {
                val credentials: Credentials = call.receive()
                logger.info("credentials received for login $credentials")
                val sessionToken = signingService.processLogin(credentials.username, credentials.password)
                logger.info("sessionToken for login $sessionToken")
                call.respond(sessionToken)
            }
            post(SIGNUP_PATH) {
                val credentials: Credentials = call.receive()
                logger.info("credentials received for signup $credentials")
                val sessionToken = signingService.processSignup(credentials.username, credentials.password)
                logger.info("sessionToken for signup $sessionToken")
                call.respond(sessionToken)
            }
            get("$ACCOUNT_PATH/{session_token}") {
                val sessionToken : String = call.parameters["session_token"] ?: throw RuntimeException("no valid session token specified in path")
                logger.info("session token received for account retrieval $sessionToken")
                val account = accessService.getAccountFromSessionToken(sessionToken)
                call.respond(account)
            }
        }
    }
}