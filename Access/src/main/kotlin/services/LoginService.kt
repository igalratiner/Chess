package services

import com.google.inject.Inject
import dao.SessionDao
import dao.UserCredentialsDao
import exceptions.UserNotExistingException
import pojos.SessionToken

class LoginService @Inject constructor(private val userCredentialsDao: UserCredentialsDao, private val sessionDao: SessionDao) {

    fun login(username: String, password: String) :  SessionToken {
        val userId = userCredentialsDao.getUserId(username, password)
        if (userId != null) {
            return sessionDao.getUserSession(userId)
        } else {
            throw UserNotExistingException()
        }
    }
}