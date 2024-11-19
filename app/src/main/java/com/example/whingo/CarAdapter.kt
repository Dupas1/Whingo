package com.example.whingo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.whingo.databinding.ItemCarBinding
import com.squareup.picasso.Picasso

class CarAdapter(private var carList: MutableList<Car>, private val onCarClick: (Car) -> Unit) : RecyclerView.Adapter<CarAdapter.CarViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarViewHolder {
        // Usa o ViewBinding para inflar o layout
        val binding = ItemCarBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CarViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CarViewHolder, position: Int) {
        val car = carList[position]
        holder.bind(car)
    }

    override fun getItemCount(): Int = carList.size

    inner class CarViewHolder(private val binding: ItemCarBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(car: Car) {
            // Usar os IDs corretos para o layout atual
            binding.tvCarName.text = car.name
            binding.tvCarPrice.text = "R$ ${car.price}"

            // Carregar a imagem do carro usando Picasso
            Picasso.get().load(car.imageUrl).into(binding.ivCar)

            // Configurar o clique no item
            itemView.setOnClickListener {
                onCarClick(car)
            }
        }
    }

    // Método para atualizar a lista após filtros ou outras mudanças
    fun updateList(newList: List<Car>) {
        carList.clear()
        carList.addAll(newList)
        notifyDataSetChanged()
    }
}
