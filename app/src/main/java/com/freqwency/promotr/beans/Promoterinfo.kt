package com.freqwency.promotr.beans

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Promoterinfo (
    @SerializedName("id"    ) var PromoterId    : Int? = null,
    @SerializedName("about"     ) var about     : String? = null,
    @SerializedName("instagram_profile_url"     ) var instagram_profile_url     : String? = null,
    @SerializedName("full_image_url"         ) var full_image_url        : String? = null
): Serializable