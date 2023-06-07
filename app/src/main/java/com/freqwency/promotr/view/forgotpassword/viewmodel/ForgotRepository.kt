package com.freqwency.promotr.view.forgotpassword.viewmodel

import com.freqwency.promotr.application.PromotrApp
import com.freqwency.promotr.services.ApiService
import io.reactivex.Observable
import javax.inject.Inject

class ForgotRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun getForgot(forgotBody: ForgotBody): Observable<ForgotResponse> {
        return apiService.ForgotPassword(PromotrApp.encryptedPrefs.appLanguage, forgotBody)
    }
}