import com.auth0.jwt.*
import com.auth0.jwt.algorithms.*
import pojo.TextDetails
import pojo.TextRole
import java.util.*

object TextJwtConfig {

    const val TEXT_HASH_CLAIM = "textHash"
    const val TEXT_ROLE_CLAIM = "textRole"

    private const val secret = "cBDMBZAB423Iz0MZopTW"
    private const val issuer = "http://localhost:1581"
    private const val validityInMs = 36_000_00 * 24 // 24 hours
    private val algorithm = Algorithm.HMAC512(secret)

    val verifier: JWTVerifier = JWT
            .require(algorithm)
            .withIssuer(issuer)
            .build()

    /**
     * Produce a token for this combination of User and Account
     */
    fun makeToken(textDetails: TextDetails, textRole: TextRole): String = JWT.create()
            .withSubject("Authentication")
            .withIssuer(issuer)
            .withClaim(TEXT_HASH_CLAIM, textDetails.hash)
            .withClaim(TEXT_ROLE_CLAIM, textRole.name)
            .withExpiresAt(getExpiration())
            .sign(algorithm)

    /**
     * Calculate the expiration Date based on current time + the given validity
     */
    private fun getExpiration() = Date(System.currentTimeMillis() + validityInMs)

}
