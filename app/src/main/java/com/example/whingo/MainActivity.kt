package com.example.whingo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.whingo.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private var db = FirebaseFirestore.getInstance()
    private var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Acessa o SharedPreferences diretamente aqui, sem usar função
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

        // Verifica se o usuário já está logado antes de carregar a interface
        if (isLoggedIn == true) {
            // Se o usuário já estiver logado, redireciona para HomeActivity
            startActivity(Intent(this, HomeActivity::class.java))
            finish() // Fecha a MainActivity para evitar voltar
            return
        }

        // Carrega a interface somente se o usuário não estiver logado
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        // Inicializa o FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // Botão de login
        binding?.btnLogin?.setOnClickListener {
            val email = binding?.etEmail?.text.toString()
            val password = binding?.etPassword?.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                signInWithEmailAndPassword(email, password)
            } else {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            }
        }

        // Ações para criar conta e recuperar senha
        binding?.tvCreateAccount?.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        binding?.tvEsqueciSenha?.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }
    }

    // Função para realizar o login e armazenar o estado de login
    private fun signInWithEmailAndPassword(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                if (user != null) {
                    // Armazena o estado de login no SharedPreferences
                    val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putBoolean("isLoggedIn", true)
                    editor.apply()

                    // Login bem-sucedido, redireciona para a HomeActivity
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    finish() // Fecha a MainActivity para evitar voltar
                }
            } else {
                Toast.makeText(baseContext, "Falha na Autenticação.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}
