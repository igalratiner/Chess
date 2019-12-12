package services

import exceptions.AccountNotExistingException
import com.google.inject.Inject
import dao.AccountsDao
import mu.KLogging
import requests.AccountRequest
import pojo.Account

class AccountsService @Inject constructor(private val accountsDao: AccountsDao) {
    companion object : KLogging()

    fun createAccount(accountRequest: AccountRequest): Account {
        val account = accountsDao.createAccount(accountRequest.username)
        logger.info { "account=$account was created" }
        return account
    }

    fun getAccount(username: String) : Account {
        logger.info { "account was requested by username=$username" }
        val account : Account = accountsDao.getAccountByUsername(username) ?: throw AccountNotExistingException()
        logger.info { "account=$account was requested by username" }
        return account
    }
}