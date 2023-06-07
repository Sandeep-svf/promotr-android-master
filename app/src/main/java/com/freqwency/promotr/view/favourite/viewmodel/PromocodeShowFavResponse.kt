package com.freqwency.promotr.view.favourite.viewmodel

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class PromocodeShowFavResponse : Serializable {
    @SerializedName("errors")
    var errors: Boolean? = false

    @SerializedName("data")
    var data: Data? = null

    inner class Data : Serializable {
        @SerializedName("favoritePromoCodes")
        var favoritePromoCodes: ArrayList<FavoritePromoCodes> = ArrayList()
    }

    inner class FavoritePromoCodes : Serializable {
        @SerializedName("id")
        var id = 0

        @SerializedName("title")
        var title = ""

        @SerializedName("description")
        var description = ""

        @SerializedName("code_id")
        var code_id = ""

        @SerializedName("discount_amount")
        var discount_amount = ""

        @SerializedName("discount_amount_ceiling")
        var discount_amount_ceiling = ""

        @SerializedName("type")
        var type = ""

        @SerializedName("category_id")
        var category_id = 0

        @SerializedName("promoter_id")
        var promoter_id = 0

        @SerializedName("full_image_url")
        var full_image_url = ""
    }
}