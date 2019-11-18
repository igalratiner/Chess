package client


import HttpClient
import com.google.gson.GsonBuilder


class ServerClient {
    val client = HttpClient("http://localhost:1517")
    val gson = GsonBuilder().setPrettyPrinting().create()

    fun sayHelloToMyself(greetings : Any) : Bla {
        return gson.fromJson(client.post(HELLO_PATH, greetings).body?.string(), Bla::class.java)
    }

    companion object {

        val HELLO_PATH : String = "/hello"
    }
}