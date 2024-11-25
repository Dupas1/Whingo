package com.example.whingo

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.whingo.databinding.ActivityCarDetailBinding

class CarDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCarDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCarDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Receber os dados do carro
        val car = intent.getParcelableExtra<Car>("CAR_DETAILS")

        car?.let { car ->
            // Exibir os dados do carro na UI
            binding.tvCarModel.text = car.name
            binding.tvCarPrice.text = "R$ %.2f por dia".format(car.ValordaLocação) // Exibe com duas casas decimais
            binding.tvCarYear.text = "Ano: ${car.year}"

            // Exibir as imagens do carro (todas as imagens)
            if (car.photos.isNotEmpty()) {
                setupImageGallery(car.photos)
            }

            // Configurar o clique no botão "Calcular Valor Total"
            binding.btnCalculateTotal.setOnClickListener {
                calculateTotalCost(car.ValordaLocação) // Passar o ValordaLocação corretamente
            }

            // Configurar o clique no botão "Alugar Carro"
            binding.btnRentCar.setOnClickListener {
                navigateToSelectCardActivity(car) // Passar o carro completo
            }
        }
    }

    private fun calculateTotalCost(pricePerDay: Double) {
        val days = binding.etDays.text.toString().toIntOrNull()
        if (days != null && days > 0) {
            val totalCost = days * pricePerDay
            binding.tvTotalCost.text = "Total: R$ %.2f".format(totalCost) // Exibe o valor formatado
        } else {
            Toast.makeText(this, "Por favor, insira um número válido de dias.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToSelectCardActivity(car: Car) {
        val days = binding.etDays.text.toString().toIntOrNull()
        if (days != null && days > 0) {
            val totalCost = days * car.ValordaLocação
            val intent = Intent(this, SelectCardActivity::class.java)
            intent.putExtra("CAR_DETAILS", car)
            intent.putExtra("TOTAL_COST", totalCost)
            intent.putExtra("DAYS", days)
            startActivity(intent)
        } else {
            Toast.makeText(this, "Por favor, insira um número válido de dias antes de continuar.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupImageGallery(photos: List<String>) {
        val imageAdapter = ImageAdapter(photos)
        binding.recyclerViewImages.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerViewImages.adapter = imageAdapter
    }
}
