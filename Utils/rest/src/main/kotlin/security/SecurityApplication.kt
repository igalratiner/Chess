package security

import io.ktor.application.Application
import io.ktor.application.ApplicationCallPipeline

fun Application.securityModule() {
    intercept(ApplicationCallPipeline.Call) {
//        if (call.receive<Any>().toString().containsUnsecuredChars()) {
//            throw SecurityException()
//        }
    }
}