package fr.epf.getimagefromgallery

import android.app.Application
import com.facebook.stetho.Stetho

class PhotoApp : Application(){
    override fun onCreate() {
        super.onCreate()
        Stetho.initializeWithDefaults(this)
    }
}