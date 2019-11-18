package client


import Gson.Gson
import HttpClient


class ServerClient {
    val client = HttpClient()
    val gson = Gson.getGson()

    fun sayHelloToMyself(greetings : String) : String {
        return client.post("http://localhost:1517$HELLO_PATH", gson.toJson(greetings))
    }

    companion object {

        val HELLO_PATH : String = "/hello"
    }
}