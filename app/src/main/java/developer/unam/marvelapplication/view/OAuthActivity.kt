package developer.unam.marvelapplication.view

import android.content.ContentValues.TAG
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.core.app.ActivityCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import developer.unam.marvelapplication.R
import developer.unam.marvelapplication.databinding.ActivityOauthBinding
import developer.unam.marvelapplication.utils.ShareUtil

class OAuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOauthBinding
    private val REQUEST_LOGIN_GOOGLE = 100
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOauthBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        auth = Firebase.auth
        if (auth.currentUser != null)
            if (ShareUtil(this).getRemember())
                startActivity(Intent(this,MainActivity::class.java))
            else
                auth.signOut()
    }

    override fun onResume() {
        super.onResume()
        binding.btnRegistroOAuht.setOnClickListener {
            startActivity(Intent(this,RegisterActivity::class.java))
        }

        binding.btnSignGoogleOAuth.setOnClickListener {

            val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()

            googleSignInClient = GoogleSignIn.getClient(this, options)
            googleSignInClient.signOut()
            startActivityForResult(googleSignInClient.signInIntent, REQUEST_LOGIN_GOOGLE)
        }

        binding.btnLoginOAuht.setOnClickListener {
            val user = binding.etUserOAuth.editText?.text.toString()
            val password = binding.etPasswordOAuth.editText?.text.toString()
            if (user.isEmpty() || password.isEmpty()) {
                if (password.isEmpty()) {
                    binding.etPasswordOAuth.error = getString(R.string.not_empty)
                } else {
                    binding.etPasswordOAuth.error = null
                }

                if (user.isEmpty()) {
                    binding.etUserOAuth.error = getString(R.string.not_empty)
                } else {
                    binding.etUserOAuth.error = null
                }
            } else {
                val check = isValidEmail(user) && password.length > 5
                if (check) {
                    binding.etUserOAuth.error = null
                    binding.etPasswordOAuth.error = null
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(user, password)
                        .addOnCompleteListener {
                            Log.e("exception", Gson().toJson(it.exception))
                            val check = it.exception == null
                            if (check) {
                                if (it.isSuccessful) {
                                    val material = MaterialAlertDialogBuilder(this@OAuthActivity)
                                    material.setTitle(R.string.login)
                                    material.setMessage("¿Quieres que la app te recuerde?")
                                    material.setPositiveButton(android.R.string.ok) { dialog, _ ->
                                        dialog.dismiss()
                                        ShareUtil(this).setRemember(true)
                                        val intent = Intent(this, MainActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }
                                    material.setNegativeButton("No") { dialog, _ ->
                                        ShareUtil(this).setRemember(false)
                                        dialog.dismiss()
                                        val intent = Intent(this, MainActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }
                                    material.create().show()
                                } else {
                                    val material = MaterialAlertDialogBuilder(this@OAuthActivity)
                                    material.setTitle(R.string.login)
                                    material.setMessage(R.string.credentials_not)
                                    material.setNeutralButton(android.R.string.ok) { dialog, _ -> dialog.dismiss() }
                                    material.create().show()

                                }
                            } else {
                                val material = MaterialAlertDialogBuilder(this@OAuthActivity)
                                material.setTitle(R.string.login)
                                material.setMessage(it.exception?.message)
                                material.setNeutralButton(android.R.string.ok) { dialog, _ -> dialog.dismiss() }
                                material.create().show()
                            }
                        }
                } else {
                    if (!isValidEmail(user))
                        binding.etUserOAuth.error = getString(R.string.format_email)
                    else
                        binding.etUserOAuth.error = null
                    if (password.length < 6)
                        binding.etPasswordOAuth.error = getString(R.string.menor_length)
                    else
                        binding.etPasswordOAuth.error = null
                }
            }


        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_LOGIN_GOOGLE) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken)

            } catch (e: ApiException) {

            }
        }
    }

    val isValidEmail = { it: String ->
        Patterns.EMAIL_ADDRESS.matcher(it).matches()
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    val material = MaterialAlertDialogBuilder(this@OAuthActivity)
                    material.setTitle(R.string.login)
                    material.setMessage("¿Quieres que la app te recuerde?")
                    material.setPositiveButton(android.R.string.ok) { dialog, _ ->
                        dialog.dismiss()
                        ShareUtil(this).setRemember(true)
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    material.setNegativeButton("No") { dialog, _ ->
                        ShareUtil(this).setRemember(false)
                        dialog.dismiss()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    material.create().show()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    val material = MaterialAlertDialogBuilder(this@OAuthActivity)
                    material.setTitle(R.string.login)
                    material.setMessage(R.string.error_generic)
                    material.setNeutralButton(android.R.string.ok) { dialog, _ -> dialog.dismiss() }
                    material.create().show()

                }
            }

    }

}