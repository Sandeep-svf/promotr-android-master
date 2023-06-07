package com.freqwency.promotr.view.verifymobile.viewmodel.verifyOTP

import com.freqwency.promotr.beans.User
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class VerifyOTPResponse(
    @SerializedName("errors") var errors: Boolean? = null,
    @SerializedName("data") var data: Data? = Data()
) : Serializable {

    data class Data(
        @SerializedName("status") var status: String? = null,
    ) : Serializable
}