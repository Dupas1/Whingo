package com.example.whingo

import PhotosAdapter
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.whingo.databinding.ActivityRegisterCarBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class RegisterCarActivity : AppCompatActivity() {

    private var binding: ActivityRegisterCarBinding? = null
    private var auth = FirebaseAuth.getInstance()
    private var db = FirebaseFirestore.getInstance()

    private val selectedImagesUris = mutableListOf<Uri>()
    private lateinit var photosAdapter: PhotosAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterCarBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.rvCarPhotos?.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        photosAdapter = PhotosAdapter(selectedImagesUris)
        binding?.rvCarPhotos?.adapter = photosAdapter

        binding?.etCarPlate?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.length ?: 0 > 7) {
                    binding?.etCarPlate?.setText(s?.substring(0, 7))
                    binding?.etCarPlate?.setSelection(7) // Move o cursor para o final
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding?.btnRegisterCar?.setOnClickListener {
            val carModel = binding?.etCarModel?.text.toString().toUpperCase()
            val carColor = binding?.etCarColor?.text.toString()
            val carYear = binding?.etCarYear?.text.toString()
            val carPlate = binding?.etCarPlate?.text.toString()
            val carValueString = binding?.etCarValue?.text.toString()
            val phone = binding?.etPhoneNumber?.text.toString()

            if (carModel.isNotEmpty() && carYear.isNotEmpty() && carColor.isNotEmpty() && carPlate.isNotEmpty() && carValueString.isNotEmpty()) {
                if (carPlate.length <= 7) {
                    if (selectedImagesUris.size == 4) {
                        saveCarWithPhotos(carModel, carColor, carYear, carPlate, carValueString, phone)  // Passando carValueString como String
                    } else {
                        Toast.makeText(this, "Por favor, selecione exatamente 4 fotos.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "A placa deve ter no máximo 7 caracteres.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Preencha todos os campos.", Toast.LENGTH_SHORT).show()
            }
        }



        binding?.btnAlbum?.setOnClickListener {
            selectImagesInAlbum()
        }
    }

    private fun saveCarWithPhotos(
        carModel: String,
        carColor: String,
        carYear: String,
        carPlate: String,
        carValue: String,
        phone: String
    ) {
        val user = auth.currentUser
        if (user != null) {
            savePhotosToFirebase(carPlate) { photoUrls ->
                saveCarToFirestore(carModel, carColor, carYear, carPlate, carValue, phone , photoUrls)
            }
        } else {
            Log.w(TAG, "SaveCar:failure")
        }
    }

    private fun savePhotosToFirebase(carPlate: String, callback: (List<String>) -> Unit) {
        val storage = FirebaseStorage.getInstance().reference
        val photoUrls = mutableListOf<String>()

        selectedImagesUris.forEachIndexed { index, uri ->
            val photoRef = storage.child("car_photos/$carPlate/photo_$index.jpg")
            photoRef.putFile(uri)
                .addOnSuccessListener {
                    photoRef.downloadUrl.addOnSuccessListener { uri ->
                        photoUrls.add(uri.toString())
                        if (photoUrls.size == selectedImagesUris.size) {
                            callback(photoUrls)
                        }
                    }
                }
                .addOnFailureListener {
                    Log.e(TAG, "Error uploading photo: $it")
                }
        }
    }

    private fun saveCarToFirestore(
        carModel: String,
        carColor: String,
        carYear: String,
        carPlate: String,
        carValueString: String,
        phone: String,
        photoUrls: List<String>
    ) {
        val userId = auth.currentUser?.uid ?: return
        val car = hashMapOf(
            "userId" to userId,
            "modeloDoCarro" to carModel,
            "CorDoCarro" to carColor,
            "AnoDoCarro" to carYear,
            "PlacaDoCarro" to carPlate,
            "ValorDaLocação" to carValueString,
            "Telefone" to phone,
            "Fotos" to photoUrls
        )

        db.collection("Carros")
            .add(car)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                Toast.makeText(this, "Carro registrado com sucesso", Toast.LENGTH_SHORT).show()

                // Redireciona para a HomeActivity após o registro bem-sucedido
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()  // Fecha a RegisterCarActivity
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
                Toast.makeText(this, "Erro ao registrar o carro", Toast.LENGTH_SHORT).show()
            }
    }



    fun selectImagesInAlbum() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            type = "image/*"
        }
        startActivityForResult(intent, REQUEST_SELECT_IMAGE_IN_ALBUM)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_SELECT_IMAGE_IN_ALBUM && resultCode == RESULT_OK) {
            data?.clipData?.let { clipData ->
                selectedImagesUris.clear() // Garante que apenas 4 imagens sejam selecionadas
                for (i in 0 until clipData.itemCount.coerceAtMost(4)) {
                    val imageUri = clipData.getItemAt(i).uri
                    if (!selectedImagesUris.contains(imageUri)) {
                        selectedImagesUris.add(imageUri)
                    }
                }
            } ?: data?.data?.let { uri ->
                if (selectedImagesUris.size < 4 && !selectedImagesUris.contains(uri)) {
                    selectedImagesUris.add(uri)
                }
            }
            photosAdapter.notifyDataSetChanged()
        }
    }

    companion object {
        private const val REQUEST_SELECT_IMAGE_IN_ALBUM = 1
        private const val TAG = "RegisterCar"
    }
}
