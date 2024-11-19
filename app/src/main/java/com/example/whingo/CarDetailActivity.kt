package com.example.whingo

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
            binding.tvCarYear.text = "Ano: ${it.year}"  // Assegure-se de que o 'year' esteja no seu objeto Car
            Picasso.get().load(it.imageUrl).into(binding.ivCarImage)

            // Configurar o clique no botão "Alugar Carro"
            binding.btnRentCar.setOnClickListener {
                // Lógica para alugar o carro (Exemplo: mostrar uma mensagem ou abrir outra tela)
            }
        }
    }
}
