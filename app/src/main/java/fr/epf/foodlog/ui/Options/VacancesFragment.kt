package fr.epf.foodlog.ui.Options

import android.app.DatePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import fr.epf.foodlog.ListProduct.ProductAdapter
import fr.epf.foodlog.ListProduct.ProductInterface
import fr.epf.foodlog.R
import fr.epf.foodlog.data.AppDataBase
import fr.epf.foodlog.data.ProductDao
import fr.epf.foodlog.model.CategoryProduct
import fr.epf.foodlog.model.Product
import fr.epf.foodlog.model.UnityProduct
import fr.epf.foodlog.service.ProductService
import fr.epf.foodlog.service.retrofit
import fr.epf.foodlog.ui.ListProduct.ListProductFragment
import kotlinx.android.synthetic.main.fragment_add_product.view.*
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.util.*


class VacancesFragment : Fragment(), ProductInterface {

    private var mDateSetListener: DatePickerDialog.OnDateSetListener? = null
    private var fridge=0
    lateinit var products_recyclerview : RecyclerView
    var actionMode: ActionMode? = null
    var adap: ProductAdapter? = null

    //ListeProductAffichée

     var ListeProductCopy : MutableList<Product> = arrayListOf()

    override fun productInterface(size: Int) {
        if (actionMode == null) actionMode = requireActivity().startActionMode(ActionModeCallback())
        if (size > 0) actionMode?.setTitle("$size")
        else actionMode?.finish()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


           val pref = requireContext().getSharedPreferences(
                "Foodlog",
                Context.MODE_PRIVATE
            )

            if (fridge != 0){
                val editor: SharedPreferences.Editor = pref.edit()
                editor.putInt(
                    "fridge", fridge
                )
                editor.apply()
            } else if (fridge == 0) {
                fridge = pref.getInt("fridge", 0)
            }




        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_vacances, container, false)

        root.tvDate.setOnClickListener(View.OnClickListener {
            val cal: Calendar = Calendar.getInstance()
            val year: Int = cal.get(Calendar.YEAR)
            val month: Int = cal.get(Calendar.MONTH)
            val day: Int = cal.get(Calendar.DAY_OF_MONTH)
            val dialog = DatePickerDialog(
                requireContext(),
                mDateSetListener,
                year, month, day
            )
            dialog.show()
        })

        mDateSetListener =
            DatePickerDialog.OnDateSetListener { datePicker, year, month, day ->
                var month = month
                var date_month: String = ""
                var date_day: String = ""
                month = month + 1
                if ("${month}".length == 1) {
                    date_month = "0$month"
                } else {
                    date_month = "$month"
                }

                if ("${day}".length == 1) {
                    date_day = "0$day"
                } else {
                    date_day = "$day"
                }
                val date = "$year-$date_month-$date_day"
                root.tvDate.text = date
            }

        root.tvDate.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(
                s: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
                // TODO Auto-generated method stub
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int,
                after: Int
            ) {
                // TODO Auto-generated method stub
            }

            override fun afterTextChanged(s: Editable) {

                if(ListeProductCopy.size<1){
                    ListeProductCopy.addAll(Product.all)
                }

                val dateMax = LocalDate.parse(s.toString())
                Product.all.clear()
                ListeProductCopy.map {
                    if(it.date.isBefore(dateMax)){
                        Product.all.add(it)
                    }
                }
                adap?.notifyDataSetChanged()
            }
        })

        // Avoir initialisé ListeCopy.addAll(ListeProduct) juste après avoir tout téléchargé
        /*filtre: ListAffichée.clear()
//       ListCopy.map{->product

if(product.date.isAfter(date max){
ListAffichée.add(product)
}
}
adap.notifyDataSetChanged();

*/

        requireActivity().runOnUiThread (Runnable {

            products_recyclerview = root.findViewById<RecyclerView>(R.id.products_recyclerview2)

            adap = ProductAdapter(requireContext(), this, Product.all)
            ListProductFragment.isMultiSelectOn = false

            products_recyclerview.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            products_recyclerview.adapter =   adap

            Product.all.clear()
            getServer()
        })


        return root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getServer(){
        val service  = retrofit("https://foodlog.min.epf.fr/").create(ProductService::class.java)
        val database: AppDataBase = Room.databaseBuilder(requireContext(), AppDataBase::class.java, "gestionclients")
            .build()
        val productDao : ProductDao = database.getProductDao()
        runBlocking {
            productDao.deleteProducts() //permet de clear la base de donnée interne products
        }

        runBlocking {
            val appContext = requireActivity().getApplicationContext()
            val pref = appContext.getSharedPreferences(
                "Foodlog",
                Context.MODE_PRIVATE
            )
            val token = pref.getString("token", null)
            val result = service.getProducts("$token",fridge)
            result.products.map {
                val id = it.id
                val name = it.name
                val type = it.type
                val date = it.date
                val stock = it.stock
                val unite = it.unite
                val nutriscore = it.nutriscore
                var uri = it.uri

                var typeCategory : CategoryProduct = CategoryProduct.FRUIT
                when(type){
                    "2" -> typeCategory = CategoryProduct.LEGUME
                    "1" -> typeCategory = CategoryProduct.FRUIT
                    "3" -> typeCategory = CategoryProduct.CEREALE
                    "4" -> typeCategory = CategoryProduct.LAITIER
                    "5" -> typeCategory = CategoryProduct.SUCRE
                    "6" -> typeCategory = CategoryProduct.SALE
                    "7" -> typeCategory = CategoryProduct.VIANDE
                    "8" -> typeCategory = CategoryProduct.POISSON
                    "9" -> typeCategory = CategoryProduct.BOISSON
                }

                var typeUnite : UnityProduct = UnityProduct.Gramme
                when(unite){
                    1 -> typeUnite = UnityProduct.Gramme
                    2 -> typeUnite = UnityProduct.Portion
                }

                if (uri == null){
                    uri = "null"
                }

                Product.all.add(Product(id, name, typeCategory, LocalDate.parse(date),stock.toDouble(),typeUnite, nutriscore, uri))

                //put into intern BDD

                val product = Product(id, name, typeCategory, LocalDate.parse(date),stock.toDouble(),typeUnite, nutriscore, uri)

                runBlocking {
                    productDao.addProduct(product)
                    //ListeProductCopy.add(product)
                }

                Log.d("Produit: ", "$name $type $date")
            }
            Log.d("liste des produits:", "$result")
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        products_recyclerview.adapter = adap
        Product.all.clear()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getServer()
        }
        super.onResume()
    }

    inner class ActionModeCallback : ActionMode.Callback {
        private var shouldResetRecyclerView = true
        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            val service = retrofit("https://foodlog.min.epf.fr/").create(ProductService::class.java)
            val pref = requireActivity().getApplicationContext().getSharedPreferences(
                "Foodlog",
                Context.MODE_PRIVATE
            )
            val token = pref.getString("token", null);
            val fridge = pref.getInt("fridge", 0)
            when (item?.getItemId()) {

                R.id.action_delete -> {
                    shouldResetRecyclerView = false
                    Log.d("EPF", "${adap?.selectedIds}")
                    //adap?.deleteSelectedIds()
                    val selectedIdIteration = adap?.selectedIds?.listIterator();
                    adap?.selectedIds?.map {
                        runBlocking {
                            service.deleteProduct("${token}", fridge, it)
                        }
                    }

                    ListProductFragment.isMultiSelectOn = false

                    adap?.selectedIds?.clear()
                    adap?.notifyDataSetChanged()
                    Log.d("after", "${adap?.selectedIds}")
                    actionMode?.setTitle("") //remove item count from action mode.
                    actionMode?.finish()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        onResume()
                    }
                    return true
                }
            }
            return false
        }
        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            val inflater = mode?.getMenuInflater()
            inflater?.inflate(R.menu.detail_product, menu)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            menu?.findItem(R.id.action_delete)?.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
            return true
        }

        override fun onDestroyActionMode(mode: ActionMode?) {
            Log.d("EPF", "destroy")
            if (shouldResetRecyclerView) {
                adap?.selectedIds?.clear()
                adap?.notifyDataSetChanged()
            }
            ListProductFragment.isMultiSelectOn = false
            actionMode = null
            shouldResetRecyclerView = true
        }

      }



}