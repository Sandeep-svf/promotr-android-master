package com.freqwency.promotr.view.notification.viewmodel.notificationlist

import com.freqwency.promotr.services.ApiService
import io.reactivex.Observable
import javax.inject.Inject

class NotificationListRepository @Inject constructor(private val apiService: ApiService) {
    suspend fun getNotificationList(): Observable<NotificationListResponse> {
        return apiService.notificationList()
    }
}