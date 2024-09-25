package com.example.whingo

import android.content.Intent
import android.nfc.Tag
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.whingo.databinding.ActivityRegisterCarBinding
import com.google.firebase.auth.FirebaseAuth

class RegisterCarActivity : AppCompatActivity() {

    private var binding: ActivityRegisterCarBinding? = null
    private var auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityRegisterCarBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.btnRegisterCar?.setOnClickListener {
            val carModel = binding?.etCarModel?.text.toString().toUpperCase()
            val carColor = binding?.etCarColor?.text.toString()
            val carYear = binding?.etCarYear?.text.toString()
            val carPlate = binding?.etCarPlate?.text.toString()

            if (carModel.isNotEmpty() &&
                carYear.isNotEmpty() &&
                carColor.isNotEmpty() &&
                carPlate.isNotEmpty()) {
                    if (isValidBrazilianPlate(carPlate)){


                    }else{
                        Log.e(TAG ,"Placa inválida")
                    }
            }else{
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            }

        }

        binding?.btnFoto?.setOnClickListener {
            takePhoto()
        }

        binding?.btnAlbum?.setOnClickListener {
            selectImageInAlbum()
        }

    }

    fun isValidBrazilianPlate(plate: String): Boolean {
        // Regex para o padrão antigo de placas (3 letras + 4 números)
        val oldPattern = "^[A-Z]{3}[0-9]{4}$".toRegex()

        // Regex para o padrão Mercosul (3 letras + 1 número + 1 letra + 2 números)
        val mercosulPattern = "^[A-Z]{3}[0-9]{1}[A-Z]{1}[0-9]{2}$".toRegex()

        // Verifica se a placa corresponde a algum dos padrões
        return plate.matches(oldPattern) || plate.matches(mercosulPattern)
    }

    fun selectImageInAlbum() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, REQUEST_SELECT_IMAGE_IN_ALBUM)
        }
    }
    fun takePhoto() {
        val intent1 = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent1.resolveActivity(packageManager) != null) {
            startActivityForResult(intent1, REQUEST_TAKE_PHOTO)
        }
    }
    companion object {
        private val REQUEST_TAKE_PHOTO = 0
        private val REQUEST_SELECT_IMAGE_IN_ALBUM = 1
        private var TAG ="RegisterCar"
    }


}