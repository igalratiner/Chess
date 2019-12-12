package rest

import responses.Session
import client.AccessClient
import com.google.inject.Inject
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.sessions.get
import io.ktor.sessions.sessions
import mu.KotlinLogging
import services.GameMasterService


class GameMasterResource @Inject constructor(application: Application, service: GameMasterService)  {
    init {
        val logger = KotlinLogging.logger {}
        application.routing {
            get("/helloFromAccount") {
                val session : Session = call.sessions.get() ?: throw RuntimeException("no valid session token specified in path")
                logger.info("SessionToken from GameMaster: $session")
                val account = AccessClient().getSessionAccount(session.token)
                logger.info("Return back a message from GameMaster: $account")
                call.respond(service.processHello())
            }

        }
    }
}