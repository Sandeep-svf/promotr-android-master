package com.freqwency.promotr.view.verifymobile.viewmodel.regquestOTP

import com.freqwency.promotr.application.PromotrApp
import com.freqwency.promotr.services.ApiService
import io.reactivex.Observable
import retrofit2.Response
import javax.inject.Inject

class GetsOtpRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun getOTP(requestOTPRequest: RequestOTPRequest): Observable<GetsOTPResponse> {
        return apiService.getOTP(PromotrApp.encryptedPrefs.appLanguage, requestOTPRequest)
    }
}