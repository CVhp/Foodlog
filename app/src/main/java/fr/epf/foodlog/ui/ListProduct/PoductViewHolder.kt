package fr.epf.foodlog.ui.ListProduct

import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import fr.epf.foodlog.ListProduct.ViewHolderClickListener
import fr.epf.foodlog.R

class ProductViewHolder(val productView: View, val r_tap: ViewHolderClickListener) : RecyclerView.ViewHolder(productView),
    View.OnLongClickListener, View.OnClickListener{

        val linearLayout: LinearLayout

        init {//initialization block
            linearLayout = productView.findViewById(R.id.lyt_parent)
            linearLayout.setOnClickListener(this)
            linearLayout.setOnLongClickListener(this)
        }

        override fun onClick(v: View?) {
            r_tap.onTap(adapterPosition)
        }

        override fun onLongClick(v: View?): Boolean {
            r_tap.onLongTap(adapterPosition)
            return true
        }
}