package com.freqwency.promotr.view.forgotpassword.viewmodel

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ForgotResponse(
    @SerializedName("errors") var errors: Boolean? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("data") var data: Data? = Data()
) : Serializable {

    data class Data(
        @SerializedName("status") var status: String? = null
    ) : Serializable
}