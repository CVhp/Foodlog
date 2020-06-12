package fr.epf.foodlog.ui.fridge

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import fr.epf.foodlog.R
import fr.epf.foodlog.service.Fridge
import kotlinx.android.synthetic.main.fridge_view.view.*


class FridgeFragmentAdapter (val context: Context,val fridges:List<Fridge>) : RecyclerView.Adapter<FridgeFragmentAdapter.FridgeViewHolder>(){
    class FridgeViewHolder(val fridgeview: View):RecyclerView.ViewHolder(fridgeview)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FridgeViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view: View = layoutInflater.inflate(R.layout.fridge_view, parent, false)
        return FridgeViewHolder(view)
    }

    override fun getItemCount(): Int=fridges.size

    override fun onBindViewHolder(holder: FridgeViewHolder, position: Int) {
       val fridge=fridges[position]
        holder.fridgeview.tv_fridge.text=fridge.name
        val id :Int =fridge.id_fridge
        holder.fridgeview.setOnClickListener{

          val target = FridgeFragmentDirections.actionFridgeFragmentToNavListproduct(id)
            Navigation.findNavController(holder.fridgeview).navigate(target)

            val appContext = context
            val pref = appContext.getSharedPreferences(
                "Foodlog",
                Context.MODE_PRIVATE
            )
            val editor: SharedPreferences.Editor = pref.edit()
            editor.putInt(
                "profile",
                fridge.profile
            );
            editor.apply()

        }
    }

}