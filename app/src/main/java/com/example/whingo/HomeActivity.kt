package com.example.whingo

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import com.example.whingo.Adapter.SliderAdapter
import com.example.whingo.Model.SliderModel
import com.example.whingo.ViewModel.HomeViewModel
import com.example.whingo.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var viewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        initBanners()
    }

    private fun initBanners() {
        binding.progressBarBanner.visibility = View.VISIBLE
        viewModel.banners.observe(this, Observer {
            Log.d("HomeActivity", "Banners received: ${it.size}")
            banners(it)
            binding.progressBarBanner.visibility = View.GONE
        })
        viewModel.loadBanners()
    }

    private fun banners(images: List<SliderModel>) {
        if (images.isEmpty()) {
            Log.d("HomeActivity", "No banners to display")
            return
        }

        val sliderItems = listOf(
            SliderModel(R.drawable.banner1),
            SliderModel(R.drawable.banner2)
        )

        val sliderAdapter = SliderAdapter(sliderItems, binding.viewPagerSlider)
        binding.viewPagerSlider.adapter = sliderAdapter
        binding.viewPagerSlider.clipToPadding = false
        binding.viewPagerSlider.clipChildren = false
        binding.viewPagerSlider.offscreenPageLimit = 3
        binding.viewPagerSlider.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

        val compositePageTransformer = CompositePageTransformer().apply {
            addTransformer(MarginPageTransformer(40))
        }
        binding.viewPagerSlider.setPageTransformer(compositePageTransformer)
        if (images.size > 1) {
            binding.dotIndicator.visibility = View.VISIBLE
            binding.dotIndicator.setViewPager2(binding.viewPagerSlider)
        }
    }
}