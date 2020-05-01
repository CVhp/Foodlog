package fr.epf.getimagefromgallery

import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query


interface ImageService{
    @POST("/postImage.php")
    suspend fun postImage(@Query("Id_product") Id_product : String,
                          @Query("Uri_image") Uri_image : String)

    @GET("/img_upload_to_server.php")
    suspend fun postImageServer(@Query("image_path") image_path : String)
}