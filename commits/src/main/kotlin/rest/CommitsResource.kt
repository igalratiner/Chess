package rest

import TextJwtConfig.TEXT_DETAILS_CLAIM
import TextJwtConfig.TEXT_ROLE_CLAIM
import aurthorization.rolesAllowed
import authentication.TEXT_ACCESS_AUTH
import authentication.textRequest
import com.google.common.hash.Hashing.hmacSha256
import com.google.common.hash.Hashing.hmacSha512
import com.google.gson.Gson
import io.jsonwebtoken.Jwts
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.http.HttpStatusCode
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.WebSocketSession
import io.ktor.http.cio.websocket.readText
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.delete
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.websocket.webSocket
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.selects.selectUnbiased
import mu.KotlinLogging
import org.koin.ktor.ext.get
import pojo.Commit
import pojo.CommitRequest
import pojo.TextDetails
import pojo.TextRole
import pojo.TextRole.READER
import pojo.TextRole.OWNER
import pojo.TextRole.EDITOR
import services.CommitsNotification
import services.CommitsService

@UseExperimental(InternalCoroutinesApi::class)
@ExperimentalCoroutinesApi
fun Route.commits() {
    val commitsService: CommitsService = get()
    val gson = Gson()
    val logger = KotlinLogging.logger {}

    fun Any.toJson(): String {
        return gson.toJson(this)
    }

    suspend fun SendChannel<Frame>.send(commit: Commit) {
        send(Frame.Text(commit.toJson()))
    }

    fun disconnectFromCommits(notificationsChannel: Channel<CommitsNotification>, textHash: String, listenerId: Int) {
        commitsService.unsubscribeCommitsListener(textHash, listenerId)
        notificationsChannel.cancel()
    }

    authenticate(TEXT_ACCESS_AUTH) {


        rolesAllowed(OWNER, EDITOR, READER) {
            get {
                val requestedCommits = call.receive<List<CommitRequest>>()
                call.respond(commitsService.getCommits(call.textRequest!!.textDetails.hash, requestedCommits))

            }

            rolesAllowed(OWNER) {
                post {
                    logger.info { "creation of commits requested" }
                    val textHash = call.textRequest!!.textDetails.hash

                    logger.info { "creation of commits requested for text hash: $textHash" }

                    if (commitsService.createTextCommits(textHash)) {
                        logger.info { "commits of textHash=$textHash were created" }
                        call.respond(HttpStatusCode.Created, "Created")
                    } else {
                        logger.info { "commits of textHash=$textHash already exist" }
                        call.respond(HttpStatusCode.Found, "commits queue exists already")
                    }
                }

                delete {
                    val textHash = call.textRequest!!.textDetails.hash
                    logger.info { "deletion of commits of textHash=$textHash requested" }
                    commitsService.deleteCommits(textHash)
                    call.respond("Text hash=$textHash was deleted")
                }
            }
        }
    }

    webSocket("/{JWT}") { // todo not secured at all validation needed

        val claims = Jwts.parserBuilder()
                .requireIssuer("http://localhost:1581")
                .setSigningKey("cBDMBZAB423Iz0MZopTWBZAB423IzBZAB423IzBZAB423IzBZAB423IzBZAB423IzBZAB423IzBZAB423Iz".toByteArray())
                .build()
                .parseClaimsJws(call.parameters["JWT"]!!)
                .body

        val textHash = claims[TEXT_DETAILS_CLAIM].toString().run { gson.fromJson(this, TextDetails::class.java) }.hash
        val textRole = claims[TEXT_ROLE_CLAIM].toString().let(TextRole::valueOf)

        logger.info { "hash: $textHash and role: $textRole" }

        val websocketSession = this

        val (listenerId, notificationsChannel) = commitsService.connect(textHash)

        commitsService.getCommits(textHash).forEach { outgoing.send(it) }

        try {
            while(true) {
                selectUnbiased<Unit> {
                    notificationsChannel.onReceive {
                        when (it) {
                            is CommitsNotification.NextCommit -> {
                                outgoing.send(it.commit)
                            }
                            is CommitsNotification.IllegalCommit -> {
                                logger.error { "Client sent illegal commit=${it.commit}" }
                            }
                            is CommitsNotification.CommitNotInOrder -> {
                                logger.warn { "Client sent unexpected order commit" }
                            }
                            is CommitsNotification.TextCommitsNotExist -> {
                                logger.error { "Text for the commit is already deleted" }
                                disconnectFromCommits(notificationsChannel, textHash, listenerId)
                                websocketSession.close()
                            }
                        }
                    }
                    incoming.onReceive {
                        if (textRole.writePrivileges()) {
                            val msg = (it as Frame.Text).readText()
                            logger.info { "message came: $msg" }
                            val commit = gson.fromJson(msg, Commit::class.java)
                            logger.info { "message parsed from json : $commit" }
                            commitsService.commitIncomingChannel.send(Triple(textHash, commit, notificationsChannel))
                        }
                    }
                }
            }

        } catch (e: Exception) {
            disconnectFromCommits(notificationsChannel, textHash, listenerId)
            logger.error { "Websocket ended with ${e.cause}" }
        }
    }
}
