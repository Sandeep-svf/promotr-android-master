package com.freqwency.promotr.view.register.viewmodel

import com.google.gson.annotations.SerializedName

data class RegistrationBody(
    @SerializedName("first_name") var firstName: String,
    @SerializedName("last_name") var lastName: String,
    @SerializedName("mobile_number") var mobileNumber: String,
    @SerializedName("email") val email: String = "",
    @SerializedName("country") var country: String,
    @SerializedName("password") var password: String,
    @SerializedName("password_confirmation") var passwordConfirmation: String,
    @SerializedName("gender") var gender: String = "",
    @SerializedName("has_accepted_terms") val hasAcceptedTerms: Boolean,
    @SerializedName("device_name") val deviceName: String = "",
    @SerializedName("date_of_birth") val dateOfBirth: String = ""
)