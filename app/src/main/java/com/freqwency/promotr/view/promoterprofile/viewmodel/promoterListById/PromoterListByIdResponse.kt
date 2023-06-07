package com.freqwency.promotr.view.promoterprofile.viewmodel.promoterListById

import com.freqwency.promotr.view.dashboard.viewmodel.promotersList.PromotersListResponse
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class PromoterListByIdResponse: Serializable {
    @SerializedName("errors")
    var errors: Boolean? = false

    @SerializedName("data")
    var data: Data? = null

    inner class Data : Serializable {
        @SerializedName("promoter")
        var promoter:Promoter? = null
    }

    inner class Promoter : Serializable {
        @SerializedName("id")
        var id = 0

        @SerializedName("user_id")
        var user_id = 0

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

        @SerializedName("isFavorite")
        var isFavorite: Boolean? = false

        @SerializedName("user")
        var user: User? = null

        @SerializedName("promocodes")
        var promocodes: ArrayList<Promocodes> = ArrayList()
    }

    inner class Promocodes : Serializable {
        @SerializedName("id")
        var id = 0

        @SerializedName("promoter_id")
        var promoter_id = 0

        @SerializedName("title")
        var title = ""

        @SerializedName("description")
        var description = ""

        @SerializedName("code_id")
        var code_id = ""

        @SerializedName("store_name")
        var store_name = ""

        @SerializedName("store_website_url")
        var store_website_url = ""

        @SerializedName("discount_amount")
        var discount_amount = ""

        @SerializedName("expiry_date")
        var expiry_date = ""

        @SerializedName("type")
        var type = ""

        @SerializedName("full_image_url")
        var full_image_url = ""

    }

    inner class User : Serializable {
        @SerializedName("id")
        var id = 0

        @SerializedName("first_name")
        var first_name = ""

        @SerializedName("last_name")
        var last_name = ""
    }
}