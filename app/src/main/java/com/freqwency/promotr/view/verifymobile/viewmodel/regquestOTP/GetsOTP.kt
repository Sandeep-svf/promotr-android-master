package com.freqwency.promotr.view.verifymobile.viewmodel.regquestOTP

import com.google.gson.annotations.SerializedName

data class RequestOTPRequest(
    @SerializedName("mobile_number" )var mobileNumber: String
)

data class GetsOTPResponse(
    @SerializedName("errors" ) var errors : Boolean,
    @SerializedName("data" ) var data : String
)


