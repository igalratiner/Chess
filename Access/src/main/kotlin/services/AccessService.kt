package services

import com.google.inject.Inject
import dao.SessionDao
import dao.UserCredentialsDao
import exceptions.UserAlreadyExistsException
import exceptions.UserNotExistingException
import md5
import pojos.SessionToken

class AccessService @Inject constructor(private val userCredentialsDao: UserCredentialsDao, private val sessionDao : SessionDao) {

    fun getSessionToken(username: String, password: String) : SessionToken {
        val encryptedPassword = encryptPassword(password)
        val userId = userCredentialsDao.getUserId(username, encryptedPassword)
        if (userId != null) {
            return sessionDao.getUserSession(userId)
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

    private fun encryptPassword(password: String) : String {
        return password.md5()
    }
}