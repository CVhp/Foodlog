package fr.epf.foodlog.Notif

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import fr.epf.foodlog.R
import fr.epf.foodlog.service.ProductService
import fr.epf.foodlog.service.retrofit
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AlarmReceiver : BroadcastReceiver() {

    private val CHANNEL_ID = "com.singhajit.notificationDemo.channelId"

    //will be executed when the alarm is on
    //ici on récupère les produits qui vont périmer
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context?, intent: Intent?) {
        val service  = retrofit("https://foodlog.min.epf.fr/").create(ProductService::class.java)
        runBlocking {
            val pref = context?.getSharedPreferences(
                "Foodlog",
                Context.MODE_PRIVATE
            )

            val token = pref?.getString("token", null)
            val result = service.getProducts("$token")

            val date = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE)
            val currentDate = LocalDate.parse(date)

            var needNotif = false

            result.products.map {
                val limitDate = LocalDate.parse(it.date)
                //on doit mettre 3 sinon le 2ème jour n'est pas pris en compte (me demander si pas clair)
                if(limitDate.isBefore(currentDate.plusDays(3))){
                    needNotif = true
                    Log.d("token1", "${limitDate}")
                }
            }

            if (needNotif){
                sendNotification(context)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun sendNotification(context: Context?) {
        /*
        //Pour permettre le tap :
        val intent = Intent(context, NotificationActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

        var builder = context?.let {
            NotificationCompat.Builder(it, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications)
                .setContentTitle("Attention")
                .setContentText("Certains de vos produits vont périmer")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
        }

        with(context?.let { NotificationManagerCompat.from(it) }) {
            // notificationId is a unique int for each notification that you must define
            if (builder != null) {
                this?.notify(0, builder.build())
            }
        }*/
        val notificationIntent = Intent(context, NotificationActivity::class.java)

        val stackBuilder =
            TaskStackBuilder.create(context)
        stackBuilder.addParentStack(NotificationActivity::class.java)
        stackBuilder.addNextIntent(notificationIntent)

        val pendingIntent =
            stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)

        val builder = Notification.Builder(context)

        val notification =
            builder.setContentTitle("Attention")
                .setContentText("Certains de vos produits sont périmés")
                .setTicker("New Message Alert!")
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_notifications)
                .setContentIntent(pendingIntent).build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID)
        }

        val notificationManager =
            context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "NotificationDemo",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0, notification)

    }

}