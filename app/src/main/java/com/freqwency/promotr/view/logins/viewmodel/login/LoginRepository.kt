package com.freqwency.promotr.view.logins.viewmodel.login

import com.freqwency.promotr.application.PromotrApp
import com.freqwency.promotr.services.ApiService
import com.freqwency.promotr.view.logins.viewmodel.RegistrationResponse
import io.reactivex.Observable
import javax.inject.Inject

class LoginRepository @Inject constructor(private val apiService: ApiService) {

     fun getLogin(dataModelLoginBody: LoginBody): Observable<RegistrationResponse> {
        return apiService.login(PromotrApp.encryptedPrefs.appLanguage, dataModelLoginBody)
    }
}