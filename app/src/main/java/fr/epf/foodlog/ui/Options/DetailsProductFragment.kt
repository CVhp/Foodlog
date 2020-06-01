package fr.epf.foodlog.ui.Options

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation

import fr.epf.foodlog.R
import fr.epf.foodlog.service.ProductService
import fr.epf.foodlog.service.retrofit
import kotlinx.android.synthetic.main.fragment_details_product.*
import kotlinx.android.synthetic.main.fragment_details_product.view.*
import kotlinx.android.synthetic.main.stock_dialog.view.*
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.util.*

class DetailsProductFragment : Fragment() {

    companion object{
        private val CLICK_ON_IMAGE = 100
        private val IMAGE_PICK_CODE = 101
        private val PERMISSION_CODE = 102
    }

    private var productLastName : String? = null
    private var id2 : Int = 0
    private var mDateSetListener: DatePickerDialog.OnDateSetListener? = null
    private var nutriscore: String? = null
    lateinit var appContext: Context
    lateinit var root : View

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_details_product, container, false)

        root.product_imageView_details.setOnClickListener {
            if(ContextCompat.checkSelfPermission(requireContext(),android.Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_DENIED){
                val permissions = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE);
                requestPermissions(permissions, PERMISSION_CODE);
            }
            else {
                pickImageFromGallery()
            }
        }

        id2 = requireArguments().getInt("id")

        nutriscore = requireArguments().getString("nutriscore")
        root.detail_nutriscore.text = nutriscore

        Log.d("blabla", "${nutriscore}")

        if(nutriscore == null){
            root.detail_nutriscore.setVisibility(View.GONE);
            root.qualite.setVisibility(View.GONE);
        } else {
            root.detail_nutriscore.setVisibility(View.VISIBLE);
            root.qualite.setVisibility(View.VISIBLE);
        }

        productLastName = requireArguments().getString("clientLastName") as String
        root.details_nom.text= productLastName

        val clientSexe = requireArguments().getString("clientGender") as String
        root.details_sexe.text= clientSexe

        var stock = requireArguments().getString("stock") as String
        root.details_stock.text= stock

        var RecUnite = requireArguments().getString("unite") as String
        Log.d("unitederec", "${RecUnite}")
        var unite: String
        var NumUnite = 0

        if(RecUnite=="Gramme"){
            unite="grammes"
        }
        else
            unite="portions"

        root.details_unite.text= unite


        var uri = requireArguments().getString("uri") as String
        if (uri == "null"){
            root.product_imageView_details.setImageResource(
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
            root.product_imageView_details.setImageURI(Uri.parse(uri))
        }


        /*if (clientSexe == "VIANDE"){
            product_imageView_details.setImageResource(R.drawable.viande)
        }*/

        val clientActive = requireArguments().getString("clientActive") as String
        root.details_actif.text= clientActive

        val textViewNom = root.findViewById<TextView>(R.id.details_nom)
        val editText = root.findViewById<EditText>(R.id.edit_nom)

        val spinner = root.findViewById<Spinner>(R.id.type_spinner)
        val textViewType = root.findViewById<TextView>(R.id.details_sexe)

        val button = root.findViewById<Button>(R.id.modifier)

        val dateP = root.findViewById<TextView>(R.id.details_actif)
        val dateM = root.findViewById<TextView>(R.id.DetailDate)

        val textViewUnite = root.findViewById<TextView>(R.id.details_unite)
        val spinnerUnite = root.findViewById<Spinner>(R.id.unite_modif_spinner)

        val textViewStock = root.findViewById<TextView>(R.id.details_stock)
        val modifStock = root.findViewById<TextView>(R.id.edit_stock)

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
                    requireContext(),
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

        root.edit_stock.setOnClickListener {
            if (spinnerUnite.visibility==View.VISIBLE) {
                unite = spinnerUnite.selectedItem as String
            } else {
                unite = textViewUnite.text.toString()
            }

            val mDialogView = LayoutInflater.from(requireContext()).inflate(R.layout.stock_dialog, null)
            // Set a SeekBar change listener
            mDialogView.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                    // Display the current progress of SeekBar
                    mDialogView.text_view_seekbar.text = "$i"
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {
                    // Do something
                    appContext = requireActivity().getApplicationContext()
                    Toast.makeText(appContext,"start tracking",Toast.LENGTH_SHORT).show()
                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {
                    // Do something
                    appContext = requireActivity().getApplicationContext()
                    Toast.makeText(appContext,"stop tracking",Toast.LENGTH_SHORT).show()
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
            val mBuilder = android.app.AlertDialog.Builder(requireContext())
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
        var nvdate: LocalDate = LocalDate.parse(root.details_actif.text)


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

            val pref = appContext.getSharedPreferences(
                "Foodlog",
                Context.MODE_PRIVATE
            )
            val fridge = pref.getInt("fridge", 0);
            val target=DetailsProductFragmentDirections.returnToListProductFragment(fridge)
            Navigation.findNavController(requireView()).navigate(target);
        }

        return root
    }

    private fun getServer(name : String, type : String, date : String, stock:String, unite:Int){
        val service  = retrofit("https://foodlog.min.epf.fr/").create(ProductService::class.java)
        appContext = requireActivity().getApplicationContext()
        val pref = appContext.getSharedPreferences(
            "Foodlog",
            Context.MODE_PRIVATE
        )
        val token = pref.getString("token", null);
        val fridge = pref.getInt("fridge", 0);
        Log.d("concurence", "${name}  ${type}  ${date}  $id2 "  )
        runBlocking {
            val result = service.updateProduct("${token}",fridge,"${name}","${type}","${date}","${stock}", unite, id2)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.detail_product, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.action_delete -> {
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle(R.string.product_delete_confirm_title)
                builder.setMessage(getString(R.string.product_delete_confirm_message, productLastName))
                builder.setNegativeButton(android.R.string.no){_,_ ->
                    Log.d("EPF", "Non supprimé")
                }
                builder.setPositiveButton(android.R.string.yes){_,_ ->
                    val service  = retrofit("https://foodlog.min.epf.fr/").create(ProductService::class.java)
                    appContext = requireActivity().getApplicationContext()
                    val pref = appContext.getSharedPreferences(
                        "Foodlog",
                        Context.MODE_PRIVATE
                    )
                    val token = pref.getString("token", null);
                    val fridge=pref.getInt("fridge",0);

                    runBlocking {
                        service.deleteProduct("${token}",fridge, id2)
                    }

                    //Product.all.removeAt(id2)
                   // val bundle = Bundle()
                    val target=DetailsProductFragmentDirections.returnToListProductFragment(fridge)
                    Navigation.findNavController(requireView()).navigate(target);
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
        appContext = requireActivity().getApplicationContext()
        val pref = appContext.getSharedPreferences(
            "Foodlog",
            Context.MODE_PRIVATE
        )
        val token = pref.getString("token", null);
        runBlocking {
            val result = service.postUri("${token}", id2,"${uriPicture}")
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
                    Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
