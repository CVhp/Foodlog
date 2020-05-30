package fr.epf.foodlog.ui.service

import retrofit2.http.GET
import retrofit2.http.Query

interface MarmitonAPI {

    @GET ("/apimarmiton.php")
    suspend fun getRecetteMarmiton(@Query("ingredient") ingredient: String) : Recettes

}

data class Recettes (val recipe : List<Recette> = emptyList())

data class Recette(val name: String = "",
                   val rate: String = "",
                   val time: String = "",
                   val tags: String = "",
                   val difficulty: String = "",
                   val ingredients: Array<String>,
                   val steps: Array<String>)