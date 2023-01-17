package svcs

import java.nio.charset.StandardCharsets
import java.security.MessageDigest

class Sha256Calculator {
    companion object {
        fun hashString(string: String): String = hashBytes(string.toByteArray(StandardCharsets.UTF_8))

        fun hashBytes(bytes: ByteArray): String {
            val digest = MessageDigest.getInstance(VcsConstants.SHA_ALGORITHM);
            val encodedHash = digest.digest(bytes)
            return bytesToHex(encodedHash)
        }

        private fun bytesToHex(hash: ByteArray): String {
            val hexString = StringBuilder(2 * hash.size)
            for (i in hash.indices) {
                val hex = Integer.toHexString(0xff and hash[i].toInt())
                if (hex.length == 1) {
                    hexString.append('0')
                }
                hexString.append(hex)
            }
            return hexString.toString()
        }
    }
}