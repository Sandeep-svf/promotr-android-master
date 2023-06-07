package com.freqwency.promotr.view.logins.viewmodel.updateFCMToken

import com.freqwency.promotr.application.PromotrApp
import com.freqwency.promotr.services.ApiService
import com.freqwency.promotr.view.logins.viewmodel.logout.LogoutResponse
import io.reactivex.Observable
import javax.inject.Inject

class UpdateFCMTokenRepository @Inject constructor(private val apiService: ApiService) {
     fun getUpdateFCMToken(fcmTokenBody: FcmTokenBody): Observable<LogoutResponse> {
        return apiService.updateFCMToken(PromotrApp.encryptedPrefs.bearerToken,fcmTokenBody)
    }
}