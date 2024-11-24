package com.example.whingo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.whingo.databinding.ItemCarBinding
import com.squareup.picasso.Picasso

class CarAdapter(
    private var carList: MutableList<Car>,
    private val onCarClick: (Car) -> Unit
) : RecyclerView.Adapter<CarAdapter.CarViewHolder>() {

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
            // Configurar o nome e o preço do carro
            binding.tvCarName.text = car.name
            binding.tvCarPrice.text = "R$ ${car.price}"

            // Verificar se há uma imagem para carregar
            // Dentro do CarAdapter
            if (car.photos.isNotEmpty()) {
                Picasso.get().load(car.photos[0]).into(binding.ivCar)
            } else {
                // Usar o ícone de galeria padrão do Android como placeholder
                binding.ivCar.setImageResource(android.R.drawable.ic_menu_gallery)
            }


            // Configurar o clique no item
            itemView.setOnClickListener {
                onCarClick(car)
            }
        }
    }

    // Método para atualizar a lista após filtros ou mudanças
    fun updateList(newList: List<Car>) {
        carList.clear()
        carList.addAll(newList)
        notifyDataSetChanged()
    }
}
