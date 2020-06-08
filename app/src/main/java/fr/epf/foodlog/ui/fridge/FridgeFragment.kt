package fr.epf.foodlog.ui.fridge

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import fr.epf.foodlog.R
import fr.epf.foodlog.service.ProductService
import fr.epf.foodlog.service.retrofit
import kotlinx.android.synthetic.main.fragment_fridge.*
import kotlinx.android.synthetic.main.fragment_fridge.view.*
import kotlinx.coroutines.runBlocking

/**
 * A simple [Fragment] subclass.
 */
class FridgeFragment : Fragment() {

    override fun onStart() {
        val appContext = requireActivity().applicationContext
        val pref = appContext.getSharedPreferences(
            "Foodlog",
            Context.MODE_PRIVATE
        )
        val fridge=pref.getInt("fridge",0)
        if(fridge!=0){
            Log.d("Fridge",fridge.toString())
                //val target=FridgeFragmentDirections.actionFridgeFragmentToNavListproduct(fridge)
                //Navigation.findNavController(requireView()).navigate(target)
        }
        super.onStart()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root=inflater.inflate(R.layout.fragment_fridge,container,false)
        val recyclerView: RecyclerView =root.findViewById(R.id.rv_fridge)
        recyclerView.layoutManager=
            LinearLayoutManager(this.requireContext(), LinearLayoutManager.VERTICAL,false)

        val service  = retrofit("https://foodlog.min.epf.fr/").create(ProductService::class.java)

        val appContext = requireActivity().applicationContext
        val pref = appContext.getSharedPreferences(
            "Foodlog",
            Context.MODE_PRIVATE
        )
        val token = pref.getString("token", null);

        root.fab_fridge.setOnClickListener {

        val target=FridgeFragmentDirections.actionFridgeFragmentToAddFridgeFragment()
            Navigation.findNavController(requireView()).navigate(target)

        }
        runBlocking {
            val result=service.getFridges(token!!)
            Log.d("test", "${result.fridge}")
           // if(!result.error){

            recyclerView.adapter=FridgeFragmentAdapter(result.fridge)

        }
        return root
    }

}
