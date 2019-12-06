import client.AccessClient
import mu.KotlinLogging
import requests.Credentials
import java.util.concurrent.TimeUnit


fun main(args: Array<String>) {
    val logger = KotlinLogging.logger {}
    val accessClient = AccessClient()
    val username = "mcLovin23"
    val password = "iAmMcLovin23"
    val credentials = Credentials(username, password)
    var sessionToken = accessClient.signup(credentials)
    logger.info { "Signup token received: $sessionToken" }
    while(true) {
        TimeUnit.SECONDS.sleep(1L)
        sessionToken = accessClient.login(credentials)
        logger.info { "login token received: $sessionToken" }
    }
}

