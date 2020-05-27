package fr.epf.foodlog.LoadingActivities

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import fr.epf.foodlog.Common.Common
import fr.epf.foodlog.ListProduct.ListProductActivity
import fr.epf.foodlog.R
import fr.epf.foodlog.model.ClientAuth
import fr.epf.foodlog.service.UserService
import fr.epf.foodlog.service.retrofit
import fr.epf.foodlogsprint.remote.ClientAPI
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpActivity : AppCompatActivity() {

    internal lateinit var mService: ClientAPI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        Toast.makeText(this@SignUpActivity,"Le mot de passe doit comporter au moins une majuscule, un chiffre et 6 caractères",Toast.LENGTH_LONG).show()

        mService = Common.api
        txt_login.setOnClickListener { finish() }

        btn_register.setOnClickListener {
            val passwd=edt_password.text.toString()
            val digit=passwd.filter {it.isDigit()}.length
            val maj=passwd.filter {it.isUpperCase()}.length
            val lenght=passwd.length

            if ((digit>0)&&(maj>0)&&(lenght>5)) {

                createNewUser(
                    edt_name.text.toString(),
                    edt_email.text.toString(),
                    edt_password.text.toString()
                )
            }else if (digit==0){
                Toast.makeText(this@SignUpActivity,"Le mot de passe doit comporter au moins un chiffre",Toast.LENGTH_LONG).show()
            }else if (maj==0){
                Toast.makeText(this@SignUpActivity,"Le mot de passe doit comporter au moins une majuscule",Toast.LENGTH_LONG).show()
            }else if (lenght<=5){
                Toast.makeText(this@SignUpActivity,"Le mot de passe doit comporter au moins 6 caractères",Toast.LENGTH_LONG).show()
            }
        }
    }


    private fun createNewUser(name: String, email: String, password: String) {
        mService.registerUser(name, email, password).enqueue(object : Callback<ClientAuth> {
            override fun onFailure(call: Call<ClientAuth>, t: Throwable) {
                Toast.makeText(this@SignUpActivity, t.message, Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<ClientAuth>, response: Response<ClientAuth>) {
                if (response.body()!!.error) {
                    Toast.makeText(
                        this@SignUpActivity,
                        response.body()!!.error_msg,
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(this@SignUpActivity, "Register Success!"+response.body()!!.user.toString(), Toast.LENGTH_SHORT)
                        .show()

                    val pref = applicationContext.getSharedPreferences(
                        "Foodlog",
                        Context.MODE_PRIVATE
                    )
                    val editor: SharedPreferences.Editor = pref.edit()
                    editor.clear()
                    editor.putString("token", response.body()!!.user!!.token);           // editor.getString("token", null); pour récupérer la valeur de token ou null s'il n'y a rien
                    editor.apply();
                    finish()
                }
            }
        })
    }
}