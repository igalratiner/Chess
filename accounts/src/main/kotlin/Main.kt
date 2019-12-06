import io.ktor.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import mu.KLogging
import rest.module


fun main(args: Array<String>) {
    embeddedServer(Netty, 1561, module = Application::module).start()
}