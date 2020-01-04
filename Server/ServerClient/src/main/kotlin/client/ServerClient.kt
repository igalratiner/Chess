package client

import com.google.gson.GsonBuilder

class ServerClient(clientUrl: String  = "http://localhost:1517") {
    private val client = HttpClient(clientUrl)
    private val gson = GsonBuilder().setPrettyPrinting().create()

    fun sayHelloToMyself(greetings : Any) : Bla {
        return gson.fromJson(client.post(HELLO_PATH, greetings).body?.string(), Bla::class.java)
    }

    companion object {
        const val HELLO_PATH : String = "/hello"
    }
}