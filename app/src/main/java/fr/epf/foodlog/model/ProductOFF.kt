package fr.epf.foodlog.model

import java.io.Serializable

class ProductOFF : Serializable {
     private val generic_name: String? = null
     private val product_name: String? = "test"//null
     private val quantity: String? = "150" //null
     private val ingredients_text_with_allergens: String? = null
     private val nutrition_grade_fr: String? = null
     private val image_url: String? = null


     fun getImageUrl(): String? {
          return image_url
     }

     fun getGenericName(): String? {
          return generic_name
     }

     fun getProductName(): String? {
          return product_name
     }

     fun getQuantity(): String? {
          return quantity
     }


}

