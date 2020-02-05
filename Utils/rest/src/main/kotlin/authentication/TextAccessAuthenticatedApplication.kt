package authentication

import TextJwtConfig
import TextJwtConfig.TEXT_DETAILS_CLAIM
import TextJwtConfig.TEXT_ROLE_CLAIM
import aurthorization.RoleAuthorization
import aurthorization.RoleAuthorizationException
import com.google.gson.Gson
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.Principal
import io.ktor.auth.authentication
import io.ktor.auth.jwt.jwt
import pojo.TextDetails
import pojo.TextRole

private val gson = Gson()

const val TEXT_ACCESS_AUTH = "textAccessAuth"


fun Application.textAccessAuthenticatedModule() {
    install(Authentication) {
        jwt(TEXT_ACCESS_AUTH) {
            verifier(TextJwtConfig.verifier)
            realm = "ktor.io"
            validate {
                val textDetails: TextDetails = it.payload.getClaim(TEXT_DETAILS_CLAIM).asString().run { gson.fromJson(this, TextDetails::class.java) }
                val textRole: TextRole = it.payload.getClaim(TEXT_ROLE_CLAIM).asString().let(TextRole::valueOf)
                TextPrincipal(textDetails, textRole)
            }
        }
    }
    install(RoleAuthorization) {
        validate { allowedRoles ->
            if (!allowedRoles.contains(textRequest!!.textRole)) {
                throw RoleAuthorizationException()
            }
        }
    }
}

val ApplicationCall.textRequest get() = authentication.principal<TextPrincipal>()


data class TextPrincipal(val textDetails: TextDetails, val textRole: TextRole) : Principal