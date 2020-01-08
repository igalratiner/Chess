import client.TextsClient
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.Principal
import io.ktor.auth.authentication
import io.ktor.auth.jwt.jwt
import pojo.TextDetails
import pojo.TextPermission

private val textsClient = TextsClient()

const val TEXT_ACCESS_AUTH = "textAccessAuth"


fun Application.textAccessAuthenticatedModule() {
    install(Authentication) {
        jwt(TEXT_ACCESS_AUTH) {
            verifier(TextJwtConfig.verifier)
            realm = "ktor.io"
            validate {
                val textDetails  = it.payload.getClaim("textId").asLong()?.let(textsClient::getTextDetails)
                val textPermission = it.payload.getClaim("textPermission").asString()!!.let(TextPermission::valueOf)
                if (textDetails != null) {
                    TextPrincipal(textDetails, textPermission)
                } else {
                    null
                }
            }
        }
    }
}

val ApplicationCall.textRequest get() = authentication.principal<TextPrincipal>()


data class TextPrincipal(val textDetails: TextDetails, val textPermission: TextPermission): Principal