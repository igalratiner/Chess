package client


import HttpClient
import com.google.gson.GsonBuilder
import pojos.SessionToken
import requests.Credentials
import responses.Session


class AccessClient(clientUrl: String  = "http://localhost:1551") {
    private val client = HttpClient(clientUrl)
    private val gson = GsonBuilder().setPrettyPrinting().create()

    fun sayHelloToMyself(greetings : Any) : Bla {
        return gson.fromJson(client.post(HELLO_PATH, greetings).body?.string(), Bla::class.java)
    }

    fun login(loginCredentials: Credentials) : SessionToken {
        return gson.fromJson(client.post(LOGIN_PATH, loginCredentials).body?.string(), SessionToken::class.java)
    }

    fun signup(signupCredentials: Credentials) : SessionToken {
        val response = client.post(SIGNUP_PATH, signupCredentials)
        return gson.fromJson(response.body?.string(), SessionToken::class.java)
    }

    companion object {
        const val HELLO_PATH : String = "/hello"
        const val LOGIN_PATH : String = "/login"
        const val SIGNUP_PATH : String = "/signup"
    }
}