package fr.epf.getimagefromgallery

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = arrayOf(Photo::class), version = 1)
abstract class AppDataBase : RoomDatabase(){
    abstract fun getPhotoDao() : PhotoDao
}