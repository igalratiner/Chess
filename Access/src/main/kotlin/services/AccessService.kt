package services

import authentication.SessionAuthenticationException
import client.AccountsClient
import com.google.inject.Inject
import crypto.MD5_LENGTH
import crypto.md5
import dao.SessionDao
import dao.UserCredentialsDao
import exceptions.InvalidSessionTokenException
import exceptions.UserAlreadyExistsException
import exceptions.UserNotExistingException
import pojo.Account
import responses.Session
import security.checkHash

class AccessService @Inject constructor(private val userCredentialsDao: UserCredentialsDao,
                                        private val sessionDao: SessionDao,
                                        private val accountsClient: AccountsClient) {

    fun getSessionToken(username: String, password: String): Session {
        val encryptedPassword = encryptPassword(password)
        val userId = userCredentialsDao.getUserId(username, encryptedPassword)
        if (userId != null) {
            return sessionDao.getSessionForUserId(userId)
        } else {
            throw UserNotExistingException()
        }
    }

    fun createUser(username: String, password: String): Int {
        val encryptedPassword = encryptPassword(password)
        return userCredentialsDao.createUserCredentials(username, encryptedPassword)
    }

    fun validateCredentialsNotUsed(username: String, password: String) {
        val encryptedPassword = encryptPassword(password)
        if (userCredentialsDao.getUserId(username, encryptedPassword) != null) {
            throw UserAlreadyExistsException()
        }
    }

    fun getAccountFromSessionToken(sessionToken: String): Account {
        try {
            validateSessionToken(sessionToken)
            val userId = sessionDao.getUserForSessionKey(sessionToken) ?: throw UserNotExistingException()
            val username = userCredentialsDao.getUsername(userId)
            return accountsClient.getAccount(username)
        } catch (e: Exception) {
            throw SessionAuthenticationException()
        }
    }

    private fun encryptPassword(password: String): String {
        return password.md5()
    }

    private fun validateSessionToken(sessionToken: String) {
        if (!checkHash(sessionToken, MD5_LENGTH)) {
            throw InvalidSessionTokenException()
        }
    }
}