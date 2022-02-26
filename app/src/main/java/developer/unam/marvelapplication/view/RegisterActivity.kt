package developer.unam.marvelapplication.view

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import developer.unam.marvelapplication.R
import developer.unam.marvelapplication.databinding.ActivityRegisterBinding
import java.util.*

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        title = "Registro"
    }

    override fun onResume() {
        super.onResume()
        binding.btnRegisterActivity.setOnClickListener {
            val email = binding.etUserOAuth.editText?.text.toString()
            val pass = binding.etPasswordOAuth.editText?.text.toString()

            if (email.isEmpty() || pass.isEmpty()) {
                binding.etUserOAuth.error = if (email.isEmpty())
                    getString(R.string.not_empty) else null
                binding.etPasswordOAuth.error = if (pass.isEmpty())
                    getString(R.string.not_empty) else null
            } else {
                binding.etUserOAuth.error =
                    if (!isValidEmail(email)) getString(R.string.format_email)
                    else null
                binding.etPasswordOAuth.error =
                    if (pass.length <= 5) getString(R.string.menor_length) else null
                if (isValidEmail(email) && pass.length > 5) {
                    try {
                        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, pass)
                            .addOnCompleteListener {
                                if (it.isSuccessful) {
                                    messageUser(
                                        "Bienvenido $email",
                                        DialogInterface.OnClickListener { dialog, _ ->
                                            startActivity(
                                                Intent(this, OAuthActivity::class.java)
                                            )
                                        })
                                } else {
                                    if (it.exception != null) {
                                        val messageAction = it.exception?.message?.toLowerCase(Locale.ROOT)?.contains("email")
                                        Log.e("boolean1", messageAction.toString())
                                        if (messageAction == true) {
                                            messageUser(
                                                it.exception?.message.toString()
                                            ) { _, _ ->
                                                startActivity(
                                                    Intent(this, OAuthActivity::class.java)
                                                )
                                            }
                                        } else {
                                            messageUser(it.exception?.message.toString()) { dialog, _ -> dialog.dismiss() }
                                        }
                                    } else {
                                        messageUser(
                                            "Bienvenido $email",
                                            DialogInterface.OnClickListener { dialog, _ ->
                                                startActivity(
                                                    Intent(this, OAuthActivity::class.java)
                                                )
                                            })
                                    }

                                }
                            }

                    } catch (exception: FirebaseAuthException) {
                        messageUser()
                    }


                }

            }

        }
    }

    private fun messageUser(
        message: String = getString(R.string.error_generic),
        action: DialogInterface.OnClickListener = DialogInterface.OnClickListener { _, _ -> }
    ) {
        val material = MaterialAlertDialogBuilder(this@RegisterActivity)
        material.setTitle(R.string.login)
        material.setMessage(message)
        material.setPositiveButton(android.R.string.ok, action)
        material.create().show()

    }

    private val isValidEmail = { it: String ->
        Patterns.EMAIL_ADDRESS.matcher(it).matches()
    }
}