package fr.epf.foodlog.ui.member

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fr.epf.foodlog.R
import fr.epf.foodlog.service.Invitations
import fr.epf.foodlog.service.Members
import fr.epf.foodlog.service.ProductService
import fr.epf.foodlog.service.retrofit
import fr.epf.foodlog.ui.invitation.InvitationFragmentAdapter
import kotlinx.coroutines.runBlocking

class MemberFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var members:MutableList<Members>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_member, container, false)
        recyclerView= root.findViewById(R.id.member_recyclerview)

        recyclerView.layoutManager=
            LinearLayoutManager(this.requireContext(), LinearLayoutManager.VERTICAL,false)

        val service  = retrofit("https://foodlog.min.epf.fr/").create(ProductService::class.java)

        val appContext = requireActivity().applicationContext
        val pref = appContext.getSharedPreferences(
            "Foodlog",
            Context.MODE_PRIVATE
        )
        val token = pref.getString("token", null);
        val fridge=pref.getInt("fridge",0);
        members= arrayListOf()
        runBlocking {
            val result=service.getMembers(token!!, fridge)

            result.members.map{
                members.add(it)
            }
            recyclerView.adapter  = MemberFragmentAdapter(requireContext(), members.toList())
        }

        return root

    }
}