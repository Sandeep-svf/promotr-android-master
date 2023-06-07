package com.freqwency.promotr.view.favourite.viewmodel

import com.freqwency.promotr.application.PromotrApp
import com.freqwency.promotr.services.ApiService
import io.reactivex.Observable
import javax.inject.Inject

class PromocodeShowFavRepository @Inject constructor(private val apiService: ApiService) {
    suspend fun getpromoCodeShowFav(): Observable<PromocodeShowFavResponse> {
        return apiService.promoCodeShowFav(PromotrApp.encryptedPrefs.bearerToken)
    }
}