package fr.epf.foodlog.data

import fr.epf.foodlog.model.APIResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path


interface OpenFoodFactsAPI {
    @GET("produit/{idProduct}.json")
    fun loadAPIResponse(@Path("idProduct") idProduct: String?): Call<APIResponse>
}
