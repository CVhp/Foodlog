package fr.epf.foodlog.Notif

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import fr.epf.foodlog.LoadingActivities.LoadingActivity
import fr.epf.foodlog.R
import fr.epf.foodlog.data.AppDataBase
import fr.epf.foodlog.data.TimeDao
import fr.epf.foodlog.model.Time
import kotlinx.coroutines.runBlocking


class SettingsActivity : AppCompatActivity() {

    private var heure : Int = 0
    var min : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val database: AppDataBase = Room.databaseBuilder(this, AppDataBase::class.java, "gestionclients")
            .build()
        val timeDao : TimeDao = database.getTimeDao()

        val textView = findViewById<TextView>(R.id.textView)
        val textView2 = findViewById<TextView>(R.id.textView2)
        val timePicker = findViewById<TimePicker>(R.id.timePicker)

        runBlocking {
            val list = timeDao.getTime() as MutableList<Time>
            if (list.size != 0) {
                heure = list[0].heure
                min = list[0].min
                textView2.text = "Time Set: " + " " + heure + " : " + min + " "
            } else {
                heure = 9
                min = 0
                textView2.text = "Default setting: " + " " + heure + " : " + min + " "
            }
        }
        timePicker.setIs24HourView(true);
        timePicker.setOnTimeChangedListener { _, hourOfDay, minute ->
            if (textView != null) {
                heure = hourOfDay
                min = minute
                val text = "Time: " + " " + hourOfDay + " : " + minute + " "
                textView.text = text
                textView.visibility = View.VISIBLE
            }
        }

        val button = findViewById<Button>(R.id.Button_Valider_settings)
        button.setOnClickListener {
            runBlocking {
                timeDao.deleteTime() //permet de clear la base de donnée interne de time
                timeDao.addTime(Time(0, heure, min))
            }
            startActivity(Intent(this, LoadingActivity::class.java))
        }


    }



}
