package developer.unam.marvelapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import developer.unam.marvelapplication.utils.MdCreate
import java.sql.Timestamp

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val publicKey = "48d2901781db31818938d1517c19700b"
        val privateKey = "435e550bd1e38ea6b922a2ae784a200caeb4613f"
        val ts = Timestamp(System.currentTimeMillis())
        Log.e("ts", "time ${ts.toString()}")
        Log.e("private", privateKey)
        Log.e("public", publicKey)
        Log.e("md5", "md ${MdCreate().createMd5("$ts$privateKey$publicKey")}")
    }
}