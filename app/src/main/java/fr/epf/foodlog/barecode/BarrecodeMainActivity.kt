package fr.epf.foodlog.barecode

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import fr.epf.foodlog.Common.CommonOFF
import fr.epf.foodlog.R
import fr.epf.foodlog.data.OpenFoodFactsAPI
import fr.epf.foodlog.model.APIResponse
import fr.epf.foodlog.service.ProductService
import fr.epf.foodlog.service.retrofit
import kotlinx.android.synthetic.main.activity_barrecode.btnScan
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BarrecodeMainActivity : AppCompatActivity() {
    var scannedResult: String = ""
    var ids = "0"
    internal lateinit var mService: OpenFoodFactsAPI
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_barrecode_main)
            mService = CommonOFF.api

            btnScan.setOnClickListener {
                run {
                    IntentIntegrator(this@BarrecodeMainActivity).initiateScan();
                }
            }
        }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

            var result: IntentResult? = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

            if(result != null){

                if(result.contents != null){
                    scannedResult = result.contents
                  //  txtValue.text = scannedResult
                    ids=scannedResult
                callOFF(ids)
                } else {
                   //txtValue.text = "scan failed"
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }


        override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
            super.onRestoreInstanceState(savedInstanceState!!)

            savedInstanceState?.let {
                scannedResult = it.getString("scannedResult").toString()
              //  txtValue.text = scannedResult
            }
        }
    private fun callOFF(id: String){

        runBlocking { mService.loadAPIResponse(id).enqueue(object: Callback<APIResponse> {
            override fun onFailure(call: Call<APIResponse>, t: Throwable) {
                Toast.makeText(
                    this@BarrecodeMainActivity,
                    "An error occured",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onResponse(call: Call<APIResponse>, response: Response<APIResponse>) {

                if(response.isSuccessful){
                  val  a=response.body()
                    if(a!!.getStatus() != 0){
                        Toast.makeText(
                            this@BarrecodeMainActivity,
                            a.getStatus().toString(), //Insert Product in database
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    if(a.getProduct() != null){
                     val   product=a.getProduct()
                        val name= product!!.getProductName()
                        Toast.makeText(
                            this@BarrecodeMainActivity,
                            name, //Insert Product in database
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    else{
                        Toast.makeText(
                            this@BarrecodeMainActivity,
                            "ProductNotOK", //Insert Product in database
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                }

       //        val name=response.body()!!.productOFF!!.getGenericName()
                val type="1"
           //     val date = LocalDate.parse(tv_testDate.text)
                val stock="2"
                val unite=1
                val service  = retrofit().create(ProductService::class.java)
                val pref = applicationContext.getSharedPreferences(
                    "Foodlog",
                    Context.MODE_PRIVATE
                )
                val token = pref.getString("token", null);

                //if(!token.isEmpty()) {
                ////    getServer(name!!, type, date.toString(), stock, unite, token!!)
                //}
                //resposne.body()!!.product
                //product adapter

                //postproduct
            }

        }) }
    }

    private fun getServer(name : String, type : String, date : String, stock:String, unite:Int, id_client : String){
        val service  = retrofit().create(ProductService::class.java)
        val pref = applicationContext.getSharedPreferences(
            "Foodlog",
            Context.MODE_PRIVATE
        )
        val token = pref.getString("token", null);
        runBlocking {
            val result = service.postProduct("${token}", "${name}", "${type}","${date}", "${stock}", "$unite")
        }
    }


    }
