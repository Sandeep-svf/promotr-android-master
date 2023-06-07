package com.freqwency.promotr.beans

import com.google.gson.annotations.SerializedName

data class VerifyOTPRequest(
    @SerializedName("otp_code") var verifyOTP: Int,
    @SerializedName("mobile_number") var mobileNumber: String
)

