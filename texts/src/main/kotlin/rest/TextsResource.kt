package rest

import authentication.TEXT_ACCESS_AUTH
import authentication.TextPrincipal
import authentication.account
import aurthorization.rolesAllowed
import client.TextsClient.Companion.ACCOUNT
import client.TextsClient.Companion.TEXTS
import client.TextsClient.Companion.TEXT_AUTH_TOKEN
import client.TextsClient.Companion.TEXT_DETAILS
import client.TextsClient.Companion.TEXT_PATH
import client.TextsClient.Companion.TEXT_PROVISION
import com.google.inject.Inject
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.auth.principal
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.*
import io.ktor.util.KtorExperimentalAPI
import mu.KLogging
import pojo.TextDetails
import pojo.TextRole.*
import service.TextsService

@KtorExperimentalAPI
class TextsResource @Inject constructor(application: Application, textsService: TextsService) {

    companion object : KLogging() {
        const val TEXT_HASH = "textHash"
        const val TEXT_ROLE = "textRole"
    }

    init {
        application.routing {
            route("$TEXT_PROVISION/{text-provision}") {
                get {
                    // returns a TextProvision containing jwt role + text hash
                    val textAuthorizationToken = textsService.getTextAuthorization(call.parameters["text-provision"]!!)
                    call.respond(textAuthorizationToken)
                }
            }
            route(TEXTS) {
                get() {
                    // get list of textHash
                    val accountTexts = textsService.getTexts(call.account!!.id)
                    call.respond(accountTexts)
                }
            }
            route(TEXT_PATH) {
                post {
                    // create an entry in table texts: id, table given name(from body), text hash (generated unique)
                    // insert to textHash+accounts->OWNER role (textHash+authentication.getAccount unique key)
                    // return pojo representing textHash + given name
                    val textName: String = call.receive()
                    val createdText: TextDetails = textsService.createText(textName, call.account!!.id)
                    call.respond(createdText)
                }
                route("/{$TEXT_HASH}") {
                    get(TEXT_DETAILS) {
                        call.respond(textsService.getTextDetails(call.parameters[TEXT_HASH]!!))
                    }
                    get(TEXT_AUTH_TOKEN) {
                        // check authentication.getAccount is mapped to text permitted accounts
                        // grant new jwt token
                        call.respond(textsService.getTextAuthorization(call.account!!.id, call.parameters[TEXT_HASH]!!))
                    }
                    authenticate(TEXT_ACCESS_AUTH) {
                        handle {
                            val jwtTextHash = this.context.principal<TextPrincipal>()!!.textDetails.hash
                            val routedTextHash: String = call.parameters[TEXT_HASH]
                                    ?: throw RuntimeException("no valid text hash")
                            assert(jwtTextHash == routedTextHash)
                        }

                        // roles allowed Owner
                        rolesAllowed(OWNER) {
                            get("/share-link/{$TEXT_ROLE}") {
                                val textProvision = textsService.shareText(call.parameters[TEXT_HASH]!!, valueOf(call.parameters[TEXT_ROLE]!!))
                                call.respond(textProvision)
                                // payload contains the type of permission granted with textHash
                                // creates (if not existing) and sends back a TextProvision mapped to the file hash and permission
                            }
                            post("/share-text-to-authentication.getAccount/{$TEXT_ROLE}") {
                                // payload contains the type of permission granted with textHash
                                // adds textHash to authentication.getAccount:texts mapping
                                // creates (if not existing) and sends back a TextProvision mapped to the file hash and permission
                                val usernameToShareWith: String = call.receive()
                                textsService.shareTextWithAccount(call.parameters[TEXT_HASH]!!, valueOf(call.parameters[TEXT_ROLE]!!), usernameToShareWith)
                                call.respond(HttpStatusCode.Accepted)
                            }
                            delete {
                                textsService.deleteText(call.parameters[TEXT_HASH]!!)
                                // get all accounts mapped to textHash
                                // go to textHash+accounts->permission an delete all entries of textHash
                                // go to texts and delete textHash
                            }
                        }

                        // roles allowed Owner + Editor
                        rolesAllowed(OWNER, EDITOR) {
                            put {
                                textsService.updateText()
                                // update text
                            }
                        }

                        // roles allowed Owner + Editor + Reader
                        rolesAllowed(OWNER, EDITOR, READER) {
                            get {
                                // get text
                            }
                        }
                    }
                }
            }
        }
    }
}