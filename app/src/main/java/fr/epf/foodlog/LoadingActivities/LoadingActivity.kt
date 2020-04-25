package fr.epf.foodlog.LoadingActivities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import fr.epf.foodlog.Common.Common
import fr.epf.foodlog.ListProduct.ListProductActivity
import fr.epf.foodlog.R
import fr.epf.foodlog.WelcomeScreenActivity
import fr.epf.foodlog.model.ClientAuth
import fr.epf.foodlogsprint.remote.ClientAPI
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoadingActivity : AppCompatActivity() {

    internal lateinit var mService: ClientAPI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)
        mService = Common.api
        Handler().postDelayed({
            // This method will be executed once the timer is over
            // Start your app main activity


            val pref = applicationContext.getSharedPreferences(
                "Foodlog",
                Context.MODE_PRIVATE
            )
            val token = pref.getString("token", null);

            if (token == null)
                startActivity(Intent(this, SignInActivity::class.java))
            else {
                authenticateUser(token)
                }

            // close this activity
            finish()
        }, 2000)
    }

    private fun authenticateUser(token: String) {

        mService.loginUserWithToken(token)
            .enqueue(object : Callback<ClientAuth> {
                override fun onFailure(call: Call<ClientAuth>?, t: Throwable?) {
                    startActivity(Intent(this@LoadingActivity, SignInActivity::class.java))
                }

                override fun onResponse(call: Call<ClientAuth>, response: Response<ClientAuth>) {

                    if (response.body()!!.error)
                        startActivity(Intent(this@LoadingActivity, SignInActivity::class.java))
                    else
                        startActivity(Intent(this@LoadingActivity, ListProductActivity::class.java))

                }


            })


    }

}
