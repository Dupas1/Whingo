package com.example.whingo

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.whingo.databinding.ActivityCarDetailBinding
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CarDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCarDetailBinding
    private var startDate: Calendar? = null
    private var endDate: Calendar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCarDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Receber os dados do carro
        val car = intent.getParcelableExtra<Car>("CAR_DETAILS")

        car?.let { car ->
            // Salvar o ID do documento do carro na SharedPreferences
            saveCarDocumentId(this, car.documentId)

            // Exibir os dados do carro na UI
            binding.tvCarModel.text = car.name
            binding.tvCarPrice.text = "R$ %.2f por dia".format(car.ValordaLocação)
            binding.tvCarYear.text = "Ano: ${car.year}"

            // Exibir as imagens do carro (todas as imagens)
            if (car.photos.isNotEmpty()) {
                setupImageGallery(car.photos)
            }

            // Configurar o botão para escolher o intervalo de datas
            binding.btnSelectDate.setOnClickListener {
                showMaterialDateRangePicker { start, end ->
                    startDate = start
                    endDate = end
                    binding.tvStartDate.text = formatDate(start)
                    binding.tvEndDate.text = formatDate(end)
                    updateTotalCost(car.ValordaLocação)

                    // Salvar o período de dias e o custo total nas SharedPreferences
                    saveTotalDays(this, start, end)
                    saveTotalCost(this, car.ValordaLocação)
                }
            }

            // Configurar o clique no botão "Alugar Carro"
            binding.btnRentCar.setOnClickListener {
                navigateToSelectCardActivity(car) // Passar o carro completo
            }
        }
    }

    // Função para exibir o MaterialDatePicker para intervalo de datas
    private fun showMaterialDateRangePicker(onDateSelected: (Calendar, Calendar) -> Unit) {
        val dateRangePicker = MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText("Selecione o intervalo de datas")
            .build()

        dateRangePicker.show(supportFragmentManager, "DATE_RANGE_PICKER")

        dateRangePicker.addOnPositiveButtonClickListener { selection ->
            val startDateInMillis = selection.first
            val endDateInMillis = selection.second

            if (startDateInMillis != null && endDateInMillis != null) {
                val startCalendar = Calendar.getInstance().apply { timeInMillis = startDateInMillis }
                val endCalendar = Calendar.getInstance().apply { timeInMillis = endDateInMillis }

                onDateSelected(startCalendar, endCalendar)
            } else {
                Toast.makeText(this, "Seleção de datas inválida.", Toast.LENGTH_SHORT).show()
            }
        }

        dateRangePicker.addOnNegativeButtonClickListener {
            Toast.makeText(this, "Seleção de datas cancelada.", Toast.LENGTH_SHORT).show()
        }
    }

    // Função para formatar as datas
    private fun formatDate(date: Calendar): String {
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return formatter.format(date.time)
    }

    // Função para atualizar o custo total com base no intervalo de datas
    private fun updateTotalCost(pricePerDay: Double) {
        if (startDate != null && endDate != null) {
            val diffInMillis = endDate!!.timeInMillis - startDate!!.timeInMillis
            val days = (diffInMillis / (1000 * 60 * 60 * 24)).toInt() + 1 // Inclui o último dia
            if (days > 0) {
                val totalCost = days * pricePerDay
                binding.tvTotalCost.text = "Total: R$ %.2f".format(totalCost) // Exibe o valor formatado
            } else {
                Toast.makeText(this, "A data final deve ser após a data inicial.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Função para navegar até a próxima activity (SelectCardActivity)
    private fun navigateToSelectCardActivity(car: Car) {
        if (startDate != null && endDate != null) {
            val diffInMillis = endDate!!.timeInMillis - startDate!!.timeInMillis
            val days = (diffInMillis / (1000 * 60 * 60 * 24)).toInt() + 1
            if (days > 0) {
                val totalCost = days * car.ValordaLocação
                val intent = Intent(this, SelectCardActivity::class.java)
                intent.putExtra("CAR_DETAILS", car)
                intent.putExtra("TOTAL_COST", totalCost)
                intent.putExtra("DAYS", days)
                intent.putExtra("START_DATE", formatDate(startDate!!))
                intent.putExtra("END_DATE", formatDate(endDate!!))
                startActivity(intent)
            } else {
                Toast.makeText(this, "Por favor, selecione datas válidas antes de continuar.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Por favor, selecione as datas antes de continuar.", Toast.LENGTH_SHORT).show()
        }
    }

    // Função para configurar a galeria de imagens do carro
    private fun setupImageGallery(photos: List<String>) {
        val imageAdapter = ImageAdapter(photos)
        binding.recyclerViewImages.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerViewImages.adapter = imageAdapter
    }

    // Função para salvar o ID do documento do carro
    fun saveCarDocumentId(context: Context, carDocumentId: String) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("CarPreferences", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString("CAR_DOCUMENT_ID", carDocumentId)
        editor.apply()
    }

    // Função para salvar os detalhes de aluguel (período e valor total) em SharedPreferences
    private fun saveTotalDays(context: Context, start: Calendar, end: Calendar) {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("CarPreferences", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()

        val diffInMillis = end.timeInMillis - start.timeInMillis
        val days = (diffInMillis / (1000 * 60 * 60 * 24)).toInt() + 1 // Inclui o último dia
        editor.putInt("RENTAL_DAYS", days)
        editor.apply()
    }
    
    private fun saveTotalCost(context: Context, totalCost: Double) {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("CarPreferences", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putFloat("TOTAL_COST", totalCost.toFloat())
        editor.apply()
    }
}
