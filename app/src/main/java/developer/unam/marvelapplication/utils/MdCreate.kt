package developer.unam.marvelapplication.utils

import java.lang.StringBuilder
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


class MdCreate {
    fun createMd5(message: String): String {
        val MD5 = "MD5"
        try {
            // Create MD5 Hash
            val digest: MessageDigest =MessageDigest.getInstance(MD5)
            digest.update(message.toByteArray())
            val messageDigest: ByteArray = digest.digest()
            // Create Hex String
            val hexString = StringBuilder()
            for (aMessageDigest in messageDigest) {
                var h = Integer.toHexString(0xFF and aMessageDigest.toInt())
                while (h.length < 2) h = "0$h"
                hexString.append(h)
            }
            return hexString.toString()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        return ""
    }
}