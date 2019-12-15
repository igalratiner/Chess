import client.AccessClient
import client.GameMasterClient
import com.google.gson.GsonBuilder
import mu.KotlinLogging
import requests.Credentials
import java.util.*
import java.util.concurrent.TimeUnit


fun main(args: Array<String>) {
    val logger = KotlinLogging.logger {}
    val accessClient = AccessClient()
    val gson = GsonBuilder().create()

    val charPool : List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
    val username = (1..8)
            .map { kotlin.random.Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("")
    val password = (1..8)
            .map { kotlin.random.Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("")
    val credentials = Credentials(username, password)
    var session = accessClient.signup(credentials)
    val client = GameMasterClient()
    logger.info { "Signup token received: $session" }
    while(true) {
        TimeUnit.SECONDS.sleep(1L)
        session = accessClient.login(credentials)
        logger.info { "login token received: $session" }
        client.getHelloFromClient(mapOf("SESSION_TOKEN" to gson.toJson(session)))
    }
}

