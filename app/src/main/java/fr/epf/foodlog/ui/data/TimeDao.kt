package fr.epf.foodlog.ui.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import fr.epf.foodlog.model.Product
import fr.epf.foodlog.ui.model.Time

@Dao
interface TimeDao {

    @Query("select * from times")
    suspend fun getTime() : List<Time>

    @Insert
    suspend fun addTime(time: Time)

    @Query("DELETE FROM times")
    suspend fun deleteTime()
}