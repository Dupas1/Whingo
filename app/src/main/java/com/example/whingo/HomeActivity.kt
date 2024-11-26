package com.example.whingo

import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.whingo.Adapter.SliderAdapter
import com.example.whingo.Model.BannerModel
import com.example.whingo.databinding.ActivityHomeBinding
import com.google.firebase.firestore.FirebaseFirestore

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private val firestore = FirebaseFirestore.getInstance()
    private val carList = mutableListOf<Car>()
    private lateinit var carAdapter: CarAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        supportActionBar?.hide()
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initBanners()
        setupRecyclerView()
        loadCarsFromDatabase()
        setupButtonNavigation()

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

        binding.btnAddCar.setOnClickListener {
            val intent = Intent(this, RegisterCarActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initBanners() {
        val banners = listOf(
            BannerModel(R.drawable.banner1),
            BannerModel(R.drawable.banner2)
        )

        val viewPagerBanners: ViewPager2 = binding.viewPagerBanners
        viewPagerBanners.adapter = SliderAdapter(banners)
        viewPagerBanners.orientation = ViewPager2.ORIENTATION_HORIZONTAL
    }

    private fun setupRecyclerView() {
        carAdapter = CarAdapter(carList) { car ->
            val intent = Intent(this, CarDetailActivity::class.java).apply {
                putExtra("CAR_DETAILS", car)
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
                    val valorDoCarroString = document.getString("ValorDaLocação") ?: "0.0"
                    val valorDoCarro = valorDoCarroString.toDoubleOrNull() ?: 0.0
                    val fotos = document["Fotos"] as? List<String> ?: listOf()
                    val anoDoCarro = document.getString("AnoDoCarro") ?: ""
                    val documentId = document.id

                    val car = Car(documentId, modeloDoCarro, valorDoCarro, fotos, anoDoCarro)
                    carList.add(car)
                }
                carAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao carregar carros: ${e.message}", Toast.LENGTH_SHORT).show()
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

    private fun setupButtonNavigation() {
        binding.cartoes.setOnClickListener {
            val intent = Intent(this, SelectCardActivity::class.java)
            startActivity(intent)
        }

        binding.btnPerfil.setOnClickListener {
            val intent = Intent(this, PerfilActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        setupButtonNavigation()
    }
}