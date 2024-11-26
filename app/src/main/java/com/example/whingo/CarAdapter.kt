package com.example.whingo

import Car
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
            binding.tvCarName.text = car.modeloDoCarro
            binding.tvCarPrice.text = "R$ ${car.valorDoCarro} por dia"

            if (car.fotos.isNotEmpty()) {
                Picasso.get().load(car.fotos[0]).into(binding.ivCar)
            } else {
                binding.ivCar.setImageResource(android.R.drawable.ic_menu_gallery)
            }

            itemView.setOnClickListener {
                onCarClick(car)
            }
        }
    }

    fun updateList(newList: List<Car>) {
        carList.clear()
        carList.addAll(newList)
        notifyDataSetChanged()
    }
}