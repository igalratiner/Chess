package client

import com.google.gson.GsonBuilder

class GameMasterClient(clientUrl: String  = "http://localhost:1571") {
    private val client = HttpClient(clientUrl)
    private val gson = GsonBuilder().setPrettyPrinting().create()


    fun getHelloFromClient(headers: Map<String, String>) : Int {
        return client.get("/helloFromAccount", headers).body?.string()!!.toInt()
    }

    companion object {

    }
}