package fr.epf.foodlog.LoadingActivities

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import fr.epf.foodlog.MainActivity
import fr.epf.foodlog.Notif.AlarmReceiver
import fr.epf.foodlog.R
import fr.epf.foodlog.data.AppDataBase
import fr.epf.foodlog.data.ClientDao
import fr.epf.foodlog.model.Client
import fr.epf.foodlog.model.ClientAuth
import fr.epf.foodlog.ui.Common.Common
import fr.epf.foodlog.ui.data.TimeDao
import fr.epf.foodlog.ui.model.Time
import fr.epf.foodlogsprint.remote.ClientAPI
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response



class SignInActivity : AppCompatActivity() {

    internal lateinit var mService: ClientAPI

    private var alarmMgr: AlarmManager? = null
    private lateinit var alarmIntent: PendingIntent

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

                @RequiresApi(Build.VERSION_CODES.N)
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

                        beginServiceNotification()
                        val intent = Intent(this@SignInActivity, MainActivity::class.java)
                        startActivity(intent)
                    }
                }
            }
            )
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun beginServiceNotification(){
        alarmMgr = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmIntent = Intent(this, AlarmReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(this, 0, intent, 0)
        }

        val database: AppDataBase = Room.databaseBuilder(this, AppDataBase::class.java, "gestionclients")
            .build()
        val timeDao : TimeDao = database.getTimeDao()

        var heure: Int
        var min: Int
        runBlocking {
            val list = timeDao.getTime() as MutableList<Time>
            if (list.size != 0){
                heure = list[0].heure
                min = list[0].min
            }
            else{
                heure = 9
                min = 0
            }


            // Set the alarm to start
            val calendar: Calendar = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR_OF_DAY, heure)
                set(Calendar.MINUTE, min)
            }

            alarmMgr?.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_FIFTEEN_MINUTES,
                alarmIntent
            )

        }

        Toast.makeText(this, "Alarm is set", Toast.LENGTH_SHORT).show()
    }
}