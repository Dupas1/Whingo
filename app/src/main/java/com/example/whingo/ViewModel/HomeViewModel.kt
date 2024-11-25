package com.example.whingo.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.whingo.Model.SliderModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeViewModel:ViewModel() {

    private  val firebasedatabase = FirebaseDatabase.getInstance()

    private val _banner = MutableLiveData<List<SliderModel>>()


    val banners:LiveData<List<SliderModel>> = _banner


    fun loadBanners(){
        val ref = firebasedatabase.getReference("Banner")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lists = mutableListOf<SliderModel>()
                for(childSnapshot in snapshot.children){
                    val list = childSnapshot.getValue(SliderModel::class.java)
                    if (list != null){
                        lists.add(list)
                    }
                    _banner.value = lists

                }
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                
                TODO("Not yet implemented")
            }
        })
    }
}