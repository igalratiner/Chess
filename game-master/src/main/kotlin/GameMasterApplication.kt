import com.google.gson.Gson
import com.google.inject.Guice
import di.GameMasterModule
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.gson.gson
import io.ktor.sessions.SessionSerializer
import io.ktor.sessions.Sessions
import io.ktor.sessions.header
import org.slf4j.event.Level
import responses.Session
import java.text.DateFormat

fun Application.module() {
    Guice.createInjector(GameMasterModule(this))

    // Install Ktor features
    install(DefaultHeaders)
    install(CallLogging) {
        level = Level.INFO
    }

    install(Sessions) {
        header<Session>("SESSION_TOKEN") {
            serializer = GsonSessionSerializer(Session::class.java)
        }
    }

    install(ContentNegotiation) {
        gson {
            setDateFormat(DateFormat.LONG)
            setPrettyPrinting()
        }
    }
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