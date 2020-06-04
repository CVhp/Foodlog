package fr.epf.foodlog.ui.invitation

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.google.android.material.floatingactionbutton.FloatingActionButton

import fr.epf.foodlog.R
import fr.epf.foodlog.service.ProductService
import fr.epf.foodlog.service.retrofit
import fr.epf.foodlog.ui.Options.AddProductFragmentDirections
import fr.epf.foodlog.ui.fridge.AddFridgeFragmentDirections
import kotlinx.android.synthetic.main.fragment_add_fridge.view.*
import kotlinx.android.synthetic.main.fragment_add_product.view.*
import kotlinx.android.synthetic.main.fragment_invitation_create.view.*
import kotlinx.coroutines.runBlocking
import java.time.LocalDate

/**
 * A simple [Fragment] subclass.
 */
class InvitationCreateFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root=inflater.inflate(R.layout.fragment_invitation_create,container,false)

        root.btn_create_invitation.setOnClickListener {
            val appContext = requireActivity().applicationContext
            val pref = appContext.getSharedPreferences(
                "Foodlog",
                Context.MODE_PRIVATE
            )
            val token = pref.getString("token", null);
            val fridge=pref.getInt("fridge",0);
            val service  = retrofit("https://foodlog.min.epf.fr/").create(ProductService::class.java)

            val email: String = root.email_invitation.text.toString()

           val profil =root.level_invitation.selectedItem.toString()
            var profile=1

            when(profil){
                "Invité"->  profile=1
                "Membre"->  profile=4
            }

            runBlocking {
                service.postInvitation(token!!,fridge, profile, email)
            }

        }
        return root
    }

}
