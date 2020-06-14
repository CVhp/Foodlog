package fr.epf.foodlog.ui.Notif

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.TimePicker
import androidx.navigation.Navigation
import androidx.room.Room
import fr.epf.foodlog.LoadingActivities.LoadingActivity

import fr.epf.foodlog.R
import fr.epf.foodlog.data.AppDataBase
import fr.epf.foodlog.ui.data.TimeDao
import fr.epf.foodlog.ui.model.Time
import kotlinx.coroutines.runBlocking

class SettingsFragment : Fragment() {

    private var heure : Int = 0
    var min : Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_settings, container, false)

        val database: AppDataBase = Room.databaseBuilder(requireContext(), AppDataBase::class.java, "gestionclients")
            .build()
        val timeDao : TimeDao = database.getTimeDao()

        val textView = root.findViewById<TextView>(R.id.textView)
        val textView2 = root.findViewById<TextView>(R.id.textView2)
        val timePicker = root.findViewById<TimePicker>(R.id.timePicker)

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

        val button = root.findViewById<Button>(R.id.Button_Valider_settings)
        button.setOnClickListener {
            runBlocking {
                timeDao.deleteTime() //permet de clear la base de donnée interne de time
                timeDao.addTime(Time(0, heure, min))
            }
            val intent = Intent(requireContext(), LoadingActivity::class.java)
            startActivity(intent)
            val pref = requireActivity().getApplicationContext().getSharedPreferences(
                "Foodlog",
                Context.MODE_PRIVATE
            )
            val fridge=pref.getInt("fridge",0);
            val target=SettingsFragmentDirections.returnToListProductFragment(fridge)
            Navigation.findNavController(it).navigate(target);
           // val bundle = Bundle()
           // Navigation.findNavController(it).navigate(R.id.return_to_listProduct_fragment, bundle);
        }

        return root
    }
}
