package com.example.whingo

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.whingo.databinding.ActivityFinishRentBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import android.content.Intent

class FinishRentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFinishRentBinding
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        // Configuração para tela cheia e sem barra de título
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        supportActionBar?.hide()

        super.onCreate(savedInstanceState)
        binding = ActivityFinishRentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Recuperar o ID do carro a partir das SharedPreferences
        val carDocumentId = getCarDocumentId(this)
        if (carDocumentId.isNullOrEmpty()) {
            showError("Erro: Nenhum carro selecionado.")
            return
        }

        Log.e("carDocumentId", carDocumentId)

        // Buscar informações do carro no Firestore usando o carDocumentId
        fetchCarData(carDocumentId)

        // Recuperar informações sobre o período de locação e custo total
        val rentalDays = getTotalDays(this)
        val totalCost = getTotalCost(this)

        // Atualizar os TextView com as informações recuperadas
        binding.tvRentalPeriod.text = "Período: $rentalDays dias"
        binding.tvTotalCost.text = "Custo Total: R$ %.2f".format(totalCost)

        // Configurar o botão para finalizar o aluguel e retornar à HomeActivity
        binding.btnFinishRent.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish() // Finaliza a FinishRentActivity
        }
    }

    private fun fetchCarData(carDocumentId: String) {
        // Referência ao documento do carro no Firestore
        firestore.collection("Carros").document(carDocumentId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // Obter os dados do carro
                    val carName = document.getString("modeloDoCarro") ?: "N/A"
                    val carPrice = document.getString("ValorDaLocação") ?: "0.00"
                    val carYear = document.getString("AnoDoCarro") ?: "N/A"
                    val carPhotos = document["Fotos"] as? List<String> ?: listOf()
                    val userId = document.getString("userId") ?: ""  // Pega o userId do carro
                    Log.e("userId", userId)

                    // Exibir as informações do carro
                    binding.tvCarModel.text = "Modelo: $carName"
                    binding.tvCarPrice.text = "Preço por dia: R$ %.2f".format(carPrice.toDouble())
                    binding.tvCarYear.text = "Ano: $carYear"
                    if (carPhotos.isNotEmpty()) {
                        Picasso.get().load(carPhotos.first()).into(binding.ivCarPhoto)
                    }

                    // Buscar informações do dono do carro com o userId
                    if (userId.isNotEmpty()) {
                        fetchOwnerData(userId)
                    } else {
                        showError("Erro: ID do proprietário não encontrado.")
                    }
                } else {
                    showError("Erro: Dados do carro não encontrados.")
                }
            }
            .addOnFailureListener { exception ->
                showError("Erro ao carregar os dados do carro: ${exception.message}")
            }
    }

    private fun fetchOwnerData(userId: String) {
        // Referência à coleção "pessoas" no Firestore
        val documentRef = firestore.collection("pessoas").document(userId)

        // Buscar o documento diretamente pelo ID
        documentRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // Obter os dados do proprietário
                    val userName = document.getString("name") ?: "N/A"
                    val userPhone = document.getString("cel") ?: "N/A"
                    val userEmail = document.getString("email") ?: "N/A"
                    val userCpf = document.getString("cpf") ?: "N/A"

                    // Exibir as informações do proprietário
                    binding.tvUserName.text = "Nome: $userName"
                    binding.tvUserPhone.text = "Telefone: $userPhone"
                    binding.tvUserEmail.text = "E-mail: $userEmail"
                    binding.tvUserCpf.text = "CPF: $userCpf"
                } else {
                    showError("Erro: Dados do proprietário não encontrados.")
                }
            }
            .addOnFailureListener { exception ->
                showError("Erro ao carregar os dados do proprietário: ${exception.message}")
            }
    }

    private fun getCarDocumentId(context: Context): String? {
        // Puxa o ID do carro armazenado nas SharedPreferences
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("CarPreferences", Context.MODE_PRIVATE)
        return sharedPreferences.getString("CAR_DOCUMENT_ID", null)
    }

    private fun showError(message: String) {
        // Exibe mensagem de erro e finaliza a Activity
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        finish()
    }

    private fun getTotalDays(context: Context): Int {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("CarPreferences", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("RENTAL_DAYS", 0)
    }

    private fun getTotalCost(context: Context): Float {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("CarPreferences", Context.MODE_PRIVATE)
        return sharedPreferences.getFloat("TOTAL_COST", 0.0f)
    }
}
