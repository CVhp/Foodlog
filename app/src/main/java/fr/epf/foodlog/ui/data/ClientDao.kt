package fr.epf.foodlog.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import fr.epf.foodlog.model.Client

@Dao
interface ClientDao {
    @Query("select * from clients")
    suspend fun getClients() : List<Client>

    @Insert
    suspend fun addClient(client: Client)

    @Query("DELETE FROM clients")
    suspend fun deleteClients()

    @Query("select * from clients where id = :idClient")
    suspend fun getClient(idClient: Int) : Client

}