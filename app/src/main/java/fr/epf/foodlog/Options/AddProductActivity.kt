package fr.epf.foodlog.Options

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
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

class AddProductActivity : AppCompatActivity() {

    private var scannedResult: String = ""
    private var mDisplayDate: TextView? = null
    private var mDateSetListener: DatePickerDialog.OnDateSetListener? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)

        val levels = resources.getStringArray(R.array.level_array)
        val spinner = findViewById<Spinner>(R.id.level_spinner)

        if (spinner != null) {
            val adapter = ArrayAdapter(this,
                android.R.layout.simple_spinner_item, levels)
            spinner.adapter = adapter
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {

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
                var date_month : String = ""
                var date_day : String = ""
                month = month + 1
                if ("${month}".length == 1){
                    date_month = "0$month"
                } else {
                    date_month = "$month"
                }

                if ("${day}".length == 1){
                    date_day = "0$day"
                } else {
                    date_day = "$day"
                }
                val date = "$year-$date_month-$date_day"
                tvDate.text = date

            }



        var unite:String
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
            mDialogView.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
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
            if (unite=="Portions"){
                mDialogView.seekBar.setVisibility(View.VISIBLE);
                mDialogView.text_view_seekbar.setVisibility(View.VISIBLE);
                mDialogView.product_dialog_unite.setText("portions")
                NumUnite = 2
            }
            else {
                mDialogView.stock_edittext.setVisibility(View.VISIBLE);
                mDialogView.product_dialog_unite.setText("grammes")
                NumUnite = 1
            }
            //AlertDialogBuilder
            val mBuilder = AlertDialog.Builder(this)
                .setView(mDialogView)
                .setTitle("Ajout de la quantité")
            //show dialog
            val  mAlertDialog = mBuilder.show()

            //add button click of custom layout
            mDialogView.dialogValiderBtn.setOnClickListener {
                //dismiss dialog
                mAlertDialog.dismiss()
                //get text from EditTexts of custom layout
                if (unite=="Portions"){
                    stock = mDialogView.text_view_seekbar.text.toString()
                }
                else {
                    stock = mDialogView.stock_edittext.text.toString()
                }
                //set the input text in TextView
                if(stock != "")
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
            val type : String = level_spinner.selectedItem as String
            val stockEntre = Add_enterQuantite.text.toString()
            var typeProduct : Int = 0
            when(type){
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


            getServer(name, typeProduct.toString(), date.toString(), stockEntre, NumUnite)
            //Product.all.add(Product("${lastname}",typeProduct,date))

            finish()
        }

        fab_scan.setOnClickListener{
            run {
                IntentIntegrator(this@AddProductActivity).initiateScan();
            }
        }

    }

    private fun getServer(name : String, type : String, date : String, stock:String, unite:Int){
        val service  = retrofit("https://foodlog.min.epf.fr/").create(ProductService::class.java)
        val pref = applicationContext.getSharedPreferences(
            "Foodlog",
            Context.MODE_PRIVATE
        )
        val token = pref.getString("token", null);
        runBlocking {
            val result = service.postProduct("${token}", "${name}", "${type}","${date}", "${stock}", "$unite")
        }
    }

    private fun callOFFTest(id: String){
        val service  = retrofit("https://world.openfoodfacts.org/").create(OpenFoodFactsAPI::class.java)

        runBlocking {
            val result = service.loadAPIResponse(id)
            lastname_edittext.setText(result.product.product_name)
            val quantity = result.product.quantity.filter { it.isDigit() }
            Add_enterQuantite.setText(quantity)
            val unity = result.product.quantity.filter { it.isLetter() }
            if (unity == "g"){
                unite_spinner.setSelection(0)
            } else {
                unite_spinner.setSelection(1)
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result: IntentResult? = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

        if(result != null){
            if(result.contents != null){
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



}
