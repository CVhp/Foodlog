package fr.epf.foodlog.Common

import fr.epf.foodlogsprint.remote.ClientAPI
import fr.epf.foodlog.Remote.RetrofitClient

object Common {
    val BASE_URL = "https://foodlog.min.epf.fr/api/"

    val api: ClientAPI
        get() = RetrofitClient.getClient(BASE_URL).create(ClientAPI::class.java)
}