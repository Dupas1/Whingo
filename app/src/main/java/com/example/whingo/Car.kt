import android.os.Parcel
import android.os.Parcelable

data class Car(
    val id: String,
    val modeloDoCarro: String,
    val valorDoCarro: Double,
    val fotos: List<String>,
    val anoDoCarro: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readDouble(),
        parcel.createStringArrayList() ?: listOf(),
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(modeloDoCarro)
        parcel.writeDouble(valorDoCarro)
        parcel.writeStringList(fotos)
        parcel.writeString(anoDoCarro)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Car> {
        override fun createFromParcel(parcel: Parcel): Car {
            return Car(parcel)
        }

        override fun newArray(size: Int): Array<Car?> {
            return arrayOfNulls(size)
        }
    }
}