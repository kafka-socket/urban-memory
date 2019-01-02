package urban.memory

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.interfaces.DecodedJWT
import org.slf4j.Logger
import org.slf4j.LoggerFactory


class Auth(token: String) {
    private val logger: Logger = LoggerFactory.getLogger(Auth::class.java)

    private val algorithm = Algorithm.HMAC256("your-256-bit-secret")
    private val verifier: JWTVerifier = JWT.require(algorithm).build()
    private val decoded: DecodedJWT? = verifyToken(token)

    fun isOk(): Boolean {
        return decoded != null
    }

    fun user(): String {
        return decoded?.getClaim("user_uid")?.asString().orEmpty()
    }

    private fun verifyToken(token: String) : DecodedJWT? {
        return try {
            verifier.verify(token)
        }
        catch (e: JWTVerificationException) {
            logger.error("Invalid token [$token] $e")
            null
        }
    }
}
