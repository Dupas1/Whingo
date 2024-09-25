package com.example.whingo

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.whingo.databinding.ActivityUserBinding

class UserActivity : AppCompatActivity() {

    private var binding: ActivityUserBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        binding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.btnRegisterCar?.setOnClickListener {
            val intent = Intent(this, RegisterCarActivity::class.java)
            startActivity(intent)
        }
    }
}