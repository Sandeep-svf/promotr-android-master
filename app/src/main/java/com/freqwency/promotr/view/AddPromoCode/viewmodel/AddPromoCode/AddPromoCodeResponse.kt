package com.freqwency.promotr.view.AddPromoCode.viewmodel.AddPromoCode

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class AddPromoCodeResponse : Serializable {

    @SerializedName("errors")
    var errors: Boolean? = false

    @SerializedName("data")
    var data: Data? = null

    inner class Data : Serializable {
        @SerializedName("promocode")
        var promocode: PromoCode? = null
    }

    inner class PromoCode : Serializable {
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

        @SerializedName("type")
        var type = ""

        @SerializedName("category_id")
        var category_id = ""

        @SerializedName("full_image_url")
        var full_image_url = ""

        @SerializedName("id")
        var id = 0

        @SerializedName("promoter_id")
        var promoter_id = 0
    }

}