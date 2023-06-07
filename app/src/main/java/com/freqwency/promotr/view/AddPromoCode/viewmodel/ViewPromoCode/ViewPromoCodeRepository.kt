package com.freqwency.promotr.view.AddPromoCode.viewmodel.ViewPromoCode

import com.freqwency.promotr.application.PromotrApp
import com.freqwency.promotr.services.ApiService
import com.freqwency.promotr.view.dashboard.viewmodel.promocode.PromocodeResponse
import io.reactivex.Observable
import javax.inject.Inject

class ViewPromoCodeRepository @Inject constructor(private val apiService: ApiService) {
    suspend fun getPromoCodeForPromoter(): Observable<PromocodeResponse> {
        return apiService.getPromoCodeForPromoter(PromotrApp.encryptedPrefs.bearerToken)
    }
}