package com.freqwency.promotr.view.userprofile.viewmodel

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class UserProfileResponse {
    @SerializedName("errors")
    var errors: Boolean? = false

    @SerializedName("data")
    var data: Data? = null

    inner class Data : Serializable {
        @SerializedName("user")
        var user: User? = null
    }

    inner class User : Serializable {
        @SerializedName("id")
        var id = 0

        @SerializedName("first_name")
        var first_name = ""

        @SerializedName("last_name")
        var last_name = ""

        @SerializedName("email")
        var email = ""

        @SerializedName("country")
        var country = ""

        @SerializedName("gender")
        var gender = ""

        @SerializedName("mobile_number")
        var mobile_number = ""

        @SerializedName("date_of_birth")
        var date_of_birth = ""

        @SerializedName("mobile_number_verified")
        var mobile_number_verified: Boolean? = false

        @SerializedName("promoter_info")
        var promoter_info: Promoter_info? = null
    }

    inner class Promoter_info : Serializable {
        @SerializedName("id")
        var PromoterId = 0

        @SerializedName("about")
        var about = ""

        @SerializedName("instagram_profile_url")
        var instagram_profile_url = ""

        @SerializedName("nickname")
        var nickname = ""

        @SerializedName("facebook_profile_url")
        var facebook_profile_url = ""

        @SerializedName("full_image_url")
        var full_image_url = ""
    }
}