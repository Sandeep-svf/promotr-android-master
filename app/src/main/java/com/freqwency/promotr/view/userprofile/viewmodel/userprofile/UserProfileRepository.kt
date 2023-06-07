package com.freqwency.promotr.view.userprofile.viewmodel.userprofile

import com.freqwency.promotr.application.PromotrApp
import com.freqwency.promotr.services.ApiService
import com.freqwency.promotr.view.userprofile.viewmodel.UserProfileResponse
import io.reactivex.Observable
import javax.inject.Inject

class UserProfileRepository @Inject constructor(private val apiService: ApiService) {
    suspend fun getUserProfile(): Observable<UserProfileResponse> {
        return apiService.userProfile(PromotrApp.encryptedPrefs.bearerToken)
    }
}