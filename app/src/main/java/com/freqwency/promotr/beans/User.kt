package com.freqwency.promotr.beans

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.Objects

data class User (
    @SerializedName("first_name"    ) var firstName    : String? = null,
    @SerializedName("last_name"     ) var lastName     : String? = null,
    @SerializedName("email"         ) var email        : String? = null,
    @SerializedName("gender"        ) var gender       : String? = null,
    @SerializedName("country"       ) var country      : String? = null,
    @SerializedName("device_name"   ) var deviceName   : String? = null,
    @SerializedName("locale"        ) var locale       : String? = null,
    @SerializedName("updated_at"    ) var updatedAt    : String? = null,
    @SerializedName("created_at"    ) var createdAt    : String? = null,
    @SerializedName("id"            ) var id           : Int?    = null,
    @SerializedName("date_of_birth" ) var dateOfBirth  : String? = null,
    @SerializedName("mobile_number" ) var mobileNumber : String? = null,
    @SerializedName("mobile_number_verified" ) var mobileNumberVerified : Boolean,
    @SerializedName("fcm_token") var fcm_token: String? = null,
    @SerializedName("promoter_info") var promoter_info: Promoterinfo? = null
): Serializable