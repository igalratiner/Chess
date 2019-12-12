package client


import HttpClient
import com.google.gson.GsonBuilder
import pojo.Account
import responses.Session
import requests.Credentials


class AccessClient(clientUrl: String  = "http://localhost:1551") {
    private val client = HttpClient(clientUrl)
    private val gson = GsonBuilder().setPrettyPrinting().create()


    fun login(loginCredentials: Credentials) : Session {
        return gson.fromJson(client.post(LOGIN_PATH, loginCredentials).body?.string(), Session::class.java)
    }

    fun signup(signupCredentials: Credentials) : Session {
        val response = client.post(SIGNUP_PATH, signupCredentials)
        return gson.fromJson(response.body?.string(), Session::class.java)
    }

    fun getSessionAccount(sessionKey: String) : Account {
        val response = client.get("$ACCOUNT_PATH/$sessionKey")
        return gson.fromJson(response.body?.string(), Account::class.java)
    }

    companion object {
        const val LOGIN_PATH : String = "/login"
        const val SIGNUP_PATH : String = "/signup"
        const val ACCOUNT_PATH: String = "/session-account"
    }
}