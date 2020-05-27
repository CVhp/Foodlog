package fr.epf.foodlog.ListProduct

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import fr.epf.foodlog.Options.DetailsProductActivity
import fr.epf.foodlog.R
import fr.epf.foodlog.data.AppDataBase
import fr.epf.foodlog.data.ProductDao
import fr.epf.foodlog.model.CategoryProduct
import fr.epf.foodlog.model.Product
import fr.epf.foodlog.model.UnityProduct
import fr.epf.foodlog.service.ProductService
import fr.epf.foodlog.service.retrofit
import kotlinx.android.synthetic.main.product_view.view.*
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

class ProductAdapter(val context: Context, val productInterface: ProductInterface, val products: List<Product>)
    : RecyclerView.Adapter<ProductViewHolder>(), ViewHolderClickListener  {
    //class ProductViewHolder(val productView : View) : RecyclerView.ViewHolder(productView)

    var msg:Boolean = false

    override fun onLongTap(index: Int) {
        if (!ListProductActivity.isMultiSelectOn) {
            ListProductActivity.isMultiSelectOn = true
        }
        addIDIntoSelectedIds(index)
        Log.d("EPF", "$selectedIds")
    }

    override fun onTap(position: Int) {
        val product = products[position]
        if (ListProductActivity.isMultiSelectOn) {
            addIDIntoSelectedIds(position)
            Log.d("EPF", "$selectedIds")
        } else {
            Log.d("EPF", "$product")
            msg = false
            val intent = Intent(context, DetailsProductActivity::class.java) /*this : représente HomeActivity*/
            intent.putExtra("id", product.id)
            intent.putExtra("clientLastName", product.name)
            intent.putExtra("clientGender", "${product.category}")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                intent.putExtra("clientActive", "${product.date}")
            }
            intent.putExtra("stock", "${product.stock}")
            intent.putExtra("unite", "${product.unite}")
            intent.putExtra("uri", "${product.uri}" )

            context.startActivity(intent)
        }
    }

    fun addIDIntoSelectedIds(position: Int) {
        val product = products[position]
        if (selectedIds.contains(product.id))
            selectedIds.remove(product.id)
        else
            selectedIds.add(product.id)

        notifyItemChanged(position)
        if (selectedIds.size < 1) ListProductActivity.isMultiSelectOn = false
        productInterface.productInterface(selectedIds.size)
    }

    fun deleteSelectedIds() {
        Log.d("test", "$selectedIds")
        if (selectedIds.size < 1) return
        val selectedIdIteration = selectedIds.listIterator();

        while (selectedIdIteration.hasNext()) {
            val selectedItemID = selectedIdIteration.next()
            var indexOfModelList = 0
            val modelListIteration: MutableListIterator<Product> = modelList.listIterator();
            while (modelListIteration.hasNext()) {
                val model = modelListIteration.next()
                if (selectedItemID.equals(model.id)) {
                    modelListIteration.remove()
                    selectedIdIteration.remove()
                    notifyItemRemoved(indexOfModelList)
                }
                indexOfModelList++
            }
            ListProductActivity.isMultiSelectOn = false
        }
        Log.d("EPF", "$selectedIds")
    }

    var modelList: MutableList<Product> = ArrayList<Product>()
    val selectedIds: MutableList<Int> = ArrayList<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val layoutInflater : LayoutInflater = LayoutInflater.from(parent?.context)
        val view: View = layoutInflater.inflate(R.layout.product_view, parent, false)
        return ProductViewHolder(
            view,
            this
        )
    }

    override fun getItemCount(): Int = products.size

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        holder.productView.product_name_textview.text= "${product.name}"
        holder.productView.product_date_textview.text= "${product.date.toString()}"

        if (product.uri == "null"){
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
        } else {
            holder.productView.product_imageview.setImageURI(Uri.parse(product.uri))
        }

        holder.productView.stock.text="${product.stock}"
        when (product.unite) {
            UnityProduct.Portion -> holder.productView.product_unite_textView.text= "portions"
            UnityProduct.Gramme -> holder.productView.product_unite_textView.text= " grammes"
        }

        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val currentDate = sdf.format(Date())

        val today=LocalDate.parse(currentDate)
        val limitDa = today.plusDays(3)


        if(product.date.isBefore(limitDa)){
            holder.productView.product_date_textview.setTextColor(Color.RED)
        } else {
            holder.productView.product_date_textview.setTextColor(Color.BLACK)
        }

        if(product.date.isBefore(today)){
            holder.productView.product_date_textview.text="Périmé"
            holder.productView.product_date_textview.setTextColor(Color.RED)
        }


        if (selectedIds.contains(product.id)) {
            //if item is selected then,set foreground color of FrameLayout.
            holder.linearLayout?.foreground = ColorDrawable(ContextCompat.getColor(context, R.color.colorControlActivated))
        } else {
            //else remove selected item color.
            holder.linearLayout?.foreground = ColorDrawable(ContextCompat.getColor(context, android.R.color.transparent))
        }
    }


}