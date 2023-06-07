package com.freqwency.promotr.view.forgotpassword.viewmodel

import com.google.gson.annotations.SerializedName

data class ForgotBody (
    @SerializedName("email") var email: String? = null
)