package com.example.whingo

import Car
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.whingo.databinding.ActivityCarDetailBinding

class CarDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCarDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCarDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Receive the car data
        val car = intent.getParcelableExtra<Car>("CAR_DETAILS")

        car?.let { carDetails ->
            // Display car data in the UI
            binding.tvCarModel.text = carDetails.modeloDoCarro
            binding.tvCarPrice.text = "R$ %.2f por dia".format(carDetails.valorDoCarro)
            binding.tvCarYear.text = "Ano: ${carDetails.anoDoCarro}"

            // Display car images (all images)
            if (carDetails.fotos.isNotEmpty()) {
                setupImageGallery(carDetails.fotos)
            }

            // Set up the "Calculate Total Cost" button click
            binding.btnCalculateTotal.setOnClickListener {
                calculateTotalCost(carDetails.valorDoCarro)
            }

            // Set up the "Rent Car" button click
            binding.btnRentCar.setOnClickListener {
                navigateToSelectCardActivity(carDetails)
            }
        }
    }

    private fun setupImageGallery(fotos: List<String>) {
        // Implementation of setupImageGallery
    }

    private fun calculateTotalCost(pricePerDay: Double) {
        val days = binding.etDays.text.toString().toIntOrNull()
        if (days != null && days > 0) {
            val totalCost = days * pricePerDay
            binding.tvTotalCost.text = "Total: R$ %.2f".format(totalCost)
        } else {
            Toast.makeText(this, "Por favor, insira um número válido de dias.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToSelectCardActivity(car: Car) {
        val days = binding.etDays.text.toString().toIntOrNull()
        if (days != null && days > 0) {
            val totalCost = days * car.valorDoCarro
            val intent = Intent(this, SelectCardActivity::class.java).apply {
                putExtra("CAR_DETAILS", car)
                putExtra("TOTAL_COST", totalCost)
                putExtra("DAYS", days)
            }
            startActivity(intent)
        } else {
            Toast.makeText(this, "Por favor, insira um número válido de dias.", Toast.LENGTH_SHORT).show()
        }
    }
}