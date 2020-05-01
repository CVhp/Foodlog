package fr.epf.foodlog.data

import fr.epf.foodlog.model.APIResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path


interface OpenFoodFactsAPI {
    @GET("/api/v0//produit/{idProduct}.json")
    suspend fun loadAPIResponse(@Path("idProduct") idProduct : String) : GetResult
}

data class GetResult(val status: String = "", val product: Product = Product())
data class Product(val product_name: String = "", val quantity: String ="")