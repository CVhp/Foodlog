package fr.epf.foodlog.ui.invitation

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import fr.epf.foodlog.R
import fr.epf.foodlog.service.Fridge
import fr.epf.foodlog.service.Invitations
import fr.epf.foodlog.ui.fridge.FridgeFragmentAdapter
import fr.epf.foodlog.ui.fridge.FridgeFragmentDirections
import kotlinx.android.synthetic.main.fridge_view.view.*
import kotlinx.android.synthetic.main.invitation_view.view.*
import kotlinx.coroutines.runBlocking

class InvitationFragmentAdapter (val invitations: List<Invitations>) : RecyclerView.Adapter<InvitationFragmentAdapter.InvitationViewHolder>(){
    class InvitationViewHolder(val invitationview: View): RecyclerView.ViewHolder(invitationview)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InvitationViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view: View = layoutInflater.inflate(R.layout.invitation_view, parent, false)
        return InvitationViewHolder(view)
    }

    override fun getItemCount(): Int=invitations.size

    override fun onBindViewHolder(holder: InvitationViewHolder, position: Int) {
        val invitation=invitations[position]
        val invitation_view = holder.invitationview.invitation_accept
        holder.invitationview.text_invitation.text=invitation.id_fridge.toString()



    }
}