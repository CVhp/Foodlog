package fr.epf.foodlog.ui.fridge

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation

import fr.epf.foodlog.R
import fr.epf.foodlog.service.ProductService
import fr.epf.foodlog.service.retrofit
import kotlinx.android.synthetic.main.fragment_add_fridge.view.*
import kotlinx.coroutines.runBlocking

/**
 * A simple [Fragment] subclass.
 */
class AddFridgeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root=inflater.inflate(R.layout.fragment_add_fridge,container,false)


        root.btn_add_fridge.setOnClickListener {
            val appContext = requireActivity().applicationContext
            val pref = appContext.getSharedPreferences(
                "Foodlog",
                Context.MODE_PRIVATE
            )
            val token = pref.getString("token", null);
            val service  = retrofit("https://foodlog.min.epf.fr/").create(ProductService::class.java)
            runBlocking { service.postFridge(token!!,root.et_add_fridge.text.toString()) }

            val target= AddFridgeFragmentDirections.actionAddFridgeFragmentToFridgeFragment()
             Navigation.findNavController(requireView()).navigate(target)

        }
        return root
    }

}
