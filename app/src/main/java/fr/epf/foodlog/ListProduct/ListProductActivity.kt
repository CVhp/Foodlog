package fr.epf.foodlog.ListProduct

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.annotation.RequiresApi
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import fr.epf.foodlog.LoadingActivities.LoadingActivity
import fr.epf.foodlog.Options.AddProductActivity
import fr.epf.foodlog.R
import fr.epf.foodlog.data.AppDataBase
import fr.epf.foodlog.data.ClientDao
import fr.epf.foodlog.data.ProductDao
import fr.epf.foodlog.model.CategoryProduct
import fr.epf.foodlog.model.Client
import fr.epf.foodlog.model.Product
import fr.epf.foodlog.model.UnityProduct
import fr.epf.foodlog.service.ProductService
import fr.epf.foodlog.service.retrofit
import kotlinx.android.synthetic.main.activity_list_product.*
import kotlinx.android.synthetic.main.product_view.*
import kotlinx.android.synthetic.main.product_view.view.*
import kotlinx.coroutines.runBlocking
import java.time.LocalDate

class ListProductActivity : AppCompatActivity() {

    private lateinit var adap: ProductAdapter

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_product)

        products_recyclerview.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        products_recyclerview.adapter =
            ProductAdapter(Product.all)

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this, AddProductActivity::class.java)
            startActivity(intent)
        }
        Product.all.clear()
        getServer()

//        adap=ProductAdapter(Product.all)
//        Log.d("RAAAAHHH","${adap.msg.toString()}")
//        if (adap.msg.toString()=="true"){
//            checkBox.setVisibility(View.VISIBLE)
//        }
//        adap.onItemLongClick= {
//                    products_recyclerview.checkBox.setVisibility(View.VISIBLE)
//                }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()
        products_recyclerview.adapter =
            ProductAdapter(Product.all)
        Product.all.clear()
        getServer()

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getServer(){
        val service  = retrofit().create(ProductService::class.java)
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

                Product.all.add(Product(id, name, typeCategory, LocalDate.parse(date),stock.toDouble(),typeUnite))

                //put into intern BDD

                val product = Product(id, name, typeCategory, LocalDate.parse(date),stock.toDouble(),typeUnite)

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
            else -> true
        }

}


