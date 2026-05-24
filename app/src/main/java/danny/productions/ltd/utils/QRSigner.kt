package danny.productions.ltd.utils

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object QRSigner {

    private const val ALGORITHM = "HmacSHA256"

    fun sign(data: String, secretKey: String): String {
        val mac = Mac.getInstance(ALGORITHM)
        val keySpec = SecretKeySpec(secretKey.toByteArray(), ALGORITHM)
        mac.init(keySpec)
        return mac.doFinal(data.toByteArray()).toHex()
    }

    fun verify(data: String, signature: String, secretKey: String): Boolean {
        val expected = sign(data, secretKey)
        return expected == signature
    }

    private fun ByteArray.toHex(): String = joinToString("") { "%02x".format(it) }
}
