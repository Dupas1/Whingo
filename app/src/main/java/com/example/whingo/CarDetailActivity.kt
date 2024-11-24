package com.example.whingo

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.whingo.databinding.ActivityCarDetailBinding
import com.squareup.picasso.Picasso

class CarDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCarDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCarDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Receber os dados do carro
        val car = intent.getParcelableExtra<Car>("CAR_DETAILS")

        car?.let {
            // Exibir os dados do carro na UI
            binding.tvCarModel.text = it.name
            binding.tvCarPrice.text = "R$ ${it.price}"
            binding.tvCarYear.text = "Ano: ${it.year}"

            // Exibir as imagens do carro (todas as imagens)
            if (it.photos.isNotEmpty()) {
                setupImageGallery(it.photos)
            }

            // Configurar o clique no botão "Alugar Carro"
            binding.btnRentCar.setOnClickListener {
                // Lógica para alugar o carro (Exemplo: mostrar uma mensagem ou abrir outra tela)
            }
        }
    }

    private fun setupImageGallery(photos: List<String>) {
        // Usar Picasso para carregar as imagens no RecyclerView
        val imageAdapter = ImageAdapter(photos)
        binding.recyclerViewImages.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerViewImages.adapter = imageAdapter
    }
}
