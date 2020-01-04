package client

import com.google.gson.GsonBuilder
import requests.AccountRequest
import pojo.Account


class AccountsClient(clientUrl: String  = "http://localhost:1561") {
    private val client = HttpClient(clientUrl)
    private val gson = GsonBuilder().setPrettyPrinting().create()

    fun createAccount(accountRequest: AccountRequest) : Account {
        return gson.fromJson(client.post(ACCOUNT_PATH, accountRequest).body?.string(), Account::class.java)
    }

    fun getAccount(username: String) : Account {
        return gson.fromJson(client.get("$USERNAME_PATH/$username").body?.string(), Account::class.java)
    }

    companion object {
        const val ACCOUNT_PATH : String = "/account"
        const val USERNAME_PATH : String = "/username"
    }
}