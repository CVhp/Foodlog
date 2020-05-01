package fr.epf.foodlog.ListProduct

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import fr.epf.foodlog.Options.DetailsProductActivity
import fr.epf.foodlog.R
import fr.epf.foodlog.model.CategoryProduct
import fr.epf.foodlog.model.Product
import fr.epf.foodlog.model.UnityProduct
import kotlinx.android.synthetic.main.product_view.view.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

class ProductAdapter(val products: List<Product>) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {
    class ProductViewHolder(val productView : View) : RecyclerView.ViewHolder(productView)

    var msg:Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val layoutInflater : LayoutInflater = LayoutInflater.from(parent.context)
        val view: View = layoutInflater.inflate(R.layout.product_view, parent, false)
        return ProductViewHolder(
            view
        )
    }

    override fun getItemCount(): Int = products.size

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        holder.productView.product_name_textview.text= "${product.name}"
        holder.productView.product_date_textview.text= "${product.date.toString()}"
        holder.productView.product_imageview.setImageResource(
            when(product.category){
                CategoryProduct.FRUIT -> R.drawable.pomme
                CategoryProduct.LEGUME -> R.drawable.aubergine
                CategoryProduct.CEREALE -> R.drawable.cereale
                CategoryProduct.LAITIER -> R.drawable.banane
                CategoryProduct.SALE -> R.drawable.pomme
                CategoryProduct.SUCRE -> R.drawable.sucre
                CategoryProduct.VIANDE -> R.drawable.viande
                CategoryProduct.POISSON -> R.drawable.poisson
                CategoryProduct.BOISSON -> R.drawable.boisson
            }
        )

        holder.productView.stock.text="${product.stock}"
        when (product.unite) {
            UnityProduct.Portion -> holder.productView.product_unite_textView.text= "portions"
            UnityProduct.Gramme -> holder.productView.product_unite_textView.text= " grammes"
        }

        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val currentDate = sdf.format(Date())

        val d=LocalDate.parse(currentDate)
        val limitDa = d.plusDays(2)

        if(product.date.isBefore(limitDa) && product.date.isAfter(d)  || product.date.isEqual(limitDa)){
            holder.productView.product_date_textview.setTextColor(Color.RED)
        }
        if(product.date.isBefore(d)){
            holder.productView.product_date_textview.text="Périmé"
            holder.productView.product_date_textview.setTextColor(Color.RED)
        }

        holder.productView.setOnClickListener {
            Log.d("EPF", "$product")
            msg = false
            val intent = Intent(it.context, DetailsProductActivity::class.java) /*this : représente HomeActivity*/
            intent.putExtra("id", product.id)
            intent.putExtra("clientLastName", product.name)
            intent.putExtra("clientGender", "${product.category}")
            intent.putExtra("clientActive", "${product.date}")
            intent.putExtra("stock", "${product.stock}")
            intent.putExtra("unite", "${product.unite}")

            it.context.startActivity(intent)
        }
//        holder.productView.setOnLongClickListener{
//            //onItemLongClick?.invoke(position)
//            msg = true
//            holder.productView.checkBox.setVisibility(View.VISIBLE)
//            Log.d("BOU","$msg")
//            return@setOnLongClickListener true
//        }
    }

    //var onItemLongClick: ((Int) -> Unit)? = null

}