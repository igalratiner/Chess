import client.AccountsClient
import mu.KotlinLogging
import requests.AccountRequest
import java.util.concurrent.TimeUnit


fun main(args: Array<String>) {
    val logger = KotlinLogging.logger {}
//    if (args.isNotEmpty()) {
//        logger.info { args }
//        initAccessDB(args[1])
//    } else {
//        initAccessDB()
//    }
//    embeddedServer(Netty, 1517, module = Application::module).start()
    val accountsClient = if (args.isNotEmpty()) {
        logger.info { args[0] }
        AccountsClient(args[0])
    } else {
        logger.info ("No args")
        AccountsClient()
    }
    var i = 0
    while(true) {
        val accountRequest = AccountRequest("accountNumber$i")
        TimeUnit.SECONDS.sleep(1L)
        logger.info("message to send from Access: $accountRequest")
        logger.info(accountsClient.createAccount(accountRequest).toString())
        ++i
    }
}