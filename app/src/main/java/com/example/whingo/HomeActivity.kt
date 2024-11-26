package com.example.whingo

import android.content.Intent
import android.os.Bundle
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.whingo.databinding.ActivityHomeBinding
import com.google.firebase.firestore.FirebaseFirestore

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private val firestore = FirebaseFirestore.getInstance()
    private val carList = mutableListOf<Car>()
    private lateinit var carAdapter: CarAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        loadCarsFromDatabase()

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { filterByName(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { filterByName(it) }
                return true
            }
        })

        binding.btnFilter.setOnClickListener {
            val minPrice = binding.etMinPrice.text.toString().toDoubleOrNull() ?: 0.0
            val maxPrice = binding.etMaxPrice.text.toString().toDoubleOrNull() ?: Double.MAX_VALUE
            filterByPrice(minPrice, maxPrice)
        }

        binding.btnAddCar.setOnClickListener {
            val intent = Intent(this, RegisterCarActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupRecyclerView() {
        carAdapter = CarAdapter(carList) { car ->
            val intent = Intent(this, CarDetailsActivity::class.java).apply {
                putExtra("CAR_DETAILS", car) // Passando o objeto Car
            }
            startActivity(intent)
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity)
            adapter = carAdapter
        }
    }


    private fun loadCarsFromDatabase() {
       firestore.collection("Carros")
           .get()
           .addOnSuccessListener { result ->
               carList.clear()
               for (document in result) {
                   val modeloDoCarro = document.getString("modeloDoCarro") ?: ""

                   // Tratamento seguro do valor do carro
                   val valorDoCarroString = document.getString("ValorDaLocação") ?: "0.0"
                   val valorDoCarro = valorDoCarroString.toDoubleOrNull() ?: 0.0  // Convertendo a string para Double

                   val fotos = document["Fotos"] as? List<String> ?: listOf()
                   val AnoDoCarro = document.getString("AnoDoCarro") ?: ""
                   val documentId = document.id

                   // Passando a lista 'fotos' para o construtor do carro
                   val car = Car(documentId,modeloDoCarro, valorDoCarro, fotos, AnoDoCarro)  // Corrigido
                   carList.add(car)
               }
               carAdapter.notifyDataSetChanged()
           }
           .addOnFailureListener { e ->
               Toast.makeText(this, "Erro ao carregar carros: ${e.message}", Toast.LENGTH_SHORT).show()
           }

        binding?.btnPerfil?.setOnClickListener {
            val intent = Intent(this, PerfilActivity::class.java)
            startActivity(intent)
        }
   }


    private fun filterByName(query: String) {
        val filteredList = carList.filter { it.name.contains(query, ignoreCase = true) }
        carAdapter.updateList(filteredList)
    }

    private fun filterByPrice(minPrice: Double, maxPrice: Double) {
        val filteredList = carList.filter { it.ValordaLocação in minPrice..maxPrice }
        carAdapter.updateList(filteredList)
    }
}
