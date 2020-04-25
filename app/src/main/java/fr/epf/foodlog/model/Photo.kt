package fr.epf.foodlog.model

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize


@Entity(tableName = "pictures")
data class Photo(@PrimaryKey (autoGenerate = true) val idProduct: Int,
                 val uriPicture: String)