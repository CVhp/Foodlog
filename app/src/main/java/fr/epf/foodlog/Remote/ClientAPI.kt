package fr.epf.foodlogsprint.remote

import fr.epf.foodlog.model.ClientAuth
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ClientAPI {

    @FormUrlEncoded
    @POST("register.php")
    fun registerUser(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<ClientAuth>


    @FormUrlEncoded
    @POST("login.php")
    fun loginUser(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<ClientAuth>

    @FormUrlEncoded
    @POST("login.php")
    fun loginUserWithToken(
        @Field("token") token: String
    ): Call<ClientAuth>
}