package ad.julian.elytra.core.service.auth

import ad.julian.elytra.core.exceptions.UnauthorizedException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*

@Service
class JwtService(
    private val certKeyManager: CertKeyManager
) {

    @Value("\${jwt.token-valid-seconds:3600}")
    private val tokenValidSeconds: Long? = null

    fun createToken(subject: String, claims: Map<String, Any> = emptyMap(), doExpire: Boolean = false): String {
        val pk = certKeyManager.privateKey
        val now = Instant.now()
        val builder = Jwts.builder()
            .setClaims(claims)
            .setSubject(subject)
            .setIssuedAt(Date.from(now))
            .signWith(pk, SignatureAlgorithm.RS256)

        if (doExpire) {
            builder.setExpiration(Date.from(now.plusSeconds(tokenValidSeconds!!)))
        }

        return builder.compact()
    }

    fun validateToken(token: String): Boolean {
        return try {
            val claims = Jwts.parserBuilder()
                .setSigningKey(certKeyManager.certificate.publicKey)
                .build()
                .parseClaimsJws(token)
            true
        } catch (ex: Exception) {
            throw UnauthorizedException()
        }
    }

    fun getClaims(token: String) = Jwts.parserBuilder()
        .setSigningKey(certKeyManager.certificate.publicKey)
        .build()
        .parseClaimsJws(token)
        .body
}