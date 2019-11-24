import client.Bla
import client.ServerClient
import mu.KotlinLogging
import java.util.concurrent.TimeUnit


fun main(args: Array<String>) {
    val logger = KotlinLogging.logger {}
    val serverClient: ServerClient
    serverClient = if (args.isNotEmpty()) {
        logger.info { args }
        ServerClient(args[0])
    } else {
        ServerClient()
    }
    val messageToSend = Bla("Engine", "hello from Engine")
    while(true) {
        TimeUnit.SECONDS.sleep(1L)
        logger.info("message to send from Engine: $messageToSend")
        logger.info(serverClient.sayHelloToMyself(messageToSend).toString())
    }
}

