package fr.epf.foodlog.LoadingActivities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import fr.epf.foodlog.ListProduct.ListProductActivity
import fr.epf.foodlog.R
import fr.epf.foodlog.service.UserService
import fr.epf.foodlog.service.retrofit
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.coroutines.runBlocking

class SignUpActivity : AppCompatActivity(){



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        register_button.setOnClickListener {
            val name_ = txtName.text.toString()
            val firstname_ = txtFirstname.text.toString()
            val email_registration = txtEmail.text.toString()
            val password_registration = txtPassword.text.toString()
            val password_confirmation = txtPasswordConfirmation.text.toString()

            if (password_confirmation == password_registration){
                getServer(name_, firstname_, email_registration, password_registration)
                val listActivity = Intent (this, ListProductActivity::class.java)
                startActivity(listActivity)
            } else {
                Toast.makeText(this@SignUpActivity,
                    "Le mot de passe de confirmation n'est pas le même que celui rentré.", Toast.LENGTH_SHORT).show()
            }


        }
    }

    private fun getServer(name : String, firstname : String, email : String, password : String){
        val service  = retrofit().create(UserService::class.java)
        Log.d("concurentiel", "synchro ${name} ${firstname} ${email} ${password}")
        runBlocking {
            val result = service.postUser("${name}", "${firstname}", "${email}", "${password}")
        }
    }

}