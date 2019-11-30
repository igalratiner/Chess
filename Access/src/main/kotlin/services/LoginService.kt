package services

import com.google.inject.Inject
import dao.SessionDao
import dao.UserCredentialsDao

class LoginService @Inject constructor(userCredentialsDao: UserCredentialsDao, sessionDao: SessionDao) {
}