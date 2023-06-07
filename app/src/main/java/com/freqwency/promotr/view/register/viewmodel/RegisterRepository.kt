package com.freqwency.promotr.view.register.viewmodel

import com.freqwency.promotr.application.PromotrApp
import com.freqwency.promotr.services.ApiService
import com.freqwency.promotr.view.logins.viewmodel.RegistrationResponse
import io.reactivex.Observable
import javax.inject.Inject

class RegisterRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun getRegister(registrationBody: RegistrationBody): Observable<RegistrationResponse> {
        return apiService.register(PromotrApp.encryptedPrefs.appLanguage, registrationBody)
    }
}