package rest

import client.AccountsClient.Companion.ACCOUNT_PATH
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
import requests.AccountRequest

import services.AccountsService


class AccountsResource @Inject constructor(application: Application, accountsService: AccountsService)  {
    init {
        val logger = KotlinLogging.logger {}
        application.routing {
            get("/hello") {
                logger.info("Return back a message from Server: ${accountsService.sayHello()}")
                call.respond(Bla("Server", accountsService.sayHello()))
            }
            post("/hello") {
                val messageReceived: Bla = call.receive()
                logger.info("A message from ${messageReceived.holder}: ${messageReceived.message}")
                logger.info("Return back a message from Server: ${accountsService.sayHello()}")
                call.respond(Bla("Server", accountsService.sayHello()))
            }
            post(ACCOUNT_PATH) {
                val accountRequest: AccountRequest = call.receive()
                logger.info("$accountRequest")
                call.respond(accountsService.createAccount(accountRequest))
            }
        }
    }
}