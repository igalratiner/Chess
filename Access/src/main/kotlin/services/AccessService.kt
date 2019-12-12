package services

import responses.Session
import client.AccountsClient
import com.google.inject.Inject
import dao.SessionDao
import dao.UserCredentialsDao
import exceptions.UserAlreadyExistsException
import exceptions.UserNotExistingException
import md5
import pojo.Account

class AccessService @Inject constructor(private val userCredentialsDao: UserCredentialsDao, 
                                        private val sessionDao : SessionDao,
                                        private val accountsClient: AccountsClient) {

    fun getSessionToken(username: String, password: String) : Session {
        val encryptedPassword = encryptPassword(password)
        val userId = userCredentialsDao.getUserId(username, encryptedPassword)
        if (userId != null) {
            return sessionDao.getSessionForUserId(userId)
        } else {
            throw UserNotExistingException()
        }
    }

    fun createUser(username: String, password: String) : Int {
        val encryptedPassword = encryptPassword(password)
        return userCredentialsDao.createUserCredentials(username, encryptedPassword)
    }

    fun validateCredentialsNotUsed(username: String, password: String) {
        val encryptedPassword = encryptPassword(password)
        if (userCredentialsDao.getUserId(username, encryptedPassword) != null) {
            throw UserAlreadyExistsException()
        }
    }
    
    fun getAccountFromSessionToken(sessionToken: String) : Account {
        val userId = sessionDao.getUserForSessionKey(sessionToken) ?: throw UserNotExistingException()
        val username = userCredentialsDao.getUsername(userId)
        return accountsClient.getAccount(username)
    }
    
    private fun encryptPassword(password: String) : String {
        return password.md5()
    }
}