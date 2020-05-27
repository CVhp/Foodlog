package fr.epf.foodlog.LoadingActivities

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.room.Room
import fr.epf.foodlog.Notif.AlarmReceiver
import fr.epf.foodlog.Common.Common
import fr.epf.foodlog.ListProduct.ListProductActivity
import fr.epf.foodlog.R
import fr.epf.foodlog.data.AppDataBase
import fr.epf.foodlog.data.TimeDao
import fr.epf.foodlog.model.ClientAuth
import fr.epf.foodlog.model.Time
import fr.epf.foodlogsprint.remote.ClientAPI
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoadingActivity : AppCompatActivity() {

    internal lateinit var mService: ClientAPI

    private var alarmMgr: AlarmManager? = null
    private lateinit var alarmIntent: PendingIntent


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

                @RequiresApi(Build.VERSION_CODES.N)
                override fun onResponse(call: Call<ClientAuth>, response: Response<ClientAuth>) {

                    if (response.body()!!.error) {
                        startActivity(Intent(this@LoadingActivity, SignInActivity::class.java))
                    }
                    else {
                        beginServiceNotification()
                        startActivity(Intent(this@LoadingActivity, ListProductActivity::class.java))
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
            if (list.size != 0) {
                heure = list[0].heure
                min = list[0].min
            } else {
                heure = 9
                min = 0
            }

            // Set the alarm to start at approximately 2:00 p.m.
            val calendar: Calendar = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR_OF_DAY, heure)
                set(Calendar.MINUTE, min)
            }

            // With setInexactRepeating(), you have to use one of the AlarmManager interval
            // constants--in this case, AlarmManager.INTERVAL_DAY.
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
