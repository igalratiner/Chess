import io.ktor.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import rest.module

class Main {
    fun main() {
        embeddedServer(Netty, 1517, module = Application::module).start()
    }
}
