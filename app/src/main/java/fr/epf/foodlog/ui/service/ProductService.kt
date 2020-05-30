package fr.epf.foodlog.service

import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ProductService {
    @POST("/api/product/postProduct.php")
    suspend fun postProduct(@Query("token") token: String,
                            @Query("Name") Name: String,
                            @Query("Type") Type : String,
                            @Query("Date") Date: String,
                            @Query("stock") stock: String,
                            @Query("unite") unite: String,
                            @Query("nutriscore") nutriscore: String?)

    @POST ("/api/product/postUri.php")
    suspend fun postUri(@Query("token") token: String,
                        @Query("ID_product") ID_product : Int,
                        @Query("uri_image") uri_image: String)

    @GET("/api/product/updateProduct.php")
    suspend fun updateProduct(@Query("token") token: String,
                              @Query("Name") Name : String,
                              @Query("Type") Type : String,
                              @Query("Date") Date : String,
                              @Query("stock") stock : String,
                              @Query("unite") unite : Int,   //unité du stock: en portion (1) ou en gramme (2)
                              @Query("product") product : Int)

    @GET("/api/product/deleteProduct.php")
    suspend fun deleteProduct(@Query("token") token: String,
                              @Query("ID_product") ID_product: Int
    )

    @GET("/api/product/getProduct.php")
    suspend fun getProducts(@Query("token") token: String) : GetProductResults
}

data class GetProductResults(val products: List<Product> = emptyList())

data class Product(val id : Int,
                   val name: String = "",
                   val type : String = "",
                   val date: String = "",
                   val id_client: String = "",
                   val stock: String="",
                   val unite: Int,
                   val nutriscore: String? = "",
                   val uri : String?="")
