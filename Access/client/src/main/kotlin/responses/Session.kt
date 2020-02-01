package responses

import java.time.Instant

data class Session(val token: String, val createAt: Long)