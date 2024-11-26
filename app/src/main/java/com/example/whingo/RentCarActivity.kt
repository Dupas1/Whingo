package com.example.whingo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.whingo.databinding.ActivityRentCarBinding

class RentCarActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRentCarBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRentCarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Receber os dados do carro e do usuário
        val car = intent.getParcelableExtra<Car>("CAR_DETAILS")
        val user = intent.getParcelableExtra<User>("USER_DETAILS")

        car?.let { car ->
            // Exibir os dados do carro na UI
            binding.tvCarName.text = car.name
            binding.tvCarPrice.text = "R$ %.2f por dia".format(car.ValordaLocação)
            binding.tvCarYear.text = "Ano: ${car.year}"

            // Exibir as imagens do carro
            if (car.photos.isNotEmpty()) {
                setupImageGallery(car.photos)
            }
        }

        
    }

    private fun setupImageGallery(photos: List<String>) {
        val imageAdapter = ImageAdapter(photos)
        binding.recyclerViewImages.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerViewImages.adapter = imageAdapter
    }
}