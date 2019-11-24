package pojos

import java.time.Instant

data class SessionToken(val sessionToken: String, val createAt: Instant)