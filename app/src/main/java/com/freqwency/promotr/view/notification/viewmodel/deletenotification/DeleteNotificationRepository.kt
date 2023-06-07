package com.freqwency.promotr.view.notification.viewmodel.deletenotification

import com.freqwency.promotr.application.PromotrApp
import com.freqwency.promotr.services.ApiService
import io.reactivex.Observable
import javax.inject.Inject

class DeleteNotificationRepository @Inject constructor(private val apiService: ApiService) {
    suspend fun getDeleteNotification(deleteNotificationBody: DeleteNotificationBody): Observable<DeleteNotificationResponse> {
        return apiService.DeleteNotification(PromotrApp.encryptedPrefs.bearerToken,deleteNotificationBody)
    }
}