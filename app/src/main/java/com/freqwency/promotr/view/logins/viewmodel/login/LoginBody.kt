package com.freqwency.promotr.view.logins.viewmodel.login

import com.google.gson.annotations.SerializedName

data class LoginBody (
    @SerializedName("email") var email      : String? = null,
    @SerializedName("password") var password   : String? = null,
    @SerializedName("device_name") var deviceName : String? = null
)