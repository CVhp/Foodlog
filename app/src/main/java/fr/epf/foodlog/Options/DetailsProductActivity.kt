package fr.epf.foodlog.Options

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import fr.epf.foodlog.R
import fr.epf.foodlog.model.Product
import fr.epf.foodlog.model.UnityProduct
import fr.epf.foodlog.service.ProductService
import fr.epf.foodlog.service.retrofit
import kotlinx.android.synthetic.main.activity_details_product.*
import kotlinx.android.synthetic.main.activity_loading.view.*
import kotlinx.android.synthetic.main.stock_dialog.view.*
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.util.*

class DetailsProductActivity() : AppCompatActivity(){

    private var productLastName : String? = null
    private var id : Int = 0
    private var mDateSetListener: DatePickerDialog.OnDateSetListener? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details_product)

        this.product_imageView_details.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, 100)
        }

        id = intent.getIntExtra("id", 0)

        productLastName = intent.getStringExtra("clientLastName")
        details_nom.text= productLastName

        val clientSexe = intent.getStringExtra("clientGender")
        details_sexe.text= clientSexe

        var stock = intent.getStringExtra("stock")
        details_stock.text= stock

        var RecUnite = intent.getStringExtra("unite")
        var unite: String
        var NumUnite : Int = 0

        if(RecUnite=="Gramme"){
            unite="grammes"
        }
        else
            unite="portions"

        details_unite.text= unite

        product_imageView_details.setImageResource(
            when(clientSexe){
                "VIANDE" -> R.drawable.viande
                "LEGUME" -> R.drawable.aubergine
                "FRUIT" -> R.drawable.pomme
                "POISSON" -> R.drawable.poisson
                "CEREALE" -> R.drawable.cereale
                else -> R.drawable.banane
            }
        )
        /*if (clientSexe == "VIANDE"){
            product_imageView_details.setImageResource(R.drawable.viande)
        }*/

        val clientActive = intent.getStringExtra("clientActive")
        details_actif.text= clientActive

        val textViewNom = findViewById<TextView>(R.id.details_nom)
        val editText = findViewById<EditText>(R.id.edit_nom)

        val spinner = findViewById<Spinner>(R.id.type_spinner)
        val textViewType = findViewById<TextView>(R.id.details_sexe)

        val button = findViewById<Button>(R.id.modifier)

        val dateP = findViewById<TextView>(R.id.details_actif)
        val dateM = findViewById<TextView>(R.id.DetailDate)

        val textViewUnite = findViewById<TextView>(R.id.details_unite)
        val spinnerUnite = findViewById<Spinner>(R.id.unite_modif_spinner)

        val textViewStock = findViewById<TextView>(R.id.details_stock)
        val modifStock = findViewById<TextView>(R.id.edit_stock)

        textViewNom.setOnLongClickListener {
            textViewNom.setVisibility(View.GONE);
            editText.setVisibility(View.VISIBLE);
            if(button.visibility==View.GONE)
                button.setVisibility(View.VISIBLE);
            true
        }

        textViewType.setOnLongClickListener {
            textViewType.setVisibility(View.GONE);
            spinner.setVisibility(View.VISIBLE);
            if(button.visibility==View.GONE)
                button.setVisibility(View.VISIBLE);
            true
        }

        dateP.setOnLongClickListener {
            dateP.setVisibility(View.GONE);
            dateM.setVisibility(View.VISIBLE);

            //                      DATEPICKER
            dateM.setOnClickListener(View.OnClickListener {
                val cal: Calendar = Calendar.getInstance()
                val year: Int = cal.get(Calendar.YEAR)
                val month: Int = cal.get(Calendar.MONTH)
                val day: Int = cal.get(Calendar.DAY_OF_MONTH)
                val dialog = DatePickerDialog(
                    this@DetailsProductActivity,
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
                    dateM.text = date

                }

            if(button.visibility==View.GONE)
                button.setVisibility(View.VISIBLE);
            true
        }

        textViewUnite.setOnLongClickListener {
            textViewUnite.setVisibility(View.GONE);
            spinnerUnite.setVisibility(View.VISIBLE);
            if(button.visibility==View.GONE)
                button.setVisibility(View.VISIBLE);
            true
        }

        textViewStock.setOnLongClickListener {
            textViewStock.setVisibility(View.GONE);
            modifStock.setVisibility(View.VISIBLE);
            if(button.visibility==View.GONE)
                button.setVisibility(View.VISIBLE);
            true
        }

        edit_stock.setOnClickListener {
            if (spinnerUnite.visibility==View.VISIBLE)
                unite = spinnerUnite.selectedItem as String
            else
                unite = textViewUnite.text.toString()

            val mDialogView = LayoutInflater.from(this).inflate(R.layout.stock_dialog, null)
            // Set a SeekBar change listener
            mDialogView.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                    // Display the current progress of SeekBar
                    mDialogView.text_view_seekbar.text = "$i"
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {
                    // Do something
                    Toast.makeText(applicationContext,"start tracking",Toast.LENGTH_SHORT).show()
                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {
                    // Do something
                    Toast.makeText(applicationContext,"stop tracking",Toast.LENGTH_SHORT).show()
                }
            })

            // Set le input stock
            if (unite=="Portions" || unite=="portions"){
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
            val mBuilder = android.app.AlertDialog.Builder(this)
                .setView(mDialogView)
                .setTitle("Ajout de la quantité")
            //show dialog
            val  mAlertDialog = mBuilder.show()
            //add button click of custom layout
            mDialogView.dialogValiderBtn.setOnClickListener {
                //dismiss dialog
                mAlertDialog.dismiss()
                //get text from EditTexts of custom layout
                if (unite=="portions" || unite=="Portions"){
                    stock = mDialogView.text_view_seekbar.text.toString()
                }
                else {
                    stock = mDialogView.stock_edittext.text.toString()

                }
                //set the input text in TextView
                if(stock!="" || stock!="- -")
                    edit_stock.setText(stock)
                else
                    edit_stock.setText("Entrez la quantité")

            }

            //cancel button click of custom layout
            mDialogView.dialogCancelBtn.setOnClickListener {
                //dismiss dialog
                mAlertDialog.dismiss()
            }
        }


        var name: String = ""
        var typeProduct: Int = 0
        var nvdate: LocalDate = LocalDate.parse(details_actif.text)

        // BOUTON VALIDER MODIF
        button.setOnClickListener {
                if (editText.visibility == View.VISIBLE) {
                    name = editText.text.toString()
                }
                else{
                    name = details_nom.text.toString()
                }

                if (spinner.visibility == View.VISIBLE) {
                    var type = spinner.selectedItem as String
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
                }
                else{
                    var type = details_sexe.text.toString()
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
                }

                if(dateM.visibility == View.VISIBLE){
                    nvdate = LocalDate.parse(dateM.text)
                }



                getServer(name, typeProduct.toString(), nvdate.toString(), stock,NumUnite,"2")

                finish()
        }

    }

    private fun getServer(name : String, type : String, date : String, stock:String, unite:Int, id_client : String){
        val service  = retrofit().create(ProductService::class.java)
        Log.d("concurence", "${name} ${type} ${date} $id "  )
        runBlocking {
            val result = service.updateProduct("${name}","${type}","${date}","${stock}", unite, id)
        }
    }

    /*permet d'ajouter le menu*/
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.detail_product, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.action_delete -> {
                val builder = AlertDialog.Builder(this)
                builder.setTitle(R.string.product_delete_confirm_title)
                builder.setMessage(getString(R.string.product_delete_confirm_message, productLastName))
                builder.setNegativeButton(android.R.string.no){_,_ ->
                    Log.d("EPF", "Non supprimé")
                }
                builder.setPositiveButton(android.R.string.yes){_,_ ->
                    Product.all.removeAt(id)
                    finish()
                }

                builder.show()
                true
            }
            else -> false
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100) {
            val bitmap = data?.getParcelableExtra("data") as? Bitmap //= if data est null on créée bitmap
            Log.d("EPF", "image")
            product_imageView_details.setImageBitmap(bitmap)
        }
    }


}