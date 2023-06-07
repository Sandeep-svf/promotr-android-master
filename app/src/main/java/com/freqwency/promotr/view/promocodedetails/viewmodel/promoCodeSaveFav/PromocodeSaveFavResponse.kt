package com.freqwency.promotr.view.promocodedetails.viewmodel.promoCodeSaveFav

import com.freqwency.promotr.view.promocodedetails.viewmodel.promoCodeDetails.PromocodeDetailsResponse
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class PromocodeSaveFavResponse : Serializable {
    @SerializedName("errors")
    var errors: Boolean? = false

    @SerializedName("data")
    var data: PromocodeDetailsResponse.Data? = null

    inner class Data : Serializable {
        @SerializedName("newFavoritePromoCode")
        var newFavoritePromoCode: NewFavoritePromoCode? = null
    }

    inner class NewFavoritePromoCode : Serializable {
        @SerializedName("user_id")
        var user_id = 0

        @SerializedName("promo_code_id")
        var promo_code_id = 0
    }
}