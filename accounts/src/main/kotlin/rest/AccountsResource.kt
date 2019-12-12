package rest

import client.AccountsClient.Companion.ACCOUNT_PATH
import client.AccountsClient.Companion.USERNAME_PATH
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
            post(ACCOUNT_PATH) {
                val accountRequest: AccountRequest = call.receive()
                logger.info("account request=$accountRequest was received")
                call.respond(accountsService.createAccount(accountRequest))
            }
            get("$USERNAME_PATH/{username}") {
                val username: String = call.parameters["username"] ?: throw RuntimeException("no valid username specified in path")
                logger.info("account get request for username $username received")
                call.respond(accountsService.getAccount(username))
            }
        }
    }
}