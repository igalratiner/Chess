package services

import requests.AccountRequest
import responses.Account

class AccountsService {
    fun sayHello() : String = "hello from Server"

    fun createAccount(accountRequest: AccountRequest): Account {
        return Account()
    }
}