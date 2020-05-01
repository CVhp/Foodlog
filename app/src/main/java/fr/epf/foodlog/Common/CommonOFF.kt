package fr.epf.foodlog.Common

import fr.epf.foodlog.data.OpenFoodFactsAPI
import fr.epf.foodlog.Remote.RetrofitClient

object CommonOFF {

    val BASE_URL = "https://world.openfoodfacts.org/api/v0/"

    val api: OpenFoodFactsAPI
        get() = RetrofitClient.getClient(BASE_URL).create(OpenFoodFactsAPI::class.java)

}