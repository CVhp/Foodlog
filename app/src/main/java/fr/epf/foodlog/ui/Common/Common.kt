package fr.epf.foodlog.ui.Common

import fr.epf.foodlogsprint.remote.ClientAPI
import fr.epf.foodlogsprint1.remote.RetrofitClient

object Common {
    val BASE_URL = "https://foodlog.min.epf.fr/api/"

    val api: ClientAPI
        get() = RetrofitClient.getClient(BASE_URL).create(ClientAPI::class.java)
}