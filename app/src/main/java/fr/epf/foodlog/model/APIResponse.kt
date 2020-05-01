package fr.epf.foodlog.model

import fr.epf.foodlog.model.ProductOFF

class APIResponse {
    private val code: String? = null
    private val status_verbose: String? = null
    private val status: Int? = 1//null
    private val product: ProductOFF? = null

    fun getCode(): String? {
        return code
    }

    fun getStatus_verbose(): String? {
        return status_verbose
    }

    fun getStatus(): Int? {
        return status
    }

    fun getProduct(): ProductOFF? {
        return product
    }

}