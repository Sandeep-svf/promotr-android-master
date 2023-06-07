package com.freqwency.promotr.view.dashboard.viewmodel.promocode

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class PromocodeResponse : Serializable {

    @SerializedName("errors")
    var errors: Boolean? = false

    @SerializedName("data")
    var data: Data? = null

    inner class Data : Serializable {
        @SerializedName("promocodes")
        var promoCodes: ArrayList<PromoCodes> = ArrayList()
    }

    inner class PromoCodes : Serializable {
        @SerializedName("id")
        var id = 0

        @SerializedName("category_id")
        var category_id = 0

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

        @SerializedName("type")
        var type = ""

        @SerializedName("full_image_url")
        var full_image_url = ""

        @SerializedName("expiry_date")
        var expiry_date = ""
    }
}