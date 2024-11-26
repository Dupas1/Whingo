package com.example.whingo

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.whingo.Model.BannerModel
import com.example.whingo.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initBanners()
    }

    private fun initBanners() {
        binding.progressBarBanner.visibility = View.VISIBLE

        val banners = listOf(
            BannerModel(R.drawable.banner1),
            BannerModel(R.drawable.banner2)
        )

        displayBanners(banners)
        binding.progressBarBanner.visibility = View.GONE
    }

    private fun displayBanners(banners: List<BannerModel>) {
        banners.forEach { banner ->
            val imageView = ImageView(this)
            imageView.setImageResource(banner.drawableResId)
            binding.bannerContainer.addView(imageView)
        }
    }
}