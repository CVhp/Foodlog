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
        val ingredients = requireArguments().getStringArray("ingredients")
        val steps = requireArguments().getStringArray("steps")

        root.name_view.text = name
        root.rate_view.text = rate
        root.time_view.text = time
        root.tags_view.text = tags
        root.difficulty_view.text = difficulty

        var display_ingredients = ""
        ingredients!!.forEach {
            display_ingredients = display_ingredients + "\n" + it
        }
        root.ingredient_view.text = display_ingredients

        var display_steps = ""
        steps!!.forEach {
            display_steps = display_steps + "\n" + it
        }
        root.steps_view.text = display_steps

        return root
    }
}