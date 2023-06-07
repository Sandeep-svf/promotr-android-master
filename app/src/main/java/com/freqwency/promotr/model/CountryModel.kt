package com.freqwency.promotr.model

import com.google.gson.annotations.SerializedName

data class CountryModel(
    @SerializedName("name") val name: String? = "",
    @SerializedName("nameAR") val nameAR: String? = "",
    @SerializedName("code") val iso: String? = "",
    @SerializedName("dial_code") val phoneCode: String? = ""
)