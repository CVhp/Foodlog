package fr.epf.foodlog.ui.recipe

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import fr.epf.foodlog.ListProduct.ProductInterface
import fr.epf.foodlog.R
import fr.epf.foodlog.model.Product
import fr.epf.foodlog.ui.service.Recette
import kotlinx.android.synthetic.main.product_view.view.*
import kotlinx.android.synthetic.main.recipe_view.view.*

class RecipeAdapter (val context: Context, val recipes: List<Recette>) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {
    class RecipeViewHolder(val recipeView : View) : RecyclerView.ViewHolder(recipeView)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val layoutInflater : LayoutInflater = LayoutInflater.from(parent.context)
        val view: View = layoutInflater.inflate(R.layout.recipe_view, parent, false)
        return RecipeViewHolder(view)
    }

    override fun getItemCount(): Int = recipes.size

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipes[position]
        holder.recipeView.recipe_name_textview.text = "${recipe.name}"
        holder.recipeView.difficulty.text = "${recipe.difficulty}"

        holder.recipeView.difficulty.setTextColor(
            when(recipe.difficulty){
                "très facile" -> ContextCompat.getColor(context, R.color.lightGreen)
                "facile" -> ContextCompat.getColor(context, R.color.colorPrimary)
                "Niveau moyen" -> ContextCompat.getColor(context, R.color.orange)
                else -> ContextCompat.getColor(context, R.color.red)
            }
        )

        val tag = recipe.tags
        val tags = tag.split(":")

        holder.recipeView.tags.text = tags[1]
        holder.recipeView.setOnClickListener {
            val bundle = bundleOf("name" to recipe.name,
                "rate" to recipe.rate,
                "time" to recipe.time,
                "tags" to tags[1],
                "difficulty" to recipe.difficulty,
                "budget" to recipe.budget,
                "ingredients" to recipe.ingredients,
                "people_quantity" to recipe.people_quantity,
                "steps" to recipe.steps)
            Navigation.findNavController(it)
                .navigate(R.id.navigate_to_detailRecipe_fragment, bundle);
        }
    }

}

