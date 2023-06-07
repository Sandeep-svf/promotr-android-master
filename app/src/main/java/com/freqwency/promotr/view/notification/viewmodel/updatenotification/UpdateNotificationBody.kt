package com.freqwency.promotr.view.notification.viewmodel.updatenotification

import com.google.gson.annotations.SerializedName

data class UpdateNotificationBody(
    @SerializedName("notification_ids") var notification_ids: List<Int>,
    @SerializedName("is_read") var is_read: Boolean,
)