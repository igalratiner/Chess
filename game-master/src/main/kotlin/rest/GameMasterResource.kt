package rest

import authentication.account
import com.google.inject.Inject
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.routing
import mu.KotlinLogging
import services.GameMasterService



class GameMasterResource @Inject constructor(application: Application, service: GameMasterService)  {
    init {
        val logger = KotlinLogging.logger {}
        application.routing {
            get("/helloFromAccount") {
                val account = call.account
                logger.info("Return back a message from GameMaster: $account")
                call.respond(service.processHello())
            }

        }
    }
}