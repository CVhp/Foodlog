package fr.epf.foodlog.service

import retrofit2.http.*

interface ProductService {

    @POST("/apiv2/product/postProduct.php")
    suspend fun postProduct(
        @Query("token") token: String,
        @Query("Name") Name: String,
        @Query("Type") Type: String,
        @Query("Date") Date: String,
        @Query("stock") stock: String,
        @Query("unite") unite: String,
        @Query("id_fridge") id_fridge: Int,
        @Query("nutriscore") nutriscore: String?
    )

    @POST("/apiv2/product/postUri.php")
    suspend fun postUri(
        @Query("token") token: String,
        @Query("ID_product") ID_product: Int,
        @Query("uri_image") uri_image: String
    )

    @GET("/apiv2/product/updateProduct.php")
    suspend fun updateProduct(
        @Query("token") token: String,
        @Query("id_fridge") id_fridge: Int,
        @Query("Name") Name: String,
        @Query("Type") Type: String,
        @Query("Date") Date: String,
        @Query("stock") stock: String,
        @Query("unite") unite: Int,   //unité du stock: en portion (1) ou en gramme (2)
        @Query("product") product: Int
    )

    @GET("/apiv2/product/deleteProduct.php")
    suspend fun deleteProduct(
        @Query("token") token: String,
        @Query("id_fridge")id_fridge:Int,
        @Query("ID_product") ID_product: Int
    )

    @GET("/apiv2/product/getProduct.php")
    suspend fun getProducts(
        @Query("token") token: String,
        @Query("id_fridge") id_fridge: Int
    ): GetProductResults


    @FormUrlEncoded
    @POST("/apiv2/fridge/get.php")
    suspend fun getFridges(
        @Field("token") token: String
    ): ApiGetFridge

    @FormUrlEncoded
    @POST("/apiv2/fridge/create.php")
    suspend fun postFridge(
        @Field("token") token: String,
        @Field("name") name: String
    )

}

data class GetProductResults(val products: List<Product> = emptyList())

data class Product(
    val id: Int,
    val name: String = "",
    val type: String = "",
    val date: String = "",
    val id_client: String = "",
    val stock: String = "",
    val unite: Int,
    val nutriscore: String? = "",
    val uri: String? = ""
)

data class ApiGetFridge(
    // val error : Boolean,
    val fridge: List<Fridge>
)

data class Fridge(
    val id_fridge: Int,
    val name: String,
    val profile: Int
)
