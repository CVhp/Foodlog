package fr.epf.foodlog.barecode

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path


interface OpenFoodFactsAPI {
    @GET("produit/{idProduct}.json")
    fun loadAPIResponse(@Path("idProduct") idProduct: String?): Call<APIResponse>
}
