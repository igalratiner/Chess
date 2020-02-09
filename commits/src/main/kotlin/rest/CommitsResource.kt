package rest

import authentication.textRequest
import com.google.gson.Gson
import com.google.inject.Inject
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readText
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.websocket.webSocket
import mu.KotlinLogging
import org.koin.ktor.ext.inject
import pojo.Commit
import services.CommitsService

@Inject()
fun Route.commits() {
    val logger = KotlinLogging.logger {}
    val commitsService by inject<CommitsService>()
    val deserealizer = Gson()

    post {
        if (commitsService.createTextCommits(call.textRequest!!.textDetails.hash)) {
            call.respond(HttpStatusCode.Created, "Created")
        }
        call.respond(HttpStatusCode.Found, "commits queue exists already")
    }

    webSocket {
        for (frame in incoming) {
            when (frame) {
                is Frame.Text -> {
                    val commit = deserealizer.fromJson(frame.readText(), Commit::class.java)
                    commitsService.insertCommitToQueue(call.textRequest!!.textDetails.hash, commit)
                }
            }
        }
    }
}