package fr.epf.foodlog.service

import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.facebook.stetho.okhttp3.StethoInterceptor
import fr.epf.foodlog.data.AppDataBase
import fr.epf.foodlog.data.ClientDao
import fr.epf.foodlog.data.ProductDao
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

fun AppCompatActivity.clientdao() : ClientDao {
    val database: AppDataBase = Room.databaseBuilder(this, AppDataBase::class.java, "gestionclients")
        .build()
    return database.getClientDao()
}

fun AppCompatActivity.productdao() : ProductDao {
    val database: AppDataBase = Room.databaseBuilder(this, AppDataBase::class.java, "gestionproducts")
        .build()
    return database.getProductDao()
}

fun AppCompatActivity.retrofit(baseURL : String) : Retrofit {
    val httpLoggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    val client = OkHttpClient.Builder()
        .addInterceptor(httpLoggingInterceptor)
        .addNetworkInterceptor(StethoInterceptor())
        .build()

    return Retrofit.Builder()
        .baseUrl(baseURL)
        .addConverterFactory(MoshiConverterFactory.create())
        .client(client)
        .build()
}

