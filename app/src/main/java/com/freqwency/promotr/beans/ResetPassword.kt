package com.freqwency.promotr.beans

import com.google.gson.annotations.SerializedName

data class ResetPasswordRequest(
    @SerializedName("email" ) var email: String,
)

data class ResetPasswordResponse(
    @SerializedName("errors" ) var errors : Boolean
)