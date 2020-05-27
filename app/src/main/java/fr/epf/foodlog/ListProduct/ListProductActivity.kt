package fr.epf.foodlog.ListProduct

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.RequiresApi
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import androidx.work.WorkManager
import fr.epf.foodlog.LoadingActivities.LoadingActivity
import fr.epf.foodlog.Notif.SettingsActivity
import fr.epf.foodlog.Options.AddProductActivity
import fr.epf.foodlog.Options.RecetteActivity
import fr.epf.foodlog.R
import fr.epf.foodlog.data.AppDataBase
import fr.epf.foodlog.data.ProductDao
import fr.epf.foodlog.model.CategoryProduct
import fr.epf.foodlog.model.Product
import fr.epf.foodlog.model.UnityProduct
import fr.epf.foodlog.service.ProductService
import fr.epf.foodlog.service.retrofit
import kotlinx.android.synthetic.main.activity_list_product.*
import kotlinx.coroutines.runBlocking
import java.time.LocalDate

class ListProductActivity : AppCompatActivity(), ProductInterface {

    var adap: ProductAdapter? = null
    var actionMode: ActionMode? = null
    var instanceWorkManager = WorkManager.getInstance(this)

    companion object {
        var isMultiSelectOn = false
    }

    override fun productInterface(size: Int) {
        if (actionMode == null) actionMode = startActionMode(ActionModeCallback())
        if (size > 0) actionMode?.setTitle("$size")
        else actionMode?.finish()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {

        this@ListProductActivity.runOnUiThread (Runnable {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_list_product)

            adap = ProductAdapter(this,this, Product.all)
            isMultiSelectOn = false

            products_recyclerview.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            products_recyclerview.adapter =
                adap

            val fab: FloatingActionButton = findViewById(R.id.fab)
            fab.setOnClickListener {
                val intent = Intent(this, AddProductActivity::class.java)
                startActivity(intent)
            }
            Product.all.clear()
            getServer()
        })
    }

    inner class ActionModeCallback : ActionMode.Callback {
        private var shouldResetRecyclerView = true
        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            val service  = retrofit("https://foodlog.min.epf.fr/").create(ProductService::class.java)
            val pref = applicationContext.getSharedPreferences(
                "Foodlog",
                Context.MODE_PRIVATE
            )
            val token = pref.getString("token", null);
            runBlocking {
                service.deleteProduct("${token}", 48)
            }
            when (item?.getItemId()) {

                R.id.action_delete -> {
                    shouldResetRecyclerView = false
                    Log.d("EPF", "${adap?.selectedIds}")
                    //adap?.deleteSelectedIds()
                    val selectedIdIteration = adap?.selectedIds?.listIterator();
                    adap?.selectedIds?.map{runBlocking {
                        service.deleteProduct("${token}", it )
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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()
        products_recyclerview.adapter = adap
        Product.all.clear()
        getServer()

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getServer(){
        val service  = retrofit("https://foodlog.min.epf.fr/").create(ProductService::class.java)
        val database: AppDataBase = Room.databaseBuilder(this, AppDataBase::class.java, "gestionclients")
            .build()
        val productDao : ProductDao = database.getProductDao()
        runBlocking {
            productDao.deleteProducts() //permet de clear la base de donnée interne products
        }


        runBlocking {
            val pref = applicationContext.getSharedPreferences(
                "Foodlog",
                Context.MODE_PRIVATE
            )
            val token = pref.getString("token", null);
            val result = service.getProducts("$token")
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.list_clients,menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean =

        when(item.itemId){
            R.id.action_logout -> {

                val pref = applicationContext.getSharedPreferences("Foodlog",Context.MODE_PRIVATE)
                val editor: SharedPreferences.Editor = pref.edit()
                editor.clear()
                editor.apply()
                val intent = Intent(this, LoadingActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.action_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.action_recette -> {
                val intent = Intent(this, RecetteActivity::class.java)
                startActivity(intent)
                true
            }
            else -> true
        }

}


