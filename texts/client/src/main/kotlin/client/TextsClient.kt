package client

import com.google.gson.GsonBuilder
import mu.KLogging
import pojo.TextDetails

class TextsClient(clientUrl: String  = "http://localhost:1581") {
    private val client = HttpClient(clientUrl)
    private val gson = GsonBuilder().setPrettyPrinting().create()

    companion object: KLogging() {
        const val TEXT_PROVISION = "/text-provision"
        const val TEXT_PATH = "/text"
        const val TEXTS = "/texts"
        const val ACCOUNT = "/account"
        const val TEXT_AUTH_TOKEN = "/text-auth"
        const val TEXT_DETAILS = "/details"
    }

    fun getTextDetails(textHash: String): TextDetails {
        return gson.fromJson(client.get("$TEXT_PATH/$textHash$TEXT_DETAILS").body?.string(), TextDetails::class.java)
    }

//    fun getTextAuthenticationForProvision(textProvision: String): String {
//        return client.get(TEXTS + TEXT_PROVISION + textProvision).body?.string()!!
//    }
//
//    fun getTextAuthenticationForAccount(textHash: String): String {
//        return client.get(TEXTS + TEXT_PROVISION + textProvision).body?.string()!!
//    }
//
//    fun getAccountTexts()
}