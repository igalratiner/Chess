package client

import com.google.gson.GsonBuilder
import mu.KLogging
import okhttp3.Headers.Companion.toHeaders
import java.io.IOException
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response

class HttpClient(private val url: String = "https://localhost:5000") {
    private val JSON = "application/json; charset=utf-8".toMediaType()
    private val client = OkHttpClient()
    private val gson = GsonBuilder().setPrettyPrinting().create()

    @Throws(IOException::class)
    fun post(path: String, requestObject: Any, headersMap : Map<String, String>? = null): Response {
        val body = gson.toJson(requestObject).toRequestBody(JSON)
        val request = Request.Builder()
                .url(url + path)
                .addHeaders(headersMap)
                .post(body)
                .build()
        return client.executeRequest(request)
    }

    @Throws(IOException::class)
    fun put(path: String, requestObject: Any, headersMap : Map<String, String>? = null): Response {
        val body = gson.toJson(requestObject).toRequestBody(JSON)
        val request = Request.Builder()
                .url(url + path)
                .addHeaders(headersMap)
                .put(body)
                .build()
        return client.executeRequest(request)
    }

    @Throws(IOException::class)
    fun get(path: String, headersMap : Map<String, String>? = null): Response {
        val requestBuilder = Request.Builder()
                .url(url + path)
                .addHeaders(headersMap)
                .get()
        val request : Request = if (headersMap == null) { requestBuilder.build() } else {
            headersMap.forEach{ (header, value) -> requestBuilder.addHeader(header, value) }
            requestBuilder.build()
        }
        KLogging().logger.info { "my requesrt: $request" }
        return client.executeRequest(request)
    }

    private fun Request.Builder.addHeaders(headersMap: Map<String, String>?): Request.Builder {
        val headers = headersMap?.toHeaders()
        if (headers != null) {
            headers(headers)
        }
        return this
    }

    private fun OkHttpClient.executeRequest(request: Request): Response {
        val response = this.newCall(request).execute()
        if (response.isSuccessful) {
            return response
        } else {
            throw CallFailedException(response.message)
        }
    }
}