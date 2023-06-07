package com.freqwency.promotr.view.logins.viewmodel.logout
import com.freqwency.promotr.application.PromotrApp
import com.freqwency.promotr.services.ApiService
import io.reactivex.Observable
import javax.inject.Inject

class LogoutRepository @Inject constructor(private val apiService: ApiService) {
    suspend fun getLogout(): Observable<LogoutResponse> {
        return apiService.logout(PromotrApp.encryptedPrefs.bearerToken)
    }
}