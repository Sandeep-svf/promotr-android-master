package com.freqwency.promotr.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class GetSlidersResponse: Serializable {

    @SerializedName("errors")
    var errors: Boolean? = false
    @SerializedName("data")
    var data: Sliders? = null

    inner class Sliders : Serializable {
        @SerializedName("appSliders")
        var appSliders: ArrayList<SliderBean> = ArrayList()
    }

    inner class SliderBean : Serializable {
        @SerializedName("id")
        var id = 0
        @SerializedName("title")
        var title = ""
        @SerializedName("subtitle")
        var subtitle = ""
        @SerializedName("full_image_url")
        var fullImageUrl = ""
    }
}
