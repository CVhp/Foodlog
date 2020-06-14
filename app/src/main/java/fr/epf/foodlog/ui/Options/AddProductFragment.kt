package fr.epf.foodlog.ui.Options

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
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
import kotlinx.android.synthetic.main.fragment_add_product.*
import kotlinx.android.synthetic.main.fragment_add_product.view.*
import kotlinx.android.synthetic.main.stock_dialog.view.*
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.util.*
import kotlin.properties.Delegates


class AddProductFragment : Fragment() {

    private var scannedResult: String = ""
    private var mDisplayDate: TextView? = null
    private var mDateSetListener: DatePickerDialog.OnDateSetListener? = null
    private var Recognizer:TextRecognizer?=null
    private var uri:String?=null

    private var mCameraSource:CameraSource? = null
    private lateinit var textdetecter: String
    private val PERMISSION_REQUEST_CAMERA = 100
    private lateinit var ListDate: MutableList<String>

    lateinit var root : View

    @SuppressLint("SourceLockedOrientationActivity")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        root =  inflater.inflate(R.layout.fragment_add_product, container, false)

        root.layout_date_scanner.visibility = (RelativeLayout.INVISIBLE)

        root.button_scan_date.setOnClickListener {
            root.layout_date_scanner.visibility = (RelativeLayout.VISIBLE)
            root.layout_add_product.visibility = (RelativeLayout.INVISIBLE)
            startCameraSource()
        }
        root.button_back_scan_date.setOnClickListener {
            root.layout_add_product.visibility = (RelativeLayout.VISIBLE)
            root.layout_date_scanner.visibility = (RelativeLayout.INVISIBLE)

            val handler = Handler(Looper.getMainLooper())
            handler.post {
                if (mCameraSource != null) {
                    mCameraSource!!.release();
                    mCameraSource = null;
                }}
        }
        ListDate = arrayListOf()

        val levels = resources.getStringArray(R.array.level_array)
        val spinner = root.findViewById<Spinner>(R.id.level_spinner)

        if (spinner != null) {
            val adapter = ArrayAdapter(
                requireContext(),
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


        var unite: String
        var NumUnite: Int = 0
        var stock: String = ""
        /*
        stock = Add_enterQuantite.text.toString()
        Log.d("stockAPI", "affiche stock")
        Log.d("stockAPI", "${stock}")*/

        root.Add_enterQuantite.setOnClickListener {
            Log.d("stockAPI", "enter Add_enterQuantite")
            unite = root.unite_spinner.selectedItem as String
            val mDialogView = LayoutInflater.from(requireContext()).inflate(R.layout.stock_dialog, null)
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
            if (unite == "portions") {
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
            val mBuilder = AlertDialog.Builder(requireContext())
                .setView(mDialogView)
                .setTitle("Ajout de la quantité")
            //show dialog
            val mAlertDialog = mBuilder.show()

            //add button click of custom layout
            mDialogView.dialogValiderBtn.setOnClickListener {
                //dismiss dialog
                mAlertDialog.dismiss()
                //get text from EditTexts of custom layout
                if (unite == "portions") {
                    stock = mDialogView.text_view_seekbar.text.toString()
                } else {
                    stock = mDialogView.stock_edittext.text.toString()
                }
                //set the input text in TextView
                if (stock != "")
                    root.Add_enterQuantite.setText(stock)
                else
                    root.Add_enterQuantite.setText("Entrez la quantité")

            }

            //cancel button click of custom layout
            mDialogView.dialogCancelBtn.setOnClickListener {
                //dismiss dialog
                mAlertDialog.dismiss()
            }
        }

        //                      AFFICHAGE LOGCAT
        root.add_product_button.setOnClickListener {
            val name = root.lastname_edittext.text.toString()
            val type: String = root.level_spinner.selectedItem as String
            val stockEntre = root.Add_enterQuantite.text.toString()
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

            val date : LocalDate
            if (root.tvDate.text != ""){
                date = LocalDate.parse(root.tvDate.text)
                val nutriscore = root.nutriscore_TextView.text.toString()
                getServer(name, typeProduct.toString(), date.toString(), stockEntre, NumUnite, nutriscore)
            } else {
                Toast.makeText(requireContext(),"Veuillez choisir une date", Toast.LENGTH_SHORT).show()
            }

            //val nutriscore = root.nutriscore_TextView.text.toString()



            //Product.all.add(Product("${lastname}",typeProduct,date))
            //val bundle = Bundle()
            //Navigation.findNavController(it).navigate(R.id.return_to_listProduct_fragment, bundle);

            val pref = requireActivity().getApplicationContext().getSharedPreferences(
                "Foodlog",
                Context.MODE_PRIVATE
            )
            val fridge=pref.getInt("fridge",0);
            val target =AddProductFragmentDirections.returnToListProductFragment(fridge)
            Navigation.findNavController(requireView()).navigate(target);

        }

        root.fab_scan.setOnClickListener {
            root.nutriscore_TextView.visibility = View.VISIBLE
            root.text_add_nutriscore.visibility = View.VISIBLE
            run {
                Log.d("frt", "commence scan")
                IntentIntegrator.forSupportFragment(this).initiateScan()
            }
        }

        if(root.nutriscore_TextView.text == null ){
            root.nutriscore_TextView.visibility = View.GONE
            root.text_add_nutriscore.visibility = View.GONE
        }
        return root
    }

    private fun getServer(name: String, type: String, date: String, stock: String, unite: Int, nutriscore: String) {
        val service = retrofit("https://foodlog.min.epf.fr/").create(ProductService::class.java)
        val pref = requireActivity().getApplicationContext().getSharedPreferences(
            "Foodlog",
            Context.MODE_PRIVATE
        )
        val token = pref.getString("token", null);
        val fridge=pref.getInt("fridge",0);
       // uri=null
        runBlocking {
            val result = service.postProduct(
                "${token}",
                "${name}",
                "${type}",
                "${date}",
                "${stock}",
                "$unite",
                fridge,
                "${nutriscore}",
                uri
            )
        }
    }

    private fun callOFFTest(id: String) {
        val service =
            retrofit("https://world.openfoodfacts.org/").create(OpenFoodFactsAPI::class.java)

        runBlocking {
            val result = service.loadAPIResponse(id)
            Log.d("frt", "enter callOFF")
            root.lastname_edittext.setText(result.product.product_name)
            Log.d("frt", "${result.product.product_name}")
            Log.d("frt", "${result.product.quantity}")
            Log.d("frt", "${result.product.nutrition_grade_fr}")
            val quantity = result.product.quantity.filter { it.isDigit() }
            Log.d("frt", "${quantity}")

            uri=result.product.image_front_url



            root.Add_enterQuantite.text = quantity
            val unity = result.product.quantity.filter { it.isLetter() }
            if (unity == "g") {
                root.unite_spinner.setSelection(0)
            } else {
                root.unite_spinner.setSelection(1)
            }

            root.nutriscore_TextView.text = result.product.nutrition_grade_fr
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result: IntentResult? =
            IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

        Log.d("frt", "enter result")
        Log.d("frt", "${result}")
        if (result != null) {
            if (result.contents != null) {
                scannedResult = result.contents
                val ids = scannedResult
                //callOFF(ids)
                callOFFTest(ids)
            } else {
                Toast.makeText(
                    requireContext(),
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
        Recognizer = TextRecognizer.Builder(requireContext()).build()
        mCameraSource = CameraSource.Builder(requireActivity().getApplicationContext(), Recognizer)
            .setFacing(CameraSource.CAMERA_FACING_BACK)
            .setRequestedPreviewSize(1280, 1024)
            .setAutoFocusEnabled(true)
            .setRequestedFps(10.0f)
            .build()


        if (!Recognizer!!.isOperational) {
            Log.d("syst", "Dependencies are downloading....try after few moment")
            return
        }

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestForPermission()
            return
        }

        mCameraSource!!.start(surface_camera_preview.holder)


        surface_camera_preview.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceChanged(p0: SurfaceHolder?, p1: Int, p2: Int, p3: Int) {
                Log.d("syst", "surface changed")
                // camera!!.release()
            }

            override fun surfaceDestroyed(p0: SurfaceHolder?) {
                //camera!!.release()
            }

            @SuppressLint("MissingPermission")
            override fun surfaceCreated(p0: SurfaceHolder?) {
                Log.d("syst", "surfaceCreated")
                try {
                    if (isCameraPermissionGranted()) {
                        mCameraSource!!.start(surface_camera_preview.holder)
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
                if (date != "") {
                    ListDate.add(date)
                    val c= ListDate.count{e-> (e==date)}
                    if (c > 3) {
                        requireActivity().runOnUiThread(Runnable {

                            root.layout_date_scanner.visibility = (RelativeLayout.INVISIBLE)
                            root.layout_add_product.visibility = (RelativeLayout.VISIBLE)
                            root.tvDate.text = date
                            ListDate = arrayListOf()
                        })

                        val handler = Handler(Looper.getMainLooper())
                        handler.post {
                            if (mCameraSource != null) {
                                mCameraSource!!.release();
                                mCameraSource = null;
                            }
                        }
                    }
                }

            }
        })
    }

    fun isCameraPermissionGranted(): Boolean {
        return ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
    }

    private fun requestForPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
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
                mCameraSource!!.start(root.surface_camera_preview.holder)
            } else {
                // toast("Permission need to grant")
                Log.d("syst", "Permission need to grant")
                //finish()
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
            if (((d.toInt() >= 1) && (d.toInt() <= 31) && ((m.toInt() >= 1) && (m.toInt() <= 12)))){
                return "$y-$m-$d"
            }
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
                    if (((d.toInt() >= 1) && (d.toInt() <= 31) && ((m.toInt() >= 1) && (m.toInt() <= 12)))) {
                        return "$y-$m-$d"
                    }
                } else {
                    pattern = Regex("\\d{4}$a\\d{2}")
                    if (pattern.containsMatchIn(date)) {
                        val matchResult = pattern.find(date)
                        val b = matchResult!!.value.replace(".", "-").replace("/", "-")
                            .replace("\\s", "-")
                        val d = "25"
                        val m = b.substring(5)
                        val y = b.substring(0, 4)
                        if ((m.toInt() >= 1) && (m.toInt() <= 12)) {
                            return "$y-$m-$d"
                        }
                    } else {
                        pattern = Regex("\\d{2}$a\\d{4}")
                        if (pattern.containsMatchIn(date)) {
                            val matchResult = pattern.find(date)
                            val b = matchResult!!.value.replace(".", "-").replace("/", "-")
                                .replace("\\s", "-")
                            val d = "25"
                            val m = b.substring(0, 2)
                            val y = b.substring(3)
                            if ((m.toInt() >= 1) && (m.toInt() <= 12)) {
                                return "$y-$m-$d"
                            }
                        } else {
                            pattern = Regex("\\d{2}$a\\d{2}")
                            if (pattern.containsMatchIn(date)) {
                                val matchResult = pattern.find(date)
                                val b = matchResult!!.value.replace(".", "-").replace("/", "-")
                                    .replace("\\s", "-")
                                val d = "25"
                                val m = b.substring(0, 2)
                                val y = "20" + b.substring(3)
                                if ((m.toInt() >= 1) && (m.toInt() <= 12)) {
                                    return "$y-$m-$d"
                                }
                            }
                        }
                    }

                }
            }
        }
        //Toast.makeText(requireContext(), "Date non détectée", Toast.LENGTH_SHORT).show()
        return ""
    }



}
