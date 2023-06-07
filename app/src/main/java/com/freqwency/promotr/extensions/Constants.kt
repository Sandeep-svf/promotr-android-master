package com.freqwency.promotr.utils

import android.content.Intent
import android.os.Build
import android.os.Bundle
import com.google.gson.Gson
import java.io.Serializable

// To get Gson Instance
var gson: Gson? = null

fun getGsonInstance(): Gson {

    if (gson == null)
        gson = Gson()
    return gson!!
}

inline fun <reified T : Serializable> Bundle.serializable(key: String): T? = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getSerializable(key, T::class.java)
    else -> @Suppress("DEPRECATION") getSerializable(key) as? T
}

inline fun <reified T : Serializable> Intent.serializable(key: String): T? = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getSerializableExtra(key, T::class.java)
    else -> @Suppress("DEPRECATION") getSerializableExtra(key) as? T
}



