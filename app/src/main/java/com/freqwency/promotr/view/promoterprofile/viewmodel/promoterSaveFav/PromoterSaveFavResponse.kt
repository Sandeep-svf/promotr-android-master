package com.freqwency.promotr.view.promoterprofile.viewmodel.promoterSaveFav

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class PromoterSaveFavResponse: Serializable {
    @SerializedName("errors")
    var errors: Boolean? = false

    @SerializedName("data")
    var data: Data? = null

    inner class Data : Serializable {
        @SerializedName("NewFavoritePromoter")
        var NewFavoritePromoter: NewFavoritePromoter? = null
    }

    inner class NewFavoritePromoter : Serializable {
        @SerializedName("user_id")
        var user_id = 0

        @SerializedName("promoter_id")
        var promoter_id = 0

        @SerializedName("id")
        var id = 0

    }
}