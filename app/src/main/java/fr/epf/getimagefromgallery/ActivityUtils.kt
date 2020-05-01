package fr.epf.getimagefromgallery

import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.facebook.stetho.okhttp3.StethoInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

fun AppCompatActivity.dao() : PhotoDao {
    val database: AppDataBase = Room.databaseBuilder(this, AppDataBase::class.java, "gestionPictures")
        .build()
    return database.getPhotoDao()
}

fun AppCompatActivity.retrofit() : Retrofit {
    val httpLoggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    val client = OkHttpClient.Builder()
        .addInterceptor(httpLoggingInterceptor)
        .addNetworkInterceptor(StethoInterceptor())
        .build()

    return Retrofit.Builder()
        .baseUrl("https://foodlog.min.epf.fr/")
        .addConverterFactory(MoshiConverterFactory.create())
        .client(client)
        .build()
}