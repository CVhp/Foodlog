package fr.epf.foodlog.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "clients")
data class Client (@PrimaryKey(autoGenerate = true) val id: Int,
                   val lastname: String,
                   @ColumnInfo(name="first_name") val firstname: String,
                   val email: String,
                   val psw: String) : Parcelable {
    companion object { /*  all: même chose que :ListClient<>*/
        val all = (1..20).map{
            Client(it, "nom$it", "prenom$it", "email$it@epf.fr",
                "psw$it")
        }.toMutableList()
    }
}