package fr.epf.foodlog.Options

import android.content.Context
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import fr.epf.foodlog.R
import fr.epf.foodlog.service.MarmitonAPI
import fr.epf.foodlog.service.ProductService
import fr.epf.foodlog.service.retrofit
import kotlinx.android.synthetic.main.activity_recette.*
import kotlinx.coroutines.runBlocking

class RecetteActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recette)

        lancer_recherche.setOnClickListener {
            val ingredient = ingredient_choix.text.toString()
            getRecette(ingredient)
        }
    }

    fun getRecette(ingredient : String){
        val service = retrofit("https://foodlog.min.epf.fr/").create(MarmitonAPI::class.java)
        runBlocking {
            val result = service.getRecetteMarmiton(ingredient)
            var str = result.name +
                    result.time + "\n"  +
                    result.tags + "\n" +
                    result.difficulty + "\n"

            var ingredients = ""
            result.ingredients.forEach {
                ingredients = ingredients + "\n" + it
            }

            var steps = ""
            result.steps.forEach {
                steps = steps + "\n" + it
            }

            str = str + "\n" + ingredients + "\n" + steps

            affiche_recette.setText(str)
        }
    }
}