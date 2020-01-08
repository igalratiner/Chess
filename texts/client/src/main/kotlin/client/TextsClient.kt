package client

import mu.KLogging
import pojo.TextDetails

class TextsClient {

    companion object: KLogging() {
        const val TEXT_PATH = "/text"
    }

    fun getTextDetails(textId: Long) : TextDetails {
        return TextDetails(textId,"d", "f")
    }
}