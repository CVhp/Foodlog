package fr.epf.foodlog.barecode

import androidx.multidex.MultiDexApplication
import fr.epf.foodlog.Common.CommonOFF

class KtApplication : MultiDexApplication() {

    internal lateinit var mService: OpenFoodFactsAPI
    internal lateinit var productOFF:ProductOFF
    var id ="737628064502"+".json" // le premier argument est l'id récupéré par le système de codes barres

    override fun onCreate() {
        super.onCreate()
        mService = CommonOFF.api

        // runBlocking {  getproduct(id) }  // On récupère le produit de openfoodfacts et on le poste si on a récupéré qq chose


        }
       }
