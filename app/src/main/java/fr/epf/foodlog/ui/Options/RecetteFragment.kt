package fr.epf.foodlog.ui.Options

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

import fr.epf.foodlog.R
import fr.epf.foodlog.service.retrofit
import fr.epf.foodlog.ui.service.MarmitonAPI
import kotlinx.android.synthetic.main.fragment_recette.view.*
import kotlinx.coroutines.runBlocking

class RecetteFragment : Fragment() {

    lateinit var root: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_recette, container, false)

        root.lancer_recherche.setOnClickListener {
            val ingredient = root.ingredient_choix.text.toString()
            getRecette(ingredient)
        }
        return root
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

            root.affiche_recette.setText(str)
        }
    }
}
