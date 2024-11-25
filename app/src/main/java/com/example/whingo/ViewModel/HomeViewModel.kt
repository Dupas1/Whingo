package com.example.whingo.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.whingo.Model.SliderModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeViewModel : ViewModel() {

    private val firebasedatabase = FirebaseDatabase.getInstance()
    private val _banners = MutableLiveData<List<SliderModel>>()
    val banners: LiveData<List<SliderModel>> = _banners

    fun loadBanners() {
        val ref = firebasedatabase.getReference("Banner")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lists = mutableListOf<SliderModel>()
                for (childSnapshot in snapshot.children) {
                    val list = childSnapshot.getValue(SliderModel::class.java)
                    if (list != null) {
                        lists.add(list)
                    }
                }

                // Atualiza o LiveData com os banners recuperados
                _banners.value = lists
            }

            override fun onCancelled(error: DatabaseError) {
                // Adiciona tratamento de erro apropriado
                _banners.value = emptyList() // Retorna uma lista vazia em caso de falha
                println("Erro ao carregar banners: ${error.message}") // Log do erro
            }
        })
    }
}