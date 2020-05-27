package fr.epf.foodlog.service

import fr.epf.foodlog.Options.RecetteActivity
import retrofit2.http.GET
import retrofit2.http.Query

interface MarmitonAPI {

    @GET ("/apimarmiton.php")
    suspend fun getRecetteMarmiton(@Query("ingredient") ingredient: String) : Recette

}

data class Recette(val name: String = "",
                   val rate: String = "",
                   val time: String = "",
                   val tags: String = "",
                   val difficulty: String = "",
                   val ingredients: Array<String>,
                   val steps: Array<String>)