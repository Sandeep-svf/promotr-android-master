package com.freqwency.promotr.view.dashboard.viewmodel.category

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class CategoryResponse : Serializable {

    @SerializedName("errors")
    var errors: Boolean? = false
    @SerializedName("data")
    var data: Data? = null

    inner class Data : Serializable {
        @SerializedName("categories")
        var categorys: ArrayList<Categorys> = ArrayList()
    }

    inner class Categorys : Serializable {
        @SerializedName("id")
        var id = 0
        @SerializedName("name")
        var name = ""
        @SerializedName("description")
        var description = ""
        @SerializedName("image_url")
        var image_url = ""
        @SerializedName("full_image_url")
        var full_image_url = ""
    }
}
