import io.ktor.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import mu.KotlinLogging
import rest.module


fun main(args: Array<String>) {
    val logger = KotlinLogging.logger {}
    if (args.isNotEmpty()) {
        logger.info { args }
        initAccountsDB(args[1])
    } else {
        initAccountsDB()
    }
    embeddedServer(Netty, 1517, module = Application::module).start()
}