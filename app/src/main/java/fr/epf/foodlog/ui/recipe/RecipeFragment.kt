package fr.epf.foodlog.ui.recipe

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import fr.epf.foodlog.R
import fr.epf.foodlog.service.ProductService
import fr.epf.foodlog.service.retrofit
import fr.epf.foodlog.ui.service.MarmitonAPI
import fr.epf.foodlog.ui.service.Recette
import kotlinx.android.synthetic.main.fragment_recette.*
import kotlinx.android.synthetic.main.fragment_recette.view.*
import kotlinx.coroutines.runBlocking

class RecipeFragment : Fragment() {

    lateinit var root: View
    lateinit var recipes_recyclerview : RecyclerView
    var recipes : List<Recette> = emptyList()

    lateinit var dialog: AlertDialog

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_recette, container, false)

        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        root.choisir_produits.setOnClickListener {
            showDialog()
        }

        root.lancer_recherche.setOnClickListener {

            var ingredient = root.ingredient_choix.text.toString()
            ingredient = ingredient.replace("+ ", "").replace(" ", "-").replace("'", "-")
            Log.d("plp", "${ingredient}")

            recipes = getRecette(ingredient)

            recipes_recyclerview = root.findViewById<View>(R.id.recipe_recyclerview) as RecyclerView
            recipes_recyclerview.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            recipes_recyclerview.adapter = RecipeAdapter(requireContext(), recipes)
        }
        return root
    }

    private fun showDialog(){
        val service  = retrofit("https://foodlog.min.epf.fr/").create(ProductService::class.java)
        var nameProduct = arrayOf<String>()
        runBlocking {
            val appContext = requireActivity().getApplicationContext()
            val pref = appContext.getSharedPreferences(
                "Foodlog",
                Context.MODE_PRIVATE
            )
            val token = pref.getString("token", null)
            val fridge = pref.getInt("fridge",0)
            val result = service.getProducts("$token",fridge)
            result.products.map {
                nameProduct = append(nameProduct, it.name)
            }
        }

        val arrayChecked = BooleanArray(nameProduct.size)
        Log.d("tyh", "${arrayChecked}")


        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Choississez vos produits")

        builder.setMultiChoiceItems(nameProduct, arrayChecked) { dialog, which, isChecked->
            // Update the clicked item checked status
            arrayChecked[which] = isChecked
        }

        // Set the positive/yes button click listener
        builder.setPositiveButton("OK") { _, _ ->
            // Do something when click positive button
            var str = ""
            for (i in 0 until nameProduct.size) {
                val checked = arrayChecked[i]
                if (checked) {
                    str += "${nameProduct[i]} + "
                }
            }
            str = str.substring(0, str.length-3)
            ingredient_choix.setText(str)
        }

        dialog = builder.create()
        dialog.show()

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

    fun append (arr: Array<String>, element: String) : Array<String> {
        val list = arr.toMutableList()
        list.add(element)
        return list.toTypedArray()
    }

}
