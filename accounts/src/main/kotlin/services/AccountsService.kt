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
        logger.info { "authentication.getAccount=$account was created" }
        return account
    }

    fun getAccount(username: String) : Account {
        logger.info { "authentication.getAccount was requested by username=$username" }
        val account = accountsDao.getAccountByUsername(username) ?: throw AccountNotExistingException()
        logger.info { "authentication.getAccount=$account was requested by username" }
        return account
    }
}