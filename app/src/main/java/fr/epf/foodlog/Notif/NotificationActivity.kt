package fr.epf.foodlog.Notif

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import fr.epf.foodlog.ListProduct.ListProductActivity

class NotificationActivity : AppCompatActivity() {

    //Page qui s'affiche qd on clique sur la notif
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(Intent(this, ListProductActivity::class.java))
    }
}
