import okhttp3.Headers
import java.io.IOException
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class HttpClient {
    private val JSON = "application/json; charset=utf-8".toMediaType()

    private val client = OkHttpClient()

    @Throws(IOException::class)
    fun post(url: String, json: String): String {
        val body = json.toRequestBody(JSON)
        val request = Request.Builder()
                .url(url)
                .post(body)
                .build()
        client.newCall(request).execute().use { response -> return response.body.toString() }
    }

    @Throws(IOException::class)
    fun put(url: String, json: String): String {
        val body = json.toRequestBody(JSON)
        val request = Request.Builder()
                .url(url)
                .put(body)
                .build()
        client.newCall(request).execute().use { response -> return response.body.toString() }
    }

    @Throws(IOException::class)
    fun get(url: String): String {
        val request = Request.Builder()
                .url(url)
                .get()
                .build()
        client.newCall(request).execute().use { response -> return response.body.toString() }
    }
}