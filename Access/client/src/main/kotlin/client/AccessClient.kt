package client


import HttpClient
import com.google.gson.GsonBuilder
import requests.Credentials
import responses.Session


class AccessClient(clientUrl: String  = "http://localhost:1517") {
    private val client = HttpClient(clientUrl)
    private val gson = GsonBuilder().setPrettyPrinting().create()

    fun sayHelloToMyself(greetings : Any) : Bla {
        return gson.fromJson(client.post(HELLO_PATH, greetings).body?.string(), Bla::class.java)
    }

    fun login(loginCredentials: Credentials) : Session {
        return gson.fromJson(client.post(LOGIN_PATH, loginCredentials).body?.string(), Session::class.java)
    }

    fun signup(signupCredentials: Credentials) : Session {
        return gson.fromJson(client.post(SIGNUP_PATH, signupCredentials).body?.string(), Session::class.java)
    }

    companion object {
        const val HELLO_PATH : String = "/hello"
        const val LOGIN_PATH : String = "/login"
        const val SIGNUP_PATH : String = "/signup"
    }
}