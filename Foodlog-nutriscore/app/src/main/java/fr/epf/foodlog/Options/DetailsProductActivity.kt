package fr.epf.foodlog.Options

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.GsonBuilder
import fr.epf.foodlog.ListProduct.ProductAdapter
import fr.epf.foodlog.R
import fr.epf.foodlog.service.ProductService
import fr.epf.foodlog.service.retrofit
import kotlinx.android.synthetic.main.activity_add_product.*
import kotlinx.android.synthetic.main.activity_details_product.*
import kotlinx.android.synthetic.main.activity_list_product.*
import kotlinx.android.synthetic.main.stock_dialog.view.*
import kotlinx.coroutines.runBlocking
import okhttp3.*
import java.io.IOException
import java.time.LocalDate
import java.util.*

class DetailsProductActivity() : AppCompatActivity(){

    companion object{
        private val CLICK_ON_IMAGE = 100
        private val IMAGE_PICK_CODE = 101
        private val PERMISSION_CODE = 102
    }

    private var productLastName : String? = null
    private var id : Int = 0
    private var mDateSetListener: DatePickerDialog.OnDateSetListener? = null
    private var nutriscore: String? = null

    @RequiresApi(Build.VERSION_CODES.O)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details_product)

        this.product_imageView_details.setOnClickListener {
            if(checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_DENIED){
                val permissions = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE);
                requestPermissions(permissions, PERMISSION_CODE);
            }
            else {
                pickImageFromGallery()
            }
            /*val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, CLICK_ON_IMAGE)*/
        }

        id = intent.getIntExtra("id", 0)

        nutriscore = intent.getStringExtra("nutriscore")
        detail_nutriscore.text = nutriscore

        productLastName = intent.getStringExtra("clientLastName")
        details_nom.text= productLastName


        val clientSexe = intent.getStringExtra("clientGender")
        details_sexe.text= clientSexe

        var stock = intent.getStringExtra("stock")
        details_stock.text= stock

        var RecUnite = intent.getStringExtra("unite")
        Log.d("unitederec", "${RecUnite}")
        var unite: String
        var NumUnite = 0

        if(RecUnite=="Gramme"){
            unite="grammes"
        }
        else
            unite="portions"

        details_unite.text= unite



        var uri = intent.getStringExtra("uri")
        if (uri == "null"){
            product_imageView_details.setImageResource(
                when(clientSexe){
                    "FRUIT" -> R.drawable.pomme
                    "LEGUME" -> R.drawable.aubergine
                    "CEREALE" -> R.drawable.cereale
                    "LAITIER" -> R.drawable.banane
                    "SALE" -> R.drawable.pomme
                    "SUCRE" -> R.drawable.sucre
                    "VIANDE" -> R.drawable.viande
                    "POISSON" -> R.drawable.poisson
                    "BOISSON" -> R.drawable.boisson
                    else -> R.drawable.banane
                }
            )
        } else {
            product_imageView_details.setImageURI(Uri.parse(uri))
        }


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
            Log.d("Snif", "enter unite")
            textViewUnite.setVisibility(View.GONE)
            spinnerUnite.setVisibility(View.VISIBLE)
            if(button.visibility==View.GONE)
                button.setVisibility(View.VISIBLE)
            true
        }

        textViewStock.setOnLongClickListener {
            textViewStock.setVisibility(View.GONE)
            modifStock.setVisibility(View.VISIBLE)
            if(button.visibility==View.GONE)
                button.setVisibility(View.VISIBLE)
            true
        }

        edit_stock.setOnClickListener {
            Log.d("Snif", "enter edit_stock")
            if (spinnerUnite.visibility==View.VISIBLE) {
                unite = spinnerUnite.selectedItem as String
                Log.d("Snif", "${unite}")
            } else {
                unite = textViewUnite.text.toString()
                Log.d("Snif", "${unite}")
            }

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
            if ( unite=="portions"){
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
                if (unite=="portions"){
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
                @RequiresApi(Build.VERSION_CODES.O)
                if(dateM.visibility == View.VISIBLE){
                    nvdate = LocalDate.parse(dateM.text)
                }

                var uniteVraie : Int

                if (unite_modif_spinner.visibility == View.VISIBLE){
                    if (unite_modif_spinner.selectedItem.toString() == "grammes"){
                        uniteVraie = 1
                    } else {
                        uniteVraie = 2
                    }
                } else {
                    if (details_unite.text.toString() == "grammes"){
                        uniteVraie = 1
                    } else {
                        uniteVraie = 2
                    }
                }

                Log.d("envoi", "${uniteVraie}")

                getServer(name, typeProduct.toString(), nvdate.toString(), stock, uniteVraie)

                finish()
        }

    }

    private fun getServer(name : String, type : String, date : String, stock:String, unite:Int){
        val service  = retrofit("https://foodlog.min.epf.fr/").create(ProductService::class.java)
        val pref = applicationContext.getSharedPreferences(
            "Foodlog",
            Context.MODE_PRIVATE
        )
        val token = pref.getString("token", null);
        Log.d("concurence", "${name} ${type} ${date} $id "  )
        runBlocking {
            val result = service.updateProduct("${token}","${name}","${type}","${date}","${stock}", unite, id)
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
                    val service  = retrofit("https://foodlog.min.epf.fr/").create(ProductService::class.java)
                    val pref = applicationContext.getSharedPreferences(
                        "Foodlog",
                        Context.MODE_PRIVATE
                    )
                    val token = pref.getString("token", null);

                    runBlocking {
                        service.deleteProduct("${token}", id)
                    }

                    //Product.all.removeAt(id)
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
        /*if (requestCode == CLICK_ON_IMAGE) {
            /*val bitmap = data?.getParcelableExtra("data") as? Bitmap //= if data est null on créée bitmap
            Log.d("EPF", "image")
            product_imageView_details.setImageBitmap(bitmap)*/
            pickImageFromGallery()
        }*/
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE){
            val uriPicture = data?.data
            if (uriPicture != null){
                product_imageView_details.setImageURI(uriPicture)
                Log.d("test", "${uriPicture}")
                addUriDataBase(uriPicture)
            }
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    private fun addUriDataBase(uriPicture : Uri?) {
        /*
        //intern database
        val picture = Photo(0, uriPicture.toString())
        val database : AppDataBase = Room.databaseBuilder(this, AppDataBase::class.java, "gestionPictures").build()
        val pictureDao : PhotoDao = database.getPhotoDao()
        runBlocking {
            pictureDao.addPicture(picture)
        }*/

        //server database
        val service = retrofit("https://foodlog.min.epf.fr/").create(ProductService::class.java)
        val pref = applicationContext.getSharedPreferences(
            "Foodlog",
            Context.MODE_PRIVATE
        )
        val token = pref.getString("token", null);
        runBlocking {
            val result = service.postUri("${token}", id,"${uriPicture}")
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            PERMISSION_CODE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    pickImageFromGallery()
                }
                else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


}

