package com.freqwency.promotr.view.notification.viewmodel.deletenotification

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class DeleteNotificationResponse : Serializable {

    @SerializedName("errors")
    var errors: Boolean? = false

    @SerializedName("data")
    var data: Data? = null

    inner class Data : Serializable {
        @SerializedName("notificationsDeletedCount")
        var notificationsDeletedCount = 0

        @SerializedName("notificationsUpdatedCount")
        var notificationsUpdatedCount = 0

    }
}