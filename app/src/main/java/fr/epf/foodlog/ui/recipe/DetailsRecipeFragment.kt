package fr.epf.foodlog.ui.recipe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import fr.epf.foodlog.R
import kotlinx.android.synthetic.main.fragment_details_recipe.view.*

class DetailsRecipeFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_details_recipe, container, false)

        val name = requireArguments().getString("name")
        val rate = requireArguments().getString("rate")
        val time = requireArguments().getString("time")
        val tags = requireArguments().getString("tags")
        val difficulty = requireArguments().getString("difficulty")
        val budget = requireArguments().getString("budget")
        val people = requireArguments().getString("people_quantity")
        val ingredients = requireArguments().getStringArray("ingredients")
        val steps = requireArguments().getStringArray("steps")

        val Nametimes = time!!.split("/")
        var cuisson = Nametimes[0].split(":")
        var prep = Nametimes[1].split(":")
        var tot = Nametimes[2].split(":")

        var tempsCuisson = cuisson[1]
        var tempsPrep = prep[1]
        var tempsTot = tot[1]

        root.name_view.text = name
        root.rate_view.text = rate
        root.tempsCuisson_view.text = tempsCuisson
        root.tempsPrep_view.text = tempsPrep
        root.tempsTot_view.text = tempsTot
        root.tags_view.text = tags
        root.difficulty_view.text = difficulty
        root.budget_view.text = budget
        root.quantity_view.text = people

        var display_ingredients = ""
        ingredients!!.forEach {
            if(display_ingredients != ""){
                display_ingredients = display_ingredients + "\n" + " - " + it
            }
            else{
                display_ingredients = " - " + it
            }
        }
        root.ingredient_view.text = display_ingredients

        var display_steps = ""
        steps!!.forEach {
            if(display_steps != ""){
                display_steps = display_steps + "\n" + " - " + it
            }
            else{
                display_steps = " - " + it
            }
        }
        root.steps_view.text = display_steps

        return root
    }
}