package com.freqwency.promotr.view.logins.viewmodel.logout

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class LogoutResponse : Serializable {
    @SerializedName("errors")
    var errors: Boolean? = false
    @SerializedName("data")
    var data: ArrayList<Data> = ArrayList()

    inner class Data : Serializable {
    }
}