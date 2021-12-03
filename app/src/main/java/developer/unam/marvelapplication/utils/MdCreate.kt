package developer.unam.marvelapplication.utils

import android.app.Activity
import android.util.Log
import developer.unam.marvelapplication.R
import java.lang.StringBuilder
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.sql.Timestamp


class MdCreate(private val activity: Activity) {
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

    fun addParams():Map<String,String>{
        val private = activity.getString(R.string.private_key)
        val public = activity.getString(R.string.public_key)
        val ts = Timestamp(System.currentTimeMillis())

        val map = hashMapOf<String,String>()
        map["apikey"] = public
        map["ts"] = ts.toString()
        map["hash"] = createMd5("$ts$private$public")
        Log.e("params", "params $map")
        return  map
    }
}