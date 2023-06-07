package com.freqwency.promotr.view.becomePromoter.becomePromoterProfile

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class PromoterProfileResponse: Serializable {
    @SerializedName("errors")
    var errors: Boolean? = false

    @SerializedName("data")
    var data: Data? = null

    inner class Data : Serializable {
        @SerializedName("promoterInfo")
        var promoterInfo: PromoterInfo? = null
    }

    inner class PromoterInfo : Serializable {
        @SerializedName("id")
        var id = 0

        @SerializedName("about")
        var about = ""

        @SerializedName("instagram_profile_url")
        var instagram_profile_url = ""

        @SerializedName("status")
        var status = ""

        @SerializedName("image_url")
        var image_url = ""

        @SerializedName("full_image_url")
        var full_image_url = ""

        @SerializedName("facebook_profile_url")
        var facebook_profile_url = ""

        @SerializedName("user_id")
        var user_id = 0

    }
}