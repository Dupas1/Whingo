package com.example.whingo

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.whingo.databinding.ActivityForgotPasswordBinding
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {
    private var binding: ActivityForgotPasswordBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.ivVoltar?.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding?.btnEnviarEmail?.setOnClickListener {
            val email = binding?.edEmailL?.text.toString()
            if (email.isNotEmpty()) {
                forgotPassword(email)
                val intent = Intent(this@ForgotPasswordActivity, MainActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(baseContext, "Preencha o campo", Toast.LENGTH_LONG).show()
            }

        }
    }

    private fun forgotPassword(email: String) {
        FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(ContentValues.TAG, "createUserWithEmail:success")
                Toast.makeText(baseContext, "Email de resetar senha enviado para $email.", Toast.LENGTH_LONG).show()

            } else {
                Log.w(ContentValues.TAG, "createUserWithEmail:failure", task.exception)
                Toast.makeText(baseContext, "Erro ao enviar email", Toast.LENGTH_LONG).show()
            }
        }
    }

}
