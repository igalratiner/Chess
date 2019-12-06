package services

import com.google.inject.Inject
import dao.AccountsDao
import mu.KLogging
import requests.AccountRequest
import pojo.Account

class AccountsService @Inject constructor(val accountsDao: AccountsDao) {
    companion object : KLogging()

    fun sayHello() : String = "hello from accounts"

    fun createAccount(accountRequest: AccountRequest): Account {
        val account = accountsDao.createAccount(accountRequest.username)
        logger.info { "account=$account was created" }
        return account
    }
}