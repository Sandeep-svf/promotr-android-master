package com.freqwency.promotr.view.notification.viewmodel.updatenotification

import com.freqwency.promotr.application.PromotrApp
import com.freqwency.promotr.services.ApiService
import com.freqwency.promotr.view.notification.viewmodel.deletenotification.DeleteNotificationResponse
import io.reactivex.Observable
import javax.inject.Inject

class UpdateNotificationRepository @Inject constructor(private val apiService: ApiService) {
    suspend fun getupdateUserNotification(updateNotificationBody: UpdateNotificationBody): Observable<DeleteNotificationResponse> {
        return apiService.updateUserNotification(PromotrApp.encryptedPrefs.bearerToken,updateNotificationBody)
    }
}