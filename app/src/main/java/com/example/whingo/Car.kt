package com.example.whingo

import android.os.Parcel
import android.os.Parcelable

data class Car(
    val name: String = "",
    val price: Double = 0.0,
    val imageUrl: String = "",
    val year: Int = 0
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readDouble(),
        parcel.readString() ?: "",
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeDouble(price)
        parcel.writeString(imageUrl)
        parcel.writeInt(year)
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

