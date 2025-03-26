package com.example.whingo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.example.whingo.databinding.ItemImagemBinding

class ImageAdapter(private val photos: List<String>) :
    RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ItemImagemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageUrl = photos[position]
        Picasso.get().load(imageUrl).into(holder.binding.ivCarImage) // Usando Picasso para carregar as imagens
    }

    override fun getItemCount(): Int = photos.size

    inner class ImageViewHolder(val binding: ItemImagemBinding) : RecyclerView.ViewHolder(binding.root)
}
