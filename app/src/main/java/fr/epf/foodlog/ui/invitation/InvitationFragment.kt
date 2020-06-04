package fr.epf.foodlog.ui.invitation

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import fr.epf.foodlog.ListProduct.ProductAdapter

import fr.epf.foodlog.R
import fr.epf.foodlog.model.Product
import fr.epf.foodlog.service.Invitations
import fr.epf.foodlog.service.ProductService
import fr.epf.foodlog.service.retrofit
import fr.epf.foodlog.ui.fridge.AddFridgeFragmentDirections
import fr.epf.foodlog.ui.fridge.FridgeFragmentAdapter
import fr.epf.foodlog.ui.fridge.FridgeFragmentDirections
import kotlinx.android.synthetic.main.fragment_add_fridge.view.*
import kotlinx.android.synthetic.main.fragment_fridge.view.*
import kotlinx.android.synthetic.main.fragment_invitation.view.*
import kotlinx.android.synthetic.main.fragment_invitation_create.view.*
import kotlinx.android.synthetic.main.invitation_view.*
import kotlinx.android.synthetic.main.invitation_view.view.*
import kotlinx.android.synthetic.main.invitation_view.view.text_invitation
import kotlinx.coroutines.runBlocking

/**
 * A simple [Fragment] subclass.
 */
class InvitationFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var invitations:MutableList<Invitations>


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root=inflater.inflate(R.layout.fragment_invitation,container,false)

         recyclerView = root.findViewById(R.id.invitation_recyclerview)

        recyclerView.layoutManager=
            LinearLayoutManager(this.requireContext(), LinearLayoutManager.VERTICAL,false)

        val service  = retrofit("https://foodlog.min.epf.fr/").create(ProductService::class.java)

        val appContext = requireActivity().applicationContext
        val pref = appContext.getSharedPreferences(
            "Foodlog",
            Context.MODE_PRIVATE
        )
        val token = pref.getString("token", null);
        invitations= arrayListOf()
        runBlocking {
            val result=service.getInvitations(token!!)
            result.invitations.map{
                invitations.add(it)
            }
            recyclerView.adapter  = InvitationFragmentAdapter(invitations.toList())
        }

        return root
    }

}
