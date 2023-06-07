package com.freqwency.promotr.view.userprofile.viewmodel.DeleteAccount

import com.freqwency.promotr.application.PromotrApp
import com.freqwency.promotr.services.ApiService
import com.freqwency.promotr.view.logins.viewmodel.logout.LogoutResponse
import io.reactivex.Observable
import javax.inject.Inject

class DeleteAccountRepository @Inject constructor(private val apiService: ApiService) {
    suspend fun getDeleteUserProfile(): Observable<LogoutResponse> {
        return apiService.deleteUserProfile(PromotrApp.encryptedPrefs.bearerToken)
    }
}