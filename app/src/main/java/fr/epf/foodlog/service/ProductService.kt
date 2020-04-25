package fr.epf.foodlog.service

import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ProductService {
    @POST("/test_interface/postProduct.php")
    suspend fun postProduct(@Query("Name") Name : String,
                            @Query("Type") Type : String,
                            @Query("Date") Date : String,
                            @Query("ID_client") ID_client : String,
                            @Query("stock") stock : String,
                            @Query("unite") unite : Int)

    @GET("/test_interface/updateProduct.php")
    suspend fun updateProduct(@Query("Name") Name : String,
                              @Query("Type") Type : String,
                              @Query("Date") Date : String,
                              @Query("stock") stock : String,
                              @Query("unite") unite : Int,   //unité du stock: en portion (1) ou en gramme (2)
                              @Query("ID_product") ID_product : Int)

    @GET("/test_interface/getProducts.php")
    suspend fun getProducts(@Query("ID_client") ID_client: String) : GetProductResults
}

data class GetProductResults(val results: List<Product> = emptyList())

data class Product(val ID_product: Int, val Name: String = "", val Type: String = "", val Date: String = "", val ID_client: String = "", val stock: String="", val unite: Int)