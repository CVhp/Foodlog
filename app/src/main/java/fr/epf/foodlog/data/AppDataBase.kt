package fr.epf.foodlog.data

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import fr.epf.foodlog.model.*
import java.time.LocalDate

@Database(entities = [Client::class, Product::class, Time::class], version = 1)
@TypeConverters(CategoryConverter::class, LocalDateConverter::class, UnityConverter::class)
abstract class AppDataBase : RoomDatabase() {
    abstract fun getClientDao() : ClientDao
    abstract fun getProductDao() : ProductDao
    abstract fun getTimeDao() : TimeDao
}

class CategoryConverter {
    @TypeConverter
    fun fromCategory(value : CategoryProduct) = value.toString() //prend un Gender et retourne une chaine de caractère
    @TypeConverter
    fun toCategory(value: String) = CategoryProduct.valueOf(value)
}

class LocalDateConverter {
    @TypeConverter
    fun fromLocalDate(value : LocalDate) = value.toString() //prend un Gender et retourne une chaine de caractère
    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun toLocalDate(value: String) = LocalDate.parse(value)
}

class UnityConverter {
    @TypeConverter
    fun fromUnity(value : UnityProduct) = value.toString() //prend un Gender et retourne une chaine de caractère
    @TypeConverter
    fun toUnity(value: String) = UnityProduct.valueOf(value)
}