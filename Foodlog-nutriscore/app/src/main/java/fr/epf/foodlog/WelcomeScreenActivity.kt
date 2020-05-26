package fr.epf.foodlog

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import fr.epf.foodlog.LoadingActivities.SignInActivity
import fr.epf.foodlog.LoadingActivities.SignUpActivity
import kotlinx.android.synthetic.main.activity_welcome_screen.*

class WelcomeScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome_screen)

        btnSignIn.setOnClickListener{
            val signIn = Intent (this, SignInActivity::class.java)
            startActivity(signIn)
        }

        btnSignUp.setOnClickListener{
            val signUp = Intent (this, SignUpActivity::class.java)
            startActivity(signUp)
        }
    }
}
