package fr.epf.foodlog.model

import android.os.Build
import android.os.Parcelable
import androidx.annotation.RequiresApi
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import fr.epf.foodlog.data.LocalDateConverter
import fr.epf.foodlog.data.UnityConverter
import kotlinx.android.parcel.Parcelize
import java.time.LocalDate
import java.util.*

enum class CategoryProduct {
    FRUIT, LEGUME, CEREALE, LAITIER, SUCRE, SALE, VIANDE, POISSON, BOISSON
}

enum class UnityProduct {
    Portion, Gramme
}

@Parcelize
@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val name : String,
    val category : CategoryProduct,
    @TypeConverters(LocalDateConverter::class)
    val date : LocalDate,
    val stock: Double,
    @TypeConverters(UnityConverter::class)
    val unite: UnityProduct,
    val nutriscore: String?,
    val uri : String
) : Parcelable {
    companion object {
        @RequiresApi(Build.VERSION_CODES.O)
        val all = mutableListOf<Product>(
            Product(1,"Pomme Pink Lady", CategoryProduct.FRUIT, LocalDate.of(2020, 4, 2),4.0,UnityProduct.Portion, "null", "null"),
            Product(2, "Aubergine", CategoryProduct.LEGUME, LocalDate.of(2020, 3, 30),3.0,UnityProduct.Portion, "null", "null"),
            Product(3, "Côte de boeuf", CategoryProduct.VIANDE, LocalDate.of(2020, 3, 30),500.0,UnityProduct.Gramme, "null", "null"),
            Product(4, "Dorade Grise", CategoryProduct.POISSON, LocalDate.of(2020, 4, 10),300.0,UnityProduct.Gramme, "null", "null")
        )
    }
}

