package fr.epf.foodlog.ui.member

import android.content.Context
import android.provider.Settings.Global.getString
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import fr.epf.foodlog.R
import fr.epf.foodlog.service.*
import fr.epf.foodlog.ui.fridge.FridgeFragmentAdapter
import fr.epf.foodlog.ui.fridge.FridgeFragmentDirections
import kotlinx.android.synthetic.main.fragment_invitation_create.view.*
import kotlinx.android.synthetic.main.fridge_view.view.*
import kotlinx.android.synthetic.main.invitation_view.view.*
import kotlinx.android.synthetic.main.member_view.view.*
import kotlinx.coroutines.runBlocking
import okhttp3.internal.notify
import okhttp3.internal.notifyAll
import java.lang.Boolean.FALSE
import java.lang.Boolean.TRUE
import java.security.AccessController.getContext

class MemberFragmentAdapter(val context: Context, val members: List<Members>) :
    RecyclerView.Adapter<MemberFragmentAdapter.MemberViewHolder>() {
    class MemberViewHolder(val memberview: View) : RecyclerView.ViewHolder(memberview)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view: View = layoutInflater.inflate(R.layout.member_view, parent, false)
        return MemberViewHolder(view)
    }

    override fun getItemCount(): Int = members.size

    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {

        val member = members[position]
        holder.memberview.text_email_member.text = member.user

        val appContext = context
        val pref = appContext.getSharedPreferences(
            "Foodlog",
            Context.MODE_PRIVATE
        )

        val token = pref.getString("token", null)
        val fridge = pref.getInt("fridge", 0)
        val profile = pref.getInt("profile", 0)

        val member_profile = member.profile
        var member_profile_text = ""
        when (member_profile) {
            1 -> member_profile_text = "Invité"
            4 -> member_profile_text = "Membre"
            10 -> member_profile_text = "Admin"
        }
        holder.memberview.text_profile_member.text = member_profile_text
        if (profile > 9) {

            holder.memberview.setOnClickListener {
                val builder = AlertDialog.Builder(context)
                builder.setTitle("Confirmation suppression")
                builder.setMessage(

                    "Voulez vous supprimer ${member.user} de votre frigo ?"

                )
                builder.setNegativeButton(android.R.string.no) { _, _ ->
                    Log.d("EPF", "Non supprimé")
                }
                builder.setPositiveButton(android.R.string.yes) { _, _ ->
                    Log.d("EPF", "Supprimé")
                    val service =
                        retrofit("https://foodlog.min.epf.fr/").create(ProductService::class.java)

                    runBlocking {
                        service.deleteMember(token!!, fridge, member.user)
                        notifyItemRemoved(position)
                    }
                }
                builder.show()
            }
        }

    }
}