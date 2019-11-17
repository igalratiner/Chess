package client

import HttpClient


class ServerClient {
    val yo = HttpClient()

    companion object {

        val HELLO_PATH : String = "/hello"
    }
}