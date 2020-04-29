package fr.epf.foodlog.barecode

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import fr.epf.foodlog.R
import kotlinx.android.synthetic.main.activity_barrecode.*
import kotlinx.android.synthetic.main.activity_barrecode.btnScan
import kotlinx.android.synthetic.main.activity_barrecode_main.*

class BarrecodeMainActivity : AppCompatActivity() {
    var scannedResult: String = ""
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_barrecode_main)

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

    }
