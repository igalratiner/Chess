import aurthorization.RoleAuthorizationException
import authentication.AuthenticationException
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.features.StatusPages
import io.ktor.gson.gson
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import org.slf4j.event.Level
import java.text.DateFormat

fun Application.baseModule() {
    // Install Ktor features
    install(DefaultHeaders)
    install(CallLogging) {
        level = Level.INFO
    }
    install(ContentNegotiation) {
        gson {
            setDateFormat(DateFormat.LONG)
            setPrettyPrinting()
        }
    }
    install(StatusPages) {
        exception<AuthenticationException> {
            call.respond(HttpStatusCode.Forbidden)
        }
        exception<RoleAuthorizationException> {
            call.respond(HttpStatusCode.Unauthorized)
        }
        exception<Throwable> {
            call.respond(HttpStatusCode.InternalServerError)
        }
    }
}

