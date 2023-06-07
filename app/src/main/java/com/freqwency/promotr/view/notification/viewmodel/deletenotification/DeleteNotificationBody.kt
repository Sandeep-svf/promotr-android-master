package com.freqwency.promotr.view.notification.viewmodel.deletenotification

import com.google.gson.annotations.SerializedName

data class DeleteNotificationBody (@SerializedName("notification_ids") var notification_ids: List<Int>)