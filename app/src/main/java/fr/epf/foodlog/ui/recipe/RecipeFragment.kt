package fr.epf.foodlog.ui.recipe

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import fr.epf.foodlog.R
import fr.epf.foodlog.service.retrofit
import fr.epf.foodlog.ui.service.MarmitonAPI
import fr.epf.foodlog.ui.service.Recette
import kotlinx.android.synthetic.main.fragment_recette.view.*
import kotlinx.coroutines.runBlocking

class RecipeFragment : Fragment() {

    lateinit var root: View
    lateinit var recipes_recyclerview : RecyclerView
    var recipes : List<Recette> = emptyList()

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_recette, container, false)

        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        root.lancer_recherche.setOnClickListener {
            val ingredient = root.ingredient_choix.text.toString()
            recipes = getRecette(ingredient)

            recipes_recyclerview = root.findViewById<View>(R.id.recipe_recyclerview) as RecyclerView
            recipes_recyclerview.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            recipes_recyclerview.adapter = RecipeAdapter(requireContext(), recipes)
        }
        return root
    }

    override fun onResume() {
        super.onResume()
        if (!recipes.isEmpty()){
            recipes_recyclerview = root.findViewById<View>(R.id.recipe_recyclerview) as RecyclerView
            recipes_recyclerview.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            recipes_recyclerview.adapter = RecipeAdapter(requireContext(), recipes)
        }
    }

    fun getRecette(ingredient : String) : List<Recette>{
        val service = retrofit("https://foodlog.min.epf.fr/").create(MarmitonAPI::class.java)
        var recettes = emptyList<Recette>()
        runBlocking {
            val result = service.getRecetteMarmiton(ingredient)
            recettes = result.recipe
        }
        return recettes
    }
}
