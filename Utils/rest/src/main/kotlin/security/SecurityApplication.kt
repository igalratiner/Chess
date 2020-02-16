package security

import io.ktor.application.Application
import io.ktor.application.ApplicationCallPipeline
import io.ktor.application.call
import io.ktor.request.receive

fun Application.securityModule() {
    intercept(ApplicationCallPipeline.Call) {
        if (call.receive<Any>().toString().containsUnsecuredChars()) {
            throw SecurityException()
        }
    }
}