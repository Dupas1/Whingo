package com.example.whingo

import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.whingo.Adapter.SliderAdapter
import com.example.whingo.Model.BannerModel
import com.example.whingo.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        supportActionBar?.hide() // This line hides the action bar
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initBanners()
    }

    private fun initBanners() {
        val banners = listOf(
            BannerModel(R.drawable.banner1),
            BannerModel(R.drawable.banner2)
        )

        val viewPagerBanners: ViewPager2 = binding.viewPagerBanners
        viewPagerBanners.adapter = SliderAdapter(banners)
        viewPagerBanners.orientation = ViewPager2.ORIENTATION_HORIZONTAL
    }
}