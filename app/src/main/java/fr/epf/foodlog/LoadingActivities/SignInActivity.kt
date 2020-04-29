package fr.epf.foodlog.LoadingActivities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import fr.epf.foodlog.Common.Common
import fr.epf.foodlog.ListProduct.ListProductActivity
import fr.epf.foodlog.R
import fr.epf.foodlog.data.AppDataBase
import fr.epf.foodlog.data.ClientDao
import fr.epf.foodlog.model.Client
import fr.epf.foodlog.model.ClientAuth
import fr.epf.foodlogsprint.remote.ClientAPI
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response



class SignInActivity : AppCompatActivity() {


    internal lateinit var mService: ClientAPI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        mService = Common.api
        txt_register.setOnClickListener {
            startActivity(
                Intent(
                    this@SignInActivity,
                    SignUpActivity::class.java
                )
            )
        }
        btn_login.setOnClickListener {
            authenticateUser(
                edt_email.text.toString(),
                edt_password.text.toString()
            )
        }
    }


    private fun authenticateUser(email: String, password: String) {

        mService.loginUser(email, password)
            .enqueue(object : Callback<ClientAuth> {

                override fun onFailure(call: Call<ClientAuth>?, t: Throwable?) {
                    Toast.makeText(this@SignInActivity, t!!.message, Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(call: Call<ClientAuth>, response: Response<ClientAuth>) {
                    if (response.body()!!.error) {
                        Toast.makeText(
                            this@SignInActivity,
                            response.body()!!.error_msg,
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(this@SignInActivity, "Login Success!", Toast.LENGTH_SHORT)
                            .show()

                        val pref = applicationContext.getSharedPreferences(
                            "Foodlog",
                            Context.MODE_PRIVATE
                        )
                        val editor: SharedPreferences.Editor = pref.edit()
                        editor.clear()
                        editor.putString(
                            "token",
                            response.body()!!.user!!.token
                        );           // editor.getString("token", null); pour récupérer la valeur de token ou null s'il n'y a rien
                        editor.apply();
                        // finish()

                        val intent = Intent(this@SignInActivity, ListProductActivity::class.java)
                        startActivity(intent)

                    }
                }


            })


    }
}