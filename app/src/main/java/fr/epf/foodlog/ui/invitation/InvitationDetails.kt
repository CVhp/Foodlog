package fr.epf.foodlog.ui.invitation

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import fr.epf.foodlog.R
import fr.epf.foodlog.service.ProductService
import fr.epf.foodlog.service.retrofit
import kotlinx.android.synthetic.main.fragment_invitation_details.view.*
import kotlinx.coroutines.runBlocking
import java.lang.Boolean.FALSE
import java.lang.Boolean.TRUE

/**
 * A simple [Fragment] subclass.
 */
class InvitationDetails : Fragment() {
    private lateinit var fridge: String
    private lateinit var mail: String
    private lateinit var tokenInvitation: String
    private  var profile=0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_invitation_details, container, false)

        val appContext = requireActivity().applicationContext
        val pref = appContext.getSharedPreferences(
            "Foodlog",
            Context.MODE_PRIVATE
        )
        val token = pref.getString("token", null);
        arguments?.let {
            val safeArgs = InvitationDetailsArgs.fromBundle(it)
            fridge = safeArgs.fridgeInvitation
            mail = safeArgs.mailInvitation
            tokenInvitation = safeArgs.tokenInvitation
            profile=safeArgs.profile
        }
        root.tv_email_sender.text=mail
        if(profile==1){
            root.tv_profile.text="Invité"
            }else{
            root.tv_profile.text="Membre"
        }


        val service = retrofit("https://foodlog.min.epf.fr/").create(ProductService::class.java)

        root.btn_invitation_accept.setOnClickListener {
            runBlocking {
                service.confirmInvitation(token!!, tokenInvitation, 1)
                Toast.makeText(requireContext(),"Invitation acceptée",Toast.LENGTH_SHORT).show()
                requireActivity().onBackPressed()
            }
        }
        root.btn_invitation_decline.setOnClickListener {
            runBlocking {
                service.confirmInvitation(token!!, tokenInvitation, 2)
                Toast.makeText(requireContext(),"Invitation refusée",Toast.LENGTH_SHORT).show()
                requireActivity().onBackPressed()
            }
        }


        return root
    }

}
