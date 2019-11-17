package client

import HttpClient


class ServerClient {
    val client = HttpClient()

    companion object {

        val HELLO_PATH : String = "/hello"
    }
}