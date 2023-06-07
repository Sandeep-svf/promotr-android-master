package com.freqwency.promotr.view.verifymobile.viewmodel.verifyOTP

import com.freqwency.promotr.application.PromotrApp
import com.freqwency.promotr.beans.VerifyOTPRequest
import com.freqwency.promotr.services.ApiService
import io.reactivex.Observable
import retrofit2.Response
import javax.inject.Inject

class VerifyOtpRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun getVerifyOTP(verifyOTPRequest: VerifyOTPRequest): Observable<VerifyOTPResponse> {
        return apiService.verifyOTP(PromotrApp.encryptedPrefs.appLanguage, verifyOTPRequest)
    }
}