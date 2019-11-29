package services

import Main
import client.AccountsClient
import org.junit.After
import org.junit.Before
import org.junit.Test
import requests.AccountRequest


class AccountsITest {
    companion object {
        val app = Main()
    }

    @Before
    fun init() {
        app.main()
    }

    @After
    fun stop() {

    }

    @Test
    fun testCreateAccountThroughService() {

        AccountsClient().createAccount(AccountRequest("jony"))
    }
}