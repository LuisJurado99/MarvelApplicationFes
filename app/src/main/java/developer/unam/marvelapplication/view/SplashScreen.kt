package developer.unam.marvelapplication.view

import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import developer.unam.marvelapplication.R
import developer.unam.marvelapplication.utils.ShareUtil
import kotlin.Exception

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        Handler().postDelayed({
            try {
                val remember = ShareUtil(this).getRemember()
                if (remember)
                    startActivity(Intent(this, MainActivity::class.java),ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
                else
                    startActivity(Intent(this, OAuthActivity::class.java),ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
            } catch (e: Exception) {
                startActivity(Intent(this, OAuthActivity::class.java),ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
            }
        }, 1500)


    }
}