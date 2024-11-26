package com.example.whingo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.whingo.databinding.ActivityPerfilBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso

class PerfilActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityPerfilBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        supportActionBar?.hide()
        binding = ActivityPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtendo o ID do usuário logado
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "userId123"

        // Referência ao nó do usuário no Firebase Realtime Database
        val databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId)

        // Recuperar os dados do usuário
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Obter os dados
                    val userName = snapshot.child("name").getValue(String::class.java) ?: "N/A"
                    val userPhone = snapshot.child("phone").getValue(String::class.java) ?: "N/A"
                    val isCarRented = snapshot.child("isCarRented").getValue(Boolean::class.java) ?: false
                    val photoUrl = snapshot.child("photoUrl").getValue(String::class.java) ?: ""

                    // Atualizar a interface
                    binding.tvUserName.text = userName
                    binding.tvPhoneNumber.text = "Telefone: $userPhone"
                    binding.tvRentalStatus.text = "Carro Alugado Ativo: ${if (isCarRented) "SIM" else "NÃO"}"

                    // Carregar foto ou mostrar placeholder
                    if (photoUrl.isNotEmpty()) {
                        Picasso.get().load(photoUrl).placeholder(android.R.drawable.ic_menu_camera)
                            .into(binding.ivUserPhoto)
                    } else {
                        binding.ivUserPhoto.setImageResource(android.R.drawable.ic_menu_camera)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@PerfilActivity, "Erro ao carregar dados: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })

        // Clique no botão para editar o perfil
        binding.btnEditProfile.setOnClickListener {
            Toast.makeText(this, "Editar Perfil em breve!", Toast.LENGTH_SHORT).show()
        }

        binding?.btnSelectCard?.setOnClickListener {
            val intent = Intent(this, SelectCardActivity::class.java)
            startActivity(intent)
        }

        binding.btnLogout.setOnClickListener {
            // Atualiza o SharedPreferences para marcar como deslogado
            val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putBoolean("isLoggedIn", false)
            editor.apply()

            // Redireciona para MainActivity
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish() // Finaliza a atividade atual para que o usuário não possa voltar
        }
    }
}