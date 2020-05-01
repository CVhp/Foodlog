package fr.epf.getimagefromgallery

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PhotoDao {
    @Query("select * from pictures")
    suspend fun getPictures() : List<Photo>

    @Insert
    suspend fun addPicture(client: Photo)
}