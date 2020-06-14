package fr.epf.foodlog.service

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path


interface OpenFoodFactsAPI {
    @GET("/api/v0//produit/{idProduct}.json")
    suspend fun loadAPIResponse(@Path("idProduct") idProduct : String) : GetResult
}

data class GetResult(val status: String = "", val product: ProductAPI = ProductAPI())
data class ProductAPI(val product_name: String = "", val quantity: String ="", val nutrition_grade_fr: String="", val image_front_url:String="")