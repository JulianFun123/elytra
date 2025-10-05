package ad.julian.elytra.core.service.auth

import jakarta.annotation.PostConstruct
import org.bouncycastle.asn1.x500.X500Name
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder
import org.bouncycastle.openssl.jcajce.JcaPEMWriter
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.io.BufferedReader
import java.io.BufferedWriter
import java.math.BigInteger
import java.nio.file.Files
import java.nio.file.Path
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.Security
import java.security.cert.X509Certificate
import java.util.*

@Component
class CertKeyManager() {

    @field:Value("\${jwt.cert-folder:./certs}")
    private val certFolder: String? = null

    @field:Value("\${jwt.cert-filename:selfsigned.crt.pem}")
    private val certFilename: String? = null

    @field:Value("\${jwt.key-filename:private_key.pem}")
    private val keyFilename: String? = null

    @field:Value("\${jwt.keysize:2048}")
    private val keySize: Int? = null

    @field:Value("\${jwt.subject:CN=MyApp, O=MyOrg}")
    private val subjectDn: String? = null

    lateinit var privateKey: PrivateKey
    lateinit var certificate: X509Certificate

    @PostConstruct
    fun init() {
        Security.addProvider(org.bouncycastle.jce.provider.BouncyCastleProvider())

        val folderPath = Path.of(certFolder)
        if (!Files.exists(folderPath)) Files.createDirectories(folderPath)

        val certPath = folderPath.resolve(certFilename)
        val keyPath = folderPath.resolve(keyFilename)

        if (!Files.exists(certPath) || !Files.exists(keyPath)) {
            generateAndSave(certPath, keyPath)
        } else {
            loadFromFiles(certPath, keyPath)
        }
    }

    private fun generateAndSave(certPath: Path, keyPath: Path) {
        val kpg = KeyPairGenerator.getInstance("RSA")
        kpg.initialize(keySize!!)
        val kp = kpg.generateKeyPair()

        val now = System.currentTimeMillis()
        val notBefore = Date(now - 60_000)
        val notAfter = Date(now + 10L * 365 * 24 * 60 * 60 * 1000) // 10 years
        val serial = BigInteger.valueOf(now)

        val dnName = X500Name(subjectDn)
        val certBuilder = JcaX509v3CertificateBuilder(
            dnName,
            serial,
            notBefore,
            notAfter,
            dnName,
            kp.public
        )

        val signer = JcaContentSignerBuilder("SHA256withRSA").build(kp.private)
        certificate = JcaX509CertificateConverter().setProvider("BC")
            .getCertificate(certBuilder.build(signer))

        // save private key
        BufferedWriter(Files.newBufferedWriter(keyPath)).use { writer ->
            JcaPEMWriter(writer).use { pemWriter ->
                pemWriter.writeObject(kp.private)
            }
        }

        // save certificate
        BufferedWriter(Files.newBufferedWriter(certPath)).use { writer ->
            JcaPEMWriter(writer).use { pemWriter ->
                pemWriter.writeObject(certificate)
            }
        }

        privateKey = kp.private
    }

    private fun loadFromFiles(certPath: Path, keyPath: Path) {
        // Load certificate
        BufferedReader(Files.newBufferedReader(certPath)).use { reader ->
            org.bouncycastle.openssl.PEMParser(reader).use { parser ->
                val obj = parser.readObject()
                if (obj is org.bouncycastle.cert.X509CertificateHolder) {
                    certificate = JcaX509CertificateConverter().setProvider("BC")
                        .getCertificate(obj)
                } else {
                    throw IllegalStateException("Unexpected certificate type: ${obj?.javaClass}")
                }
            }
        }

        // Load private key
        BufferedReader(Files.newBufferedReader(keyPath)).use { reader ->
            org.bouncycastle.openssl.PEMParser(reader).use { parser ->
                val obj = parser.readObject()
                val converter = org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter().setProvider("BC")
                privateKey = when (obj) {
                    is org.bouncycastle.openssl.PEMKeyPair -> converter.getKeyPair(obj).private
                    is org.bouncycastle.asn1.pkcs.PrivateKeyInfo -> converter.getPrivateKey(obj)
                    else -> throw IllegalStateException("Unexpected key type: ${obj?.javaClass}")
                }
            }
        }
    }
}