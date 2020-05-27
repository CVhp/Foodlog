package fr.epf.foodlog.Options

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Camera
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.text.TextBlock
import com.google.android.gms.vision.text.TextRecognizer
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import fr.epf.foodlog.R
import fr.epf.foodlog.service.OpenFoodFactsAPI
import fr.epf.foodlog.service.ProductService
import fr.epf.foodlog.service.retrofit
import kotlinx.android.synthetic.main.activity_add_product.*
import kotlinx.android.synthetic.main.stock_dialog.view.*
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.util.*
import kotlin.properties.Delegates

class AddProductActivity : AppCompatActivity() {

    private var scannedResult: String = ""
    private var mDisplayDate: TextView? = null
    private var mDateSetListener: DatePickerDialog.OnDateSetListener? = null
    private var camera: CameraSource? = null
    private var Recognizer:TextRecognizer?=null

    private var mCameraSource by Delegates.notNull<CameraSource>()

    private var textRecognizer by Delegates.notNull<TextRecognizer>()
    private lateinit var textdetecter: String
    private val PERMISSION_REQUEST_CAMERA = 100

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)



        layout_date_scanner.visibility = (RelativeLayout.INVISIBLE)

        button_scan_date.setOnClickListener {
            layout_date_scanner.visibility = (RelativeLayout.VISIBLE)
            layout_add_product.visibility = (RelativeLayout.INVISIBLE)
            startCameraSource()
        }
        button_back_scan_date.setOnClickListener {

            layout_add_product.visibility = (RelativeLayout.VISIBLE)
            layout_date_scanner.visibility = (RelativeLayout.INVISIBLE)

            camera=null
            camera!!.release()

        }

        val levels = resources.getStringArray(R.array.level_array)
        val spinner = findViewById<Spinner>(R.id.level_spinner)

        if (spinner != null) {
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item, levels
            )
            spinner.adapter = adapter
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {

                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }
        }

        //                      DATEPICKER
        tvDate.setOnClickListener(View.OnClickListener {
            val cal: Calendar = Calendar.getInstance()
            val year: Int = cal.get(Calendar.YEAR)
            val month: Int = cal.get(Calendar.MONTH)
            val day: Int = cal.get(Calendar.DAY_OF_MONTH)
            val dialog = DatePickerDialog(
                this@AddProductActivity,
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
                tvDate.text = date

            }


        var unite: String
        var NumUnite: Int = 0
        var stock: String = ""
        /*
        stock = Add_enterQuantite.text.toString()
        Log.d("stockAPI", "affiche stock")
        Log.d("stockAPI", "${stock}")*/

        Add_enterQuantite.setOnClickListener {
            Log.d("stockAPI", "enter Add_enterQuantite")
            unite = unite_spinner.selectedItem as String
            val mDialogView = LayoutInflater.from(this).inflate(R.layout.stock_dialog, null)
            // Set a SeekBar change listener
            mDialogView.seekBar.setOnSeekBarChangeListener(object :
                SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                    // Display the current progress of SeekBar
                    mDialogView.text_view_seekbar.text = "$i"
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {
                    // Do something
                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {
                    // Do something
                }
            })

            // Set le input stock
            if (unite == "Portions") {
                mDialogView.seekBar.setVisibility(View.VISIBLE);
                mDialogView.text_view_seekbar.setVisibility(View.VISIBLE);
                mDialogView.product_dialog_unite.setText("portions")
                NumUnite = 2
            } else {
                mDialogView.stock_edittext.setVisibility(View.VISIBLE);
                mDialogView.product_dialog_unite.setText("grammes")
                NumUnite = 1
            }
            //AlertDialogBuilder
            val mBuilder = AlertDialog.Builder(this)
                .setView(mDialogView)
                .setTitle("Ajout de la quantité")
            //show dialog
            val mAlertDialog = mBuilder.show()

            //add button click of custom layout
            mDialogView.dialogValiderBtn.setOnClickListener {
                //dismiss dialog
                mAlertDialog.dismiss()
                //get text from EditTexts of custom layout
                if (unite == "Portions") {
                    stock = mDialogView.text_view_seekbar.text.toString()
                } else {
                    stock = mDialogView.stock_edittext.text.toString()
                }
                //set the input text in TextView
                if (stock != "")
                    Add_enterQuantite.setText(stock)
                else
                    Add_enterQuantite.setText("Entrez la quantité")

            }

            //cancel button click of custom layout
            mDialogView.dialogCancelBtn.setOnClickListener {
                //dismiss dialog
                mAlertDialog.dismiss()
            }
        }

        //                      AFFICHAGE LOGCAT
        add_product_button.setOnClickListener {
            val name = lastname_edittext.text.toString()
            val type: String = level_spinner.selectedItem as String
            val stockEntre = Add_enterQuantite.text.toString()
            var typeProduct: Int = 0
            when (type) {
                "LEGUME" -> typeProduct = 2
                "FRUIT" -> typeProduct = 1
                "CEREALE" -> typeProduct = 3
                "LAITIER" -> typeProduct = 4
                "SUCRE" -> typeProduct = 5
                "SALE" -> typeProduct = 6
                "VIANDE" -> typeProduct = 7
                "POISSON" -> typeProduct = 8
                "BOISSON" -> typeProduct = 9
            }

            val date = LocalDate.parse(tvDate.text)
            val nutriscore = nutriscore_EditText.text.toString()


            getServer(name, typeProduct.toString(), date.toString(), stockEntre, NumUnite, nutriscore)
            //Product.all.add(Product("${lastname}",typeProduct,date))

            this@AddProductActivity.runOnUiThread (Runnable{
                finish()
            })
        }

        fab_scan.setOnClickListener {
            run {
                IntentIntegrator(this@AddProductActivity).initiateScan();
            }
        }

    }

    private fun getServer(name: String, type: String, date: String, stock: String, unite: Int, nutriscore: String) {
        val service = retrofit("https://foodlog.min.epf.fr/").create(ProductService::class.java)
        val pref = applicationContext.getSharedPreferences(
            "Foodlog",
            Context.MODE_PRIVATE
        )
        val token = pref.getString("token", null);
        runBlocking {
            val result = service.postProduct(
                "${token}",
                "${name}",
                "${type}",
                "${date}",
                "${stock}",
                "$unite",
                "${nutriscore}"
            )
        }
    }

    private fun callOFFTest(id: String) {
        val service =
            retrofit("https://world.openfoodfacts.org/").create(OpenFoodFactsAPI::class.java)

        runBlocking {
            val result = service.loadAPIResponse(id)
            lastname_edittext.setText(result.product.product_name)
            val quantity = result.product.quantity.filter { it.isDigit() }
            Add_enterQuantite.setText(quantity)
            val unity = result.product.quantity.filter { it.isLetter() }
            if (unity == "g") {
                unite_spinner.setSelection(0)
            } else {
                unite_spinner.setSelection(1)
            }

            nutriscore_EditText.setText(result.product.nutrition_grade_fr)

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result: IntentResult? =
            IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

        if (result != null) {
            if (result.contents != null) {
                scannedResult = result.contents
                val ids = scannedResult
                //callOFF(ids)
                callOFFTest(ids)
            } else {
                Toast.makeText(
                    this@AddProductActivity,
                    "Scan failed", //Insert Product in database
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }


    private fun startCameraSource() {

        //textRecognizer = TextRecognizer.Builder(this).build()
        Recognizer=TextRecognizer.Builder(this).build()
        camera = CameraSource.Builder(applicationContext, Recognizer)
            .setFacing(CameraSource.CAMERA_FACING_BACK)
            .setRequestedPreviewSize(1280, 1024)
            .setAutoFocusEnabled(true)
            .setRequestedFps(2.0f)
            .build()


        if (!Recognizer!!.isOperational) {
            Log.d("syst", "Dependencies are downloading....try after few moment")
            return
        }
        camera!!.start(surface_camera_preview.holder)

        surface_camera_preview.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceChanged(p0: SurfaceHolder?, p1: Int, p2: Int, p3: Int) {
                Log.d("syst", "surface changed")
            }

            override fun surfaceDestroyed(p0: SurfaceHolder?) {
                camera!!.stop()
            }

            @SuppressLint("MissingPermission")
            override fun surfaceCreated(p0: SurfaceHolder?) {
                Log.d("syst", "surfaceCreated")
                try {
                    if (isCameraPermissionGranted()) {
                        camera!!.start(surface_camera_preview.holder)
                    } else {
                        requestForPermission()
                    }
                } catch (e: Exception) {
                    //toast("Error:  ${e.message}")
                }
            }
        })

        Recognizer!!.setProcessor(object : Detector.Processor<TextBlock> {
            override fun release() {}

            override fun receiveDetections(detections: Detector.Detections<TextBlock>) {
                Log.d("syst", "receiveDetections")
                val items = detections.detectedItems

                if (items.size() <= 0) {
                    return
                }

                val stringBuilder = StringBuilder()
                for (i in 0 until items.size()) {
                    val item = items.valueAt(i)
                    stringBuilder.append(item.value)
                    stringBuilder.append("\n")
                }
                textdetecter = stringBuilder.toString()
                val date = detectDate(textdetecter)
                Log.d("detection", textdetecter.toString())
                if (date === "" ) {
                } else {
                    Log.d("StopCam", "StopCam")


                    this@AddProductActivity.runOnUiThread (Runnable{
                        // val handler = Handler(Looper.getMainLooper())
                        //handler.post(Runnable {

                        layout_date_scanner.visibility = (RelativeLayout.INVISIBLE)
                        layout_add_product.visibility = (RelativeLayout.VISIBLE)
                        tvDate.text = date })

                    camera=null
                    Recognizer=null
                    camera!!.release()


                    //   })
                    //  mCameraSource.release()

                }
            }
        })
    }

    fun isCameraPermissionGranted(): Boolean {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
    }

    private fun requestForPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            PERMISSION_REQUEST_CAMERA
        )
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode != PERMISSION_REQUEST_CAMERA) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            return
        }

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (isCameraPermissionGranted()) {
                camera!!.start(surface_camera_preview.holder)
            } else {
                // toast("Permission need to grant")
                Log.d("syst", "Permission need to grant")
                finish()
            }
        }
    }

    fun detectDate(text: String): String {

        //var date:String="02 04 2019" //(cas numero 1)
        //var date:String="2019-04/03" //(cas numero 2)
        // var date: String = "19-04.20" //(cas numero 3)
        // var date:String="2014-02" // (cas numero 4)
        // var date="06/2024" // (cas numero 5)
        // var date="04/22" // (cas numero 6)

        val a: String = "(/|.|\\s)" // = separator

        val date = text
        var pattern = Regex("\\d{2}$a\\d{2}$a\\d{4}")
        if (pattern.containsMatchIn(date)) {
            val matchResult = pattern.find(date)
            val b = matchResult!!.value.replace(".", "-").replace("/", "-").replace("\\s", "-")
            val y = b.substring(6)    // annee
            val d = b.substring(0, 2)  // jours
            val m = b.substring(3, 5)  // mois
            return "$y-$m-$d"
        } else {
            pattern = Regex("\\d{4}$a\\d{2}$a\\d{2}")
            if (pattern.containsMatchIn(date)) {
                val matchResult = pattern.find(date)
                return matchResult!!.value.replace(".", "-").replace("/", "-").replace("\\s", "-")
            } else {
                pattern = Regex("\\d{2}$a\\d{2}$a\\d{2}")
                if (pattern.containsMatchIn(date)) {
                    val matchResult = pattern.find(date)
                    val b =
                        matchResult!!.value.replace(".", "-").replace("/", "-").replace("\\s", "-")
                    val d = b.substring(0, 2)
                    val m = b.substring(3, 5)
                    val y = "20" + b.substring(6)
                    return "$y-$m-$d"
                } else {
                    pattern = Regex("\\d{4}$a\\d{2}")
                    if (pattern.containsMatchIn(date)) {
                        val matchResult = pattern.find(date)
                        val b = matchResult!!.value.replace(".", "-").replace("/", "-")
                            .replace("\\s", "-")
                        val d = "25"
                        val m = b.substring(5)
                        val y = b.substring(0, 4)
                        return "$y-$m-$d"
                    } else {
                        pattern = Regex("\\d{2}$a\\d{4}")
                        if (pattern.containsMatchIn(date)) {
                            val matchResult = pattern.find(date)
                            val b = matchResult!!.value.replace(".", "-").replace("/", "-")
                                .replace("\\s", "-")
                            val d = "25"
                            val m = b.substring(0, 2)
                            val y = b.substring(3)
                            return "$y-$m-$d"
                        } else {
                            pattern = Regex("\\d{2}$a\\d{2}")
                            if (pattern.containsMatchIn(date)) {
                                val matchResult = pattern.find(date)
                                val b = matchResult!!.value.replace(".", "-").replace("/", "-")
                                    .replace("\\s", "-")
                                val d = "25"
                                val m = b.substring(0, 2)
                                val y = "20" + b.substring(3)
                                return "$y-$m-$d"
                            }
                        }
                    }

                }
            }
        }

        return ""

    }


}
