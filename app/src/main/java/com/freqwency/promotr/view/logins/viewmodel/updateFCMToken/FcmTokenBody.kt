package com.freqwency.promotr.view.logins.viewmodel.updateFCMToken

import com.google.gson.annotations.SerializedName

data class FcmTokenBody(@SerializedName("fcm_token") var fcm_token: String)