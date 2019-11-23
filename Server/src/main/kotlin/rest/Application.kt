package rest

import com.google.inject.Guice
import di.ServerModule
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.gson.gson
import org.slf4j.event.Level
import java.text.DateFormat

fun Application.module() {
    Guice.createInjector(ServerModule(this))

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
}