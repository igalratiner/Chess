package pojos

import java.time.Instant

data class SessionToken(val userId: Int, val token: String, val createAt: Instant)