package com.freqwency.promotr.view.promocodedetails.viewmodel.promoCodeDeleteFav

import com.freqwency.promotr.application.PromotrApp
import com.freqwency.promotr.services.ApiService
import com.freqwency.promotr.view.logins.viewmodel.logout.LogoutResponse
import io.reactivex.Observable
import javax.inject.Inject

class PromocodeDeleteFavRepository @Inject constructor(private val apiService: ApiService) {
    suspend fun getDeleteFavouritePromoCode(id: Int): Observable<LogoutResponse> {
        return apiService.deleteFavouritePromoCode(PromotrApp.encryptedPrefs.bearerToken,id)
    }
}