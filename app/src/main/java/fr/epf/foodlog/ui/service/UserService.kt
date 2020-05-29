package fr.epf.foodlog.service

import retrofit2.http.POST
import retrofit2.http.Query

interface UserService {

    @POST("/test_interface/data.php")
    suspend fun postUser(@Query("Last_Name") Last_Name : String,
                         @Query("First_Name") First_Name : String,
                         @Query("Email") Email : String,
                         @Query("Password") Password : String)

}