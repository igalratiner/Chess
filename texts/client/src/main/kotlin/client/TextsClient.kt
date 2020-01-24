package client

import mu.KLogging
import pojo.TextDetails

class TextsClient {

    companion object: KLogging() {
        const val TEXT_PROVISION = "/text-provision"
        const val TEXT_PATH = "/text"
        const val TEXTS = "/texts"
    }

    fun getTextDetails(textHash: String) : TextDetails {
        return TextDetails("d", "f")
    }
}