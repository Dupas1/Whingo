package com.example.whingo

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.whingo.databinding.ActivityPerfilBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso

class PerfilActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityPerfilBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                } else {
                    Toast.makeText(this@PerfilActivity, "Usuário não encontrado!", Toast.LENGTH_SHORT).show()
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
    }
}
