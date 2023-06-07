package com.freqwency.promotr.view.promocodedetails.viewmodel.promoCodeSaveFav

import com.freqwency.promotr.application.PromotrApp
import com.freqwency.promotr.services.ApiService
import io.reactivex.Observable
import javax.inject.Inject

class PromocodeSaveFavRepository @Inject constructor(private val apiService: ApiService) {
    suspend fun getPromoCodeSaveFav(id: Int): Observable<PromocodeSaveFavResponse> {
        return apiService.promoCodesSaveFav(PromotrApp.encryptedPrefs.bearerToken,id)
    }
}