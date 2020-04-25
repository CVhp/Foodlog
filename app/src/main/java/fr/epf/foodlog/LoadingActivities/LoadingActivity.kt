package fr.epf.foodlog.LoadingActivities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import fr.epf.foodlog.R
import fr.epf.foodlog.WelcomeScreenActivity

class LoadingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)
        Handler().postDelayed({
            // This method will be executed once the timer is over
            // Start your app main activity

            startActivity(Intent(this, WelcomeScreenActivity::class.java))

            // close this activity
            finish()
        }, 2000)
    }
}
