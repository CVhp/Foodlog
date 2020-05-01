package fr.epf.getimagefromgallery

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.room.Room
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.runBlocking
import retrofit2.create
import java.io.ByteArrayOutputStream
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {

    private var photoDao : PhotoDao? = null

    companion object{
        private val IMAGE_PICK_CODE = 1000;
        private val PERMISSION_CODE = 1001;
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        photoDao = dao()

        img_pick_btn.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if(checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_DENIED){
                    val permissions = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE);
                    requestPermissions(permissions, PERMISSION_CODE);
                }
                else {
                    pickImageFromGallery()
                    //uploadImageToServer()
                    //displayPhotos()
                }
            }
        }
    }

    private fun uploadImageToServer(){

    }

    private fun displayPhotos() {
        runBlocking {
            val photos =  photoDao?.getPictures()
            if (photos != null && photos.size>1) {

                image_view1.setImageURI(Uri.parse(photos.get(photos.size - 2 ).uriPicture))
                image_view2.setImageURI(Uri.parse(photos.get(photos.size - 1 ).uriPicture))

            }
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

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }


     @RequiresApi(Build.VERSION_CODES.P)
     override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE){
            val uriPicture = data?.data
            //image_view.setImageURI(uriPicture)

            Log.d("test2020", "activityresult")


            val source = uriPicture?.let { ImageDecoder.createSource(this.contentResolver, it) }
            val bitmap = source?.let { ImageDecoder.decodeBitmap(it) }

            //val bitmap = data?.getParcelableExtra("data") as? Bitmap //= if data est null on créée bitmap

            image_view.setImageBitmap(bitmap)
            saveImage(bitmap)

            //addUriDataBase(uriPicture)
        }
    }

    private fun saveImage(bitmap: Bitmap?){
        val bytes = ByteArrayOutputStream()
        if (bitmap != null) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
            val byteArrayVar = bytes.toByteArray()
            val convertImage = Base64.encodeToString(byteArrayVar, Base64.DEFAULT)


            val service = retrofit().create(ImageService::class.java)
            runBlocking {
                Log.d("test2020", "envoi")
                val result = service.postImageServer("${convertImage}")
            }
        }

    }

    private fun addUriDataBase(uriPicture : Uri?) {
        //intern database
        val picture = Photo(0, uriPicture.toString())
        val database : AppDataBase = Room.databaseBuilder(this, AppDataBase::class.java, "gestionPictures").build()
        val pictureDao : PhotoDao = database.getPhotoDao()
        runBlocking {
            pictureDao.addPicture(picture)
        }
        //server database
        val service = retrofit().create(ImageService::class.java)
        runBlocking {
            val result = service.postImage("12", "${uriPicture}")
        }
    }
}
