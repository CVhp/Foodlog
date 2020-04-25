package fr.epf.foodlog.LoadingActivities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import fr.epf.foodlog.ListProduct.ListProductActivity
import fr.epf.foodlog.R
import fr.epf.foodlog.data.AppDataBase
import fr.epf.foodlog.data.ClientDao
import fr.epf.foodlog.model.Client
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.coroutines.runBlocking

class SignInActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        connexion_button.setOnClickListener {
            val email = cnxEmail.text.toString()
            val password = cnxPassword.text.toString()

            if (email == "email@gmail.com" && password == "123") {
                Toast.makeText(this@SignInActivity,
                    "Bienvenue", Toast.LENGTH_SHORT).show()
                Log.d("EPF", "Vous êtes authentifié")

                //------------------------------------------------Met le client qui s'est authentifié dans la bdd interne

                //Faire une requete de la BDD serveur afin de récup les infos du client qui vient de se logger

                val client = Client(0, "Vhp", "Cecile",email,password)

                val database: AppDataBase = Room.databaseBuilder(this, AppDataBase::class.java, "gestionclients")
                    .build()
                val clientDao : ClientDao = database.getClientDao()
                runBlocking {
                    clientDao.deleteClients() //permet de clear l'ancien user
                    clientDao.addClient(client) //référence aux coroutines Kotlin
                }
                //------------------------------------------------------------------------------------------------------------

                val listActivity = Intent (this, ListProductActivity::class.java)
                startActivity(listActivity)

            } else {
                Toast.makeText(this@SignInActivity,
                    "Mot de passe ou email incorrect", Toast.LENGTH_SHORT).show()
                Log.d("EPF", "Mot de passe ou email incorrect")

                val listActivity = Intent (this, SignInActivity::class.java)
                startActivity(listActivity)
            }

            finish()
        }
    }
}