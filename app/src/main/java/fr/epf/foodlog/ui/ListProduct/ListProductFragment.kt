package fr.epf.foodlog.ui.ListProduct

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.floatingactionbutton.FloatingActionButton
import fr.epf.foodlog.ListProduct.ProductAdapter
import fr.epf.foodlog.ListProduct.ProductInterface
import fr.epf.foodlog.LoadingActivities.LoadingActivity
import fr.epf.foodlog.R
import fr.epf.foodlog.data.AppDataBase
import fr.epf.foodlog.data.ProductDao
import fr.epf.foodlog.model.CategoryProduct
import fr.epf.foodlog.model.Product
import fr.epf.foodlog.model.UnityProduct
import fr.epf.foodlog.service.ProductService
import fr.epf.foodlog.service.retrofit
import kotlinx.android.synthetic.main.fragment_list_product.view.*
import kotlinx.coroutines.runBlocking
import java.time.LocalDate

class ListProductFragment : Fragment(), ProductInterface {

    var adap: ProductAdapter? = null
    var actionMode: ActionMode? = null
    private var fridge=0
    lateinit var products_recyclerview : RecyclerView
    private var profile =0

    companion object {
        var isMultiSelectOn = false
    }

    override fun productInterface(size: Int) {
        if (actionMode == null) actionMode = requireActivity().startActionMode(ActionModeCallback())
        if (size > 0) actionMode?.setTitle("$size")
        else actionMode?.finish()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        arguments?.let{

            val safeArgs= ListProductFragmentArgs.fromBundle(it)
            fridge=safeArgs.fridge

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
             profile = pref.getInt("profile",0)
        }

        val root = inflater.inflate(R.layout.fragment_list_product, container, false)


        root.fab.isEnabled= (profile >= 4)
        root.fab.isVisible= (profile >= 4)
        requireActivity().runOnUiThread (Runnable {

            products_recyclerview = root.findViewById<View>(R.id.products_recyclerview) as RecyclerView

            adap = ProductAdapter(requireContext(), this, Product.all)
            isMultiSelectOn = false

            products_recyclerview.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            products_recyclerview.adapter =
                adap

            var fab = root.findViewById<FloatingActionButton>(R.id.fab)
            fab.setOnClickListener {
                //ADDPRODUCT FRAGMENT
                val bundle = Bundle()
                Navigation.findNavController(it).navigate(R.id.navigate_to_addProduct_fragment,bundle);
            }
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

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main, menu)
        if(profile<10){
            menu.findItem(R.id.action_invitation).isVisible=false
            menu.findItem(R.id.action_invitation).isEnabled=false
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_logout -> {
                val pref = requireActivity().getApplicationContext().getSharedPreferences("Foodlog",Context.MODE_PRIVATE)
                val editor: SharedPreferences.Editor = pref.edit()
                editor.clear()
                editor.apply()
                val intent = Intent(requireContext(), LoadingActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.action_settings ->{
                var bundle = Bundle()
                Navigation.findNavController(requireView()).navigate(R.id.navigate_to_settings_fragment, bundle)
            }
            R.id.action_invitation->{
                val target = ListProductFragmentDirections.actionNavListproductToNavInvitationCreate()
                Navigation.findNavController(requireView()).navigate(target)
            }
            R.id.action_search->{
                Navigation.findNavController(requireView()).navigate(R.id.navigate_to_vacances_fragment)
            }

            else -> true
        }
        return super.onOptionsItemSelected(item)
    }

    inner class ActionModeCallback : ActionMode.Callback {
        private var shouldResetRecyclerView = true
        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            val service  = retrofit("https://foodlog.min.epf.fr/").create(ProductService::class.java)
            val pref = requireActivity().getApplicationContext().getSharedPreferences(
                "Foodlog",
                Context.MODE_PRIVATE
            )
            val token = pref.getString("token", null);
            val fridge=pref.getInt("fridge",0);
            //runBlocking {
                //service.deleteProduct("${token}",fridge, 48)
            //}
            when (item?.getItemId()) {

                R.id.action_delete -> {
                    shouldResetRecyclerView = false
                    Log.d("EPF", "${adap?.selectedIds}")
                    //adap?.deleteSelectedIds()
                    val selectedIdIteration = adap?.selectedIds?.listIterator();
                    adap?.selectedIds?.map{runBlocking {
                        service.deleteProduct("${token}",fridge, it)
                    }}
                    //while (selectedIdIteration!!.hasNext()) {
                    Log.d("1", "${adap?.selectedIds}")
                    //adap?.selectedIds?.map
                    //val selectedItemID = selectedIdIteration?.next()
                    //var indexOfModelList = 0

                    //val modelListIteration = adap?.modelList?.listIterator();
                    //while (modelListIteration!!.hasNext()) {
                    //Log.d("2", "${adap?.selectedIds}")
                    //val model = modelListIteration.next()
                    //if (selectedItemID.equals(model.id)) {
                    //runBlocking {
                    //service.deleteProduct("${token}", selectedItemID )
                    //}
                    //Log.d("3", "${adap?.selectedIds}")
                    //modelListIteration.remove()
                    //selectedIdIteration.remove()
                    //notifyItemRemoved(indexOfModelList)
                    //}
                    //indexOfModelList++
                    //}
                    isMultiSelectOn = false

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
            isMultiSelectOn = false
            actionMode = null
            shouldResetRecyclerView = true
        }
    }
}
