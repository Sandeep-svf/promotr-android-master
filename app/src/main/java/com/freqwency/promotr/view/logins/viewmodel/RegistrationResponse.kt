package com.freqwency.promotr.view.logins.viewmodel

import com.freqwency.promotr.beans.User
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class RegistrationResponse(
    @SerializedName("errors") var errors: Boolean? = null,
    @SerializedName("data") var data: Data? = Data()
) : Serializable {

    data class Data(
        @SerializedName("access_token") var accessToken: String? = null,
        @SerializedName("user") var user: User? = null
    ) : Serializable
}
