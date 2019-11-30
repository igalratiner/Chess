package services

import com.google.inject.Inject
import pojos.SessionToken
import requests.Credentials
import responses.Session
import java.time.Instant

class SignupService @Inject constructor(private val loginService: LoginService) {

    fun processSignup(credentials: Credentials) : Session {
        validateCredentialsValid(credentials)
        val encryptedPassword = encryptPassword(credentials.password)
        return Session(SessionToken(6, ""))
    }

    private fun validateCredentialsValid(credentials: Credentials) {
        validateUsernameValid(credentials.username)
        validatePasswordValid(credentials.password)
        validateCredentialsNotUsed(credentials)
    }

    private fun encryptPassword(password: String) : String {
        return password
    }

    private fun validateCredentialsNotUsed(credentials: Credentials) {

    }

    private fun validateUsernameValid(username: String) {

    }

    private fun validatePasswordValid(password: String) {

    }
}