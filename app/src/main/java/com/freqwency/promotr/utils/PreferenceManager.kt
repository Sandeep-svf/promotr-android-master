package com.freqwency.promotr.utils

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.freqwency.promotr.beans.User
import com.google.gson.Gson


class PreferenceManager(context: Context) {

    private var mPrefs: PreferenceManager? = null

    private var masterKey = MasterKey.Builder(context, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private var prefs = EncryptedSharedPreferences.create(
        context,
        "PromotrEncryptedPreferences",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    private val editor = prefs.edit()

    fun getInstance(context: Context): PreferenceManager {
        if (mPrefs == null) {
            synchronized(PreferenceManager::class.java) {
                if (mPrefs == null)
                    mPrefs = PreferenceManager(context)
            }
        }
        return mPrefs!!
    }

    // region "Getters & Setters"
    var isFirstTime: Boolean
        get() = prefs.getBoolean(IS_FIRST_TIME, true)
        set(isFirstTime) {
            editor.putBoolean(IS_FIRST_TIME, isFirstTime)
            editor.apply()
        }

    var appLanguage: String
        get() = prefs.getString(USER_LANG, "") ?: ""
        set(appLanguage) {
            editor.putString(USER_LANG, appLanguage)
            editor.apply()
        }

    var deviceID: String
        get() = prefs.getString(DEVICE_ID, "") ?: ""
        set(deviceID) {
            editor.putString(DEVICE_ID, deviceID)
            editor.apply()
        }

    var token: String
        get() = prefs.getString(USER_TOKEN, "") ?: ""
        set(userToken) {
            editor.putString(USER_TOKEN, userToken)
            editor.apply()
        }

    var bearerToken: String
        get() = prefs.getString(BEARER_USER_TOKEN, "") ?: ""
        set(userToken) {
            editor.putString(BEARER_USER_TOKEN, userToken)
            editor.apply()
        }

    var promoterId: String
        get() = prefs.getString(PROMOTER_ID, "") ?: ""
        set(userToken) {
            editor.putString(PROMOTER_ID, userToken)
            editor.apply()
        }

    var userId: String
        get() = prefs.getString(USER_ID, "") ?: ""
        set(userId) {
            editor.putString(USER_ID, userId)
            editor.apply()
        }

    var storeName: String
        get() = prefs.getString(STORE_NAME, "") ?: ""
        set(userToken) {
            editor.putString(STORE_NAME, userToken)
            editor.apply()
        }

    var dateOfBirth: String
        get() = prefs.getString(DATE_OF_BIRTH, "") ?: ""
        set(userToken) {
            editor.putString(DATE_OF_BIRTH, userToken)
            editor.apply()
        }

    var discount: String
        get() = prefs.getString(DISCOUNT, "") ?: ""
        set(userToken) {
            editor.putString(DISCOUNT, userToken)
            editor.apply()
        }

    var category: Int
        get() = prefs.getInt(CATEGORY, 0)
        set(userToken) {
            editor.putInt(CATEGORY, userToken)
            editor.apply()
        }

    var FCMToken: String
        get() = prefs.getString(FCM_TOKEN, "") ?: ""
        set(userToken) {
            editor.putString(FCM_TOKEN, userToken)
            editor.apply()
        }

    var isNotification: Boolean
        get() = prefs.getBoolean(IS_NOTIFICATION, true)
        set(isNotification) {
            editor.putBoolean(IS_NOTIFICATION, isNotification)
            editor.apply()
        }


    var user: User?
        get() {
            val gson = Gson()
            val userString = prefs.getString(
                USER_PREFS, ""
            ).toString()
            return gson.fromJson(userString, User::class.java)
        }
        set(user) {
            val gson = Gson()
            val userString = gson.toJson(user)
            editor.putString(USER_PREFS, userString)
            editor.apply()
        }


    fun clearPrefs() {
        val editor = prefs.edit()
        editor.remove(IS_FIRST_TIME)
        editor.clear()
        editor.apply()
    }


    companion object {
        // region "Tags"
        private const val IS_FIRST_TIME = "isFirstTime"

        private const val USER_TOKEN = "USER_TOKEN"

        private const val BEARER_USER_TOKEN = "BEARER_USER_TOKEN"

        private const val PROMOTER_ID = "PROMOTER_ID"

        private const val USER_ID = "USER_ID"

        private const val USER_PREFS = "USER_PREFS"

        private const val USER_LANG = "USER_LANG"

        private const val DEVICE_ID = "DEVICE_ID"

        private const val STORE_NAME = "STORE_NAME"

        private const val DATE_OF_BIRTH = "DATE_OF_BIRTH"

        private const val DISCOUNT = "DISCOUNT"

        private const val CATEGORY = "CATEGORY"

        private const val FCM_TOKEN = "FCM_TOKEN"

        private const val IS_NOTIFICATION = "IS_NOTIFICATION"
    }

}