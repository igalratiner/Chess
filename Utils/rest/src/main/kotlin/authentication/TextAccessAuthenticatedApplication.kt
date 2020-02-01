package authentication

import TextJwtConfig
import TextJwtConfig.TEXT_HASH_CLAIM
import TextJwtConfig.TEXT_ROLE_CLAIM
import aurthorization.RoleAuthorization
import aurthorization.RoleAuthorizationException
import client.TextsClient
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.Principal
import io.ktor.auth.authentication
import io.ktor.auth.jwt.jwt
import io.ktor.features.StatusPages
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import okhttp3.internal.http2.Http2Reader.Companion.logger
import pojo.TextDetails
import pojo.TextRole

private val textsClient = TextsClient()

const val TEXT_ACCESS_AUTH = "textAccessAuth"


fun Application.textAccessAuthenticatedModule() {
    install(Authentication) {
        jwt(TEXT_ACCESS_AUTH) {
            verifier(TextJwtConfig.verifier)
            realm = "ktor.io"
            validate {
                val textDetails = it.payload.getClaim(TEXT_HASH_CLAIM).asString().let(textsClient::getTextDetails)
                val textRole = it.payload.getClaim(TEXT_ROLE_CLAIM).asString().let(TextRole::valueOf)
                TextPrincipal(textDetails, textRole)
            }
        }
    }
    install(RoleAuthorization) {
        validate { allowedRoles ->
            logger.info(allowedRoles.toString())
            logger.info(textRequest!!.textDetails.toString())
            logger.info(textRequest!!.textRole.name)
            if (!allowedRoles.contains(textRequest!!.textRole)) {
                throw RoleAuthorizationException()
            }
        }
    }
}

val ApplicationCall.textRequest get() = authentication.principal<TextPrincipal>()


data class TextPrincipal(val textDetails: TextDetails, val textRole: TextRole) : Principal