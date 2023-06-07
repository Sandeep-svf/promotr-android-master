package com.freqwency.promotr.view.userprofile.viewmodel.updateprofile

import com.freqwency.promotr.application.PromotrApp
import com.freqwency.promotr.services.ApiService
import com.freqwency.promotr.view.userprofile.viewmodel.UserProfileResponse
import io.reactivex.Observable
import okhttp3.RequestBody
import javax.inject.Inject

class UpdateUserProfileRepository @Inject constructor(private val apiService: ApiService) {
    suspend fun getUpdateUserProfile(requestBody: RequestBody): Observable<UserProfileResponse> {
        return apiService.updateUserProfile(PromotrApp.encryptedPrefs.bearerToken,requestBody)
    }
}