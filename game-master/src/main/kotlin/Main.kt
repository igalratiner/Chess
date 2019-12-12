import io.ktor.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty


fun main(args: Array<String>) {
    embeddedServer(Netty, 1571, module = Application::module).start()
}