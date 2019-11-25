package client


import HttpClient
import com.google.gson.GsonBuilder
import requests.AccountRequest
import responses.Account


class AccountsClient(clientUrl: String  = "http://localhost:1517") {
    private val client = HttpClient(clientUrl)
    private val gson = GsonBuilder().setPrettyPrinting().create()

    fun sayHelloToMyself(greetings : Any) : Bla {
        return gson.fromJson(client.post(HELLO_PATH, greetings).body?.string(), Bla::class.java)
    }

    fun createAccount(accountRequest: AccountRequest) : Account {
        return gson.fromJson(client.post(ACCOUNT_PATH, accountRequest).body?.string(), Account::class.java)
    }

    companion object {
        const val HELLO_PATH : String = "/hello"
        const val ACCOUNT_PATH : String = "/account"
    }
}