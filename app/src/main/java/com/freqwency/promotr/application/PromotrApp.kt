package com.freqwency.promotr.application

import android.app.Application
import android.content.res.Configuration
import com.freqwency.promotr.utils.PreferenceManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PromotrApp: Application() {

    companion object {
        lateinit var encryptedPrefs: PreferenceManager
        lateinit var instance: PromotrApp
    }

    override fun onCreate() {
        super.onCreate()
        encryptedPrefs = PreferenceManager(applicationContext).getInstance(applicationContext)
        instance = this

    }

    fun isDarkThemeOn(): Boolean {
        return resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    }
}