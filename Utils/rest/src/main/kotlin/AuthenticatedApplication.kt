import client.AccessClient
import com.google.gson.Gson
import io.ktor.application.*

import io.ktor.sessions.*
import io.ktor.util.AttributeKey
import pojo.Account
import responses.Session


private val accountAttributeKey: AttributeKey<Account> = AttributeKey("account")

fun Application.authenticatedModule() {
    baseModule()
    install(Sessions) {
        header<Session>("SESSION_TOKEN") {
            serializer = GsonSessionSerializer(Session::class.java)
        }
    }
    intercept(ApplicationCallPipeline.Call) {
        val session: Session = call.sessions.get()
                ?: throw RuntimeException("no valid session token specified in path")
        application.log.info("SessionToken from GameMaster: $session")
        val account = AccessClient().getSessionAccount(session.token)
        application.log.info("account received in GameMaster: $account")
        call.attributes.put(accountAttributeKey, account)
    }

}

val ApplicationCall.account get(): Account? {
    return if (attributes.contains(accountAttributeKey)) attributes[accountAttributeKey] else null
}

class GsonSessionSerializer(
        private val type: java.lang.reflect.Type, private val gson: Gson = Gson(), configure: Gson.() -> Unit = {}
) : SessionSerializer {
    init {
        configure(gson)
    }

    override fun serialize(session: Any): String = gson.toJson(session)
    override fun deserialize(text: String): Any = gson.fromJson(text, type)
}


