package services

import responses.Session
import client.AccountsClient
import com.google.inject.Inject
import exceptions.*
import requests.AccountRequest

class SigningService @Inject constructor(private val accessService: AccessService) {

    private val accountsClient = AccountsClient()

    fun processSignup(username: String, password: String) : Session {
        validateCredentialsValid(username, password)
        accessService.createUser(username, password)
        accountsClient.createAccount(AccountRequest(username))
        return processLogin(username, password)
    }

    fun processLogin(username: String, password: String) : Session {
        return accessService.getSessionToken(username, password)
    }

    private fun validateCredentialsValid(username: String, password: String) {
        validateUsernameValid(username)
        validatePasswordValid(password)
        validateCredentialsNotUsed(username, password)
    }

    private fun validateCredentialsNotUsed(username: String, password: String) {
        accessService.validateCredentialsNotUsed(username, password)
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