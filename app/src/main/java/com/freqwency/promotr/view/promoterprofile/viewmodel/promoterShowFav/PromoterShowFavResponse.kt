package com.freqwency.promotr.view.promoterprofile.viewmodel.promoterShowFav

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class PromoterShowFavResponse : Serializable {
    @SerializedName("errors")
    var errors: Boolean? = false

    @SerializedName("data")
    var data: Data? = null

    inner class Data : Serializable {
        @SerializedName("favoritePromoters")
        var favoritePromoters: ArrayList<FavoritePromoters> = ArrayList()
    }

    inner class FavoritePromoters : Serializable {
        @SerializedName("id")
        var id = 0

        @SerializedName("user_id")
        var user_id = 0

        @SerializedName("nickname")
        var nickname = ""

        @SerializedName("about")
        var about = ""

        @SerializedName("instagram_profile_url")
        var instagram_profile_url = ""

        @SerializedName("facebook_profile_url")
        var facebook_profile_url = ""

        @SerializedName("status")
        var status = ""

        @SerializedName("full_image_url")
        var full_image_url = ""

        @SerializedName("created_at")
        var created_at = ""

        @SerializedName("pivot")
        var pivot: Pivot? = null
    }

    inner class Pivot : Serializable {
        @SerializedName("user_id")
        var user_id = 0

        @SerializedName("promoter_id")
        var promoter_id = 0
    }
}