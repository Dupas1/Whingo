package com.example.whingo

import android.os.Parcel
import android.os.Parcelable

data class Car(
    val name: String = "",
    val ValordaLocação: Double = 0.0, // Alterado de "price" para "ValordaLocação"
    val photos: List<String> = emptyList(), // Lista de fotos
    val year: String = "", // Ano do carro
    val Phone: String = "" // Telefone do dono do carro
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readDouble(),
        parcel.createStringArrayList() ?: emptyList(),
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeDouble(ValordaLocação) // Alterado para "ValordaLocação"
        parcel.writeStringList(photos)
        parcel.writeString(year)
        parcel.writeString(Phone)
    }

    override fun describeContents(): Int = 0

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Car> = object : Parcelable.Creator<Car> {
            override fun createFromParcel(parcel: Parcel): Car {
                return Car(parcel)
            }

            override fun newArray(size: Int): Array<Car?> {
                return arrayOfNulls(size)
            }
        }
    }
}