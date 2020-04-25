package fr.epf.foodlog.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import fr.epf.foodlog.model.Product

@Dao
interface ProductDao {

    @Query("select * from products")
    suspend fun getProducts() : List<Product>

    @Insert
    suspend fun addProduct(product: Product)

    @Query("DELETE FROM products")
    suspend fun deleteProducts()

    @Query("select * from products where id = :idProduct")
    suspend fun getProduct(idProduct: Int) : Product
}