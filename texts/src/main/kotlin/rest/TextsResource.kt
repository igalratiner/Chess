package rest

import TEXT_ACCESS_AUTH
import TextPrincipal
import account
import aurthorization.rolesAllowed
import client.TextsClient.Companion.TEXTS
import client.TextsClient.Companion.TEXT_PATH
import client.TextsClient.Companion.TEXT_PROVISION
import com.google.inject.Inject
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.auth.principal
import io.ktor.routing.*
import mu.KLogging
import pojo.TextRole.OWNER
import pojo.TextRole.EDITOR
import pojo.TextRole.READER
import service.TextsService

class TextsResource @Inject constructor(application: Application, textsService: TextsService)  {

    companion object : KLogging() {
        const val TEXT_HASH = "textHash"
    }

    init {
        application.routing {
            route(TEXTS) {
                get("/account/{accountId}") {
                    // get list of textHash->permission of account
                }
            }
            route(TEXT_PROVISION) {
                get {
                    // returns a TextProvision containing jwt role + text hash
                }
            }
            route(TEXT_PATH) {
                post {
                    textsService.createText()
                    // create an entry in table texts: id, table given name(from body), text hash (generated unique)
                    // insert to textHash+accounts->OWNER role (textHash+account unique key)
                    // return pojo representing textHash + given name
                }
                get("/text-authentication") {
                    call.account != null
                    // check account is mapped to text permitted accounts
                    // grant new jwt token
                }
                route("/{$TEXT_HASH}") {
                    authenticate(TEXT_ACCESS_AUTH) {
                        handle {
                            val jwtTextHash = this.context.principal<TextPrincipal>()!!.textDetails.hash
                            val routedTextHash : String = call.parameters[TEXT_HASH] ?: throw RuntimeException("no valid text hash")
                            assert(jwtTextHash == routedTextHash)
                        }

                        // roles allowed Owner
                        rolesAllowed(OWNER) {
                            post("/share-link") {
                                textsService.shareText()
                                // payload contains the type of permission granted with textHash
                                // creates (if not existing) and sends back a TextProvision mapped to the file hash and permission
                            }
                            post("/share-account/{username}") {
                                textsService.shareText()
                                // payload contains the type of permission granted with link
                                // adds textHash to account:texts mapping
                                // creates (if not existing) and sends back a TextProvision mapped to the file hash and permission
                            }
                            delete {
                                textsService.deleteText()
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