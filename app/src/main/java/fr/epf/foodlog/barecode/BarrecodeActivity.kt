package fr.epf.foodlog.barecode

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.CaptureManager
import fr.epf.foodlog.Common.CommonOFF
import kotlinx.android.synthetic.main.activity_barrecode.*
import kotlinx.android.synthetic.main.activity_barrecode.btnScan
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BarrecodeActivity : AppCompatActivity() {

    lateinit var captureManager: CaptureManager



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        captureManager = CaptureManager(this, barcodeView)
        captureManager.initializeFromIntent(intent, savedInstanceState)


          btnScan.setOnClickListener {
            txtResult.text = "scanning..."
            barcodeView.decodeSingle(object : BarcodeCallback {

                override fun barcodeResult(result: BarcodeResult?) {
                    result?.let {
                        txtResult.text = it.text


                        val vib: Vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

                        if(vib.hasVibrator()){
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                                // void vibrate (VibrationEffect vibe)
                                vib.vibrate(
                                    VibrationEffect.createOneShot(
                                        100,
                                        // The default vibration strength of the device.
                                        VibrationEffect.DEFAULT_AMPLITUDE
                                    )
                                )
                            }else{
                                // This method was deprecated in API level 26
                                vib.vibrate(100)
                            }
                        }
                    }
                }

                override fun possibleResultPoints(resultPoints: MutableList<ResultPoint>?) {
                    //
                }
            })
        }


    }
    override fun onPause() {
        super.onPause()
        captureManager.onPause()
    }

    override fun onResume() {
        super.onResume()
        captureManager.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        captureManager.onDestroy()
    }
}

