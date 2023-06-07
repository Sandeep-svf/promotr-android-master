package com.freqwency.promotr.view.notification.viewmodel.notificationlist

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class NotificationListResponse : Serializable {
    @SerializedName("errors")
    var errors: Boolean? = false

    @SerializedName("data")
    var data: Data? = null

    inner class Data : Serializable {
        @SerializedName("notifications")
        var notificationsList: ArrayList<Notifications> = ArrayList()
    }

    inner class Notifications : Serializable {
        @SerializedName("id")
        var id = 0

        @SerializedName("title")
        var title = ""

        @SerializedName("body")
        var body = ""

        @SerializedName("is_read")
        var is_read: Boolean? = false

        @SerializedName("receiver_id")
        var receiver_id = 0

        @SerializedName("channel")
        var channel = ""

        @SerializedName("created_at")
        var created_at = ""

        @SerializedName("updated_at")
        var updated_at = ""

        @SerializedName("isCheck")
        var isSelect: Boolean = false

    }
}