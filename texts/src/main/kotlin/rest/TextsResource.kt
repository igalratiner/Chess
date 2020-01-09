package rest

import TEXT_ACCESS_AUTH
import TextPrincipal
import aurthorization.rolesAllowed
import client.TextsClient.Companion.TEXT_PATH
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
            route(TEXT_PATH) {
                post {
                    textsService.createText()
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
                            post("/share") {
                                textsService.shareText()
                            }
                            delete {
                                textsService.deleteText()
                            }
                        }

                        // roles allowed Owner + Editor
                        rolesAllowed(OWNER, EDITOR) {
                            put {
                                textsService.updateText()
                            }
                        }

                        // roles allowed Owner + Editor + Reader
                        rolesAllowed(OWNER, EDITOR, READER) {
                            get {

                            }
                        }
                    }
                }
            }
        }
    }
}