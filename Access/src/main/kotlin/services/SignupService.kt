package services

import client.AccountsClient
import com.google.inject.Inject
import dao.UserCredentialsDao
import exceptions.*
import pojos.SessionToken
import requests.AccountRequest

class SignupService @Inject constructor(private val loginService: LoginService, private val userCredentialsDao: UserCredentialsDao) {
    private val accountsClient = AccountsClient()

    fun processSignup(username: String, password: String) : SessionToken {
        validateCredentialsValid(username, password)
        val encryptedPassword = encryptPassword(password)
        userCredentialsDao.createUserCredentials(username, encryptedPassword)
        accountsClient.createAccount(AccountRequest(username))
        return loginService.login(username, encryptedPassword)
    }

    private fun validateCredentialsValid(username: String, password: String) {
        validateUsernameValid(username)
        validatePasswordValid(password)
        validateCredentialsNotUsed(username, password)
    }

    private fun encryptPassword(password: String) : String {
        return password
    }

    private fun validateCredentialsNotUsed(username: String, password: String) {
        if (userCredentialsDao.getUserId(username, password) != null) {
            throw UserAlreadyExistsException()
        }
    }

    private fun validateUsernameValid(username: String) {
        if (username.length < 4) {
            throw UsernameTooShortException()
        } else if (username.length > 15) {
            throw UsernameTooShortException()
        } else if (!("""[a-zA-z0-9]*""".toRegex().matches(username))) {
            throw InvalidLiteralInUsernameException()
        }
    }

    private fun validatePasswordValid(password: String) {
        if (password.length < 4) {
            throw PasswordTooShortException()
        } else if (password.length > 15) {
            throw PasswordTooLongException()
        } else if (!("""[a-zA-z0-9]*""".toRegex().matches(password))) {
            throw InvalidLiteralInPasswordException()
        }
    }
}