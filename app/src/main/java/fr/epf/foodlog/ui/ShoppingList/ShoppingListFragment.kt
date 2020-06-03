package fr.epf.foodlog.ui.ShoppingList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import fr.epf.foodlog.R

class ShoppingListFragment : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_shopping_list, container, false)
        val textView: TextView = root.findViewById(R.id.text_gallery)

        return root
    }
}
