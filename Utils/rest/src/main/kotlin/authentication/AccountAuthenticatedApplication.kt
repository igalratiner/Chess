package authentication

import authentication.exceptions.SessionAuthenticationException
import client.AccessClient
import com.google.gson.Gson
import io.ktor.application.*
import io.ktor.sessions.*
import io.ktor.util.AttributeKey
import pojo.Account
import responses.Session


private val accountAttributeKey: AttributeKey<Account> = AttributeKey("authentication.getAccount")
private val accessClient = AccessClient()

fun Application.accountAuthenticatedModule() {
    install(Sessions) {
        header<Session>("SESSION_TOKEN") {
            serializer = GsonSessionSerializer(Session::class.java)
        }
    }
    intercept(ApplicationCallPipeline.Call) {
        try {
            val session: Session? = call.sessions.get()
            if (session != null) {
                val account = accessClient.getSessionAccount(session.token)
                call.attributes.put(accountAttributeKey, account)
            }
        } catch (e: Exception) {
            throw SessionAuthenticationException()
        }
    }
}

val ApplicationCall.account
    get(): Account? {
        return if (attributes.contains(accountAttributeKey)) attributes[accountAttributeKey] else null
    }

class GsonSessionSerializer(
        private val type: java.lang.reflect.Type,
        private val gson: Gson = Gson(),
        configure: Gson.() -> Unit = {}
) : SessionSerializer {
    init {
        configure(gson)
    }

    override fun serialize(session: Any): String = gson.toJson(session)
    override fun deserialize(text: String): Any = gson.fromJson(text, type)
}


