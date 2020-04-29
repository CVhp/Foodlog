package fr.epf.foodlog.barecode

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import fr.epf.foodlog.Common.CommonOFF
import fr.epf.foodlog.R
import kotlinx.android.synthetic.main.activity_barrecode.*
import kotlinx.android.synthetic.main.activity_barrecode.btnScan
import kotlinx.android.synthetic.main.activity_barrecode_main.*
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
                    txtValue.text = scannedResult
                    ids=scannedResult
                callAPI(ids)
                } else {
                    txtValue.text = "scan failed"
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }


        override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
            super.onRestoreInstanceState(savedInstanceState!!)

            savedInstanceState?.let {
                scannedResult = it.getString("scannedResult").toString()
                txtValue.text = scannedResult
            }
        }
    private fun callAPI(id: String){

        runBlocking { mService.loadAPIResponse(id).enqueue(object: Callback<APIResponse> {
            override fun onFailure(call: Call<APIResponse>, t: Throwable) {
                Toast.makeText(
                    this@BarrecodeMainActivity,
                    "An error occured",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onResponse(call: Call<APIResponse>, response: Response<APIResponse>) {
                Toast.makeText(
                    this@BarrecodeMainActivity,
                    response.body()!!.code.toString(), //Insert Product in database
                    Toast.LENGTH_SHORT
                ).show()
            }

        }) }
    }


    }
