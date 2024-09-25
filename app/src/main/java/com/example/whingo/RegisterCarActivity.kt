package com.example.whingo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.whingo.databinding.ActivityRegisterCarBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterCarActivity : AppCompatActivity() {

    private var binding: ActivityRegisterCarBinding? = null
    private var auth = FirebaseAuth.getInstance()
    private var db = FirebaseFirestore.getInstance()

    private var isPhotoTaken: Boolean = false // Variável para verificar se a foto foi tirada

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

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

                if (isPhotoTaken) { // Verifica se a foto foi tirada ou escolhida
                    createCar(carModel, carColor, carYear, carPlate)
                } else {
                    Toast.makeText(this, "Por favor, tire uma foto ou escolha uma imagem.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            }
        }

        binding?.btnFoto?.setOnClickListener {
            cameraProviderResult.launch(android.Manifest.permission.CAMERA)
        }

        binding?.btnAlbum?.setOnClickListener {
            selectImageInAlbum()
        }
    }

    private val cameraProviderResult =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                openCameraPreview()
            } else {
                Toast.makeText(baseContext, "VOCE NÃO DEU PERMISSÃO PARA A CAMERA", Toast.LENGTH_LONG).show()
            }
        }

    private fun openCameraPreview() {
        val camera = Intent(this, CameraPreviewActivity::class.java)
        startActivity(camera)
    }

    private fun createCar(carModel: String, carColor: String, carYear: String, carPlate: String) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            saveCarToFirestore(carModel, carColor, carYear, carPlate)
            Toast.makeText(this, "Carro registrado com sucesso", Toast.LENGTH_SHORT).show()
            Log.d(TAG, "Carro registrado com sucesso")
        } else {
            Log.w(TAG, "SaveCar:failure")
        }
    }

    // Função para salvar o carro no Firestore
    private fun saveCarToFirestore(carModel: String, carColor: String, carYear: String, carPlate: String) {
        val userId = auth.currentUser?.uid ?: return // Garante que o userId não é nulo
        val car = hashMapOf(
            "userId" to userId,
            "modeloDoCarro" to carModel,
            "CorDoCarro" to carColor,
            "AnoDoCarro" to carYear,
            "PlacaDoCarro" to carPlate,
            "FotoId" to getPhotoIdFromPreferences() // Adiciona o ID da foto das Shared Preferences
        )

        db.collection("Carros")
            .add(car)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                // Salvar o ID do documento no SharedPreferences
                saveDocumentId(documentReference.id)
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }

    // Função para salvar o ID do documento no SharedPreferences
    private fun saveDocumentId(documentId: String) {
        val sharedPreferences = getSharedPreferences("carId", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("documentId", documentId)
        editor.apply() // Salva as alterações
    }

    // Função para salvar o ID da foto no SharedPreferences
    fun savePhotoIdInPreferences(fotoId: String) {
        val sharedPreferences = getSharedPreferences("carId", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("fotoId", fotoId)
        editor.apply() // Salva as alterações
        isPhotoTaken = true // Atualiza a variável para indicar que uma foto foi tirada
    }

    // Função para recuperar o ID da foto das SharedPreferences
    fun getPhotoIdFromPreferences(): String? {
        val sharedPreferences = getSharedPreferences("carId", Context.MODE_PRIVATE)
        return sharedPreferences.getString("fotoId", null)
    }

    fun isValidBrazilianPlate(plate: String): Boolean {
        // Regex para o padrão antigo de placas (3 letras + 4 números)
        val oldPattern = "^[A-Z]{3}[0-9]{4}$".toRegex()

        // Regex para o padrão Mercosul (3 letras + 1 número + 1 letra + 2 números)
        val mercosulPattern = "^[A-Z]{3}[0-9]{1}[A-Z]{1}[0-9]{2}$".toRegex()

        // Verifica se a placa corresponde a algum dos padrões
        return plate.matches(oldPattern) || plate.matches(mercosulPattern)
    }

    fun isValidCarYear(year: String): Boolean {
        // Verifica se o ano é um número de 4 dígitos
        return year.matches("^[0-9]{4}$".toRegex())
    }

    fun isValidColor(color: String): Boolean {
        // Verifica se a cor é uma string não vazia
        return color.isNotEmpty()
    }

    fun selectImageInAlbum() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, REQUEST_SELECT_IMAGE_IN_ALBUM)
        }
    }

    // Atualize a variável isPhotoTaken após a foto ser escolhida
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_SELECT_IMAGE_IN_ALBUM && resultCode == RESULT_OK) {
            isPhotoTaken = true // Atualiza a variável para indicar que uma foto foi escolhida
            // Adicione sua lógica para manipular a imagem escolhida aqui
        }
    }

    companion object {
        private val REQUEST_TAKE_PHOTO = 0
        private val REQUEST_SELECT_IMAGE_IN_ALBUM = 1
        private var TAG = "RegisterCar"
    }
}
