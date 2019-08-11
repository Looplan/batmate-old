package nl.looplan.batmate

import android.app.Application
import com.facebook.drawee.backends.pipeline.Fresco
import com.google.firebase.analytics.FirebaseAnalytics

class BatMateApplication : Application() {

    companion object {
        lateinit var INSTANCE : BatMateApplication
    }

    override fun onCreate() {
        super.onCreate()

        INSTANCE = this

        // Initialize the Preferences object.
        BatMatePreferences.initialize(this)

        // Initialize Fresco.
        Fresco.initialize(this)

        // Initialize firebase analytics
        FirebaseAnalytics.getInstance(this)
    }
}