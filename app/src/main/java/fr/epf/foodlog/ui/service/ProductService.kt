package fr.epf.foodlog.service

import android.provider.MediaStore
import okhttp3.MultipartBody
import okhttp3.RequestBody
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
        @Query("nutriscore") nutriscore: String?,
        @Query("uri") uri: String?
    )

    @POST("/apiv2/product/postUri.php")
    suspend fun postUri(
        @Query("token") token: String,
        @Query("id_fridge") id_frige: Int,
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

    @FormUrlEncoded
    @POST("/apiv2/fridge/getInvitation.php")
    suspend fun getInvitations(
        @Field("token") token: String
    ): GetInvitation

    @FormUrlEncoded
    @POST("/apiv2/fridge/createInvitation.php")
    suspend fun postInvitation(
        @Field("token") token: String,
        @Field("id_fridge") id_fridge: Int,
        @Field("profile") profile: Int,
        @Field("email") email: String
    )

    @FormUrlEncoded
    @POST("/apiv2/fridge/confirmInvitation.php")
    suspend fun confirmInvitation(
        @Field("token") token: String,
        @Field("token_invitation") token_invitation: String,
        @Field("choice") choice: Int
    )

    @FormUrlEncoded
    @POST("/apiv2/fridge/getMembers.php")
    suspend fun getMembers(
        @Field("token") token: String,
        @Field("id_fridge") id_fridge: Int
    ): GetMember

    @FormUrlEncoded
    @POST("/apiv2/fridge/fireFromFridge.php")
    suspend fun deleteMember(
        @Field("token") token: String,
        @Field("id_fridge") id_fridge: Int,
        @Field("email") email: String
    )

    @Multipart
    @POST("/apiv2/upload_image.php?apicall=upload")
    suspend fun uploadImage(
        @Part image: MultipartBody.Part,
        @Part("desc") desc: RequestBody,
        @Part("id_product")id_product:Int
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

data class GetInvitation(
    val invitations: List<Invitations>
)

data class Invitations(
    val fridge: String,
    val owner:String,
    val token_invitation: String,
    val profile: Int
)

data class GetMember(
    val members: List<Members>
)

data class Members(
    val user: String,
    val profile: Int
)