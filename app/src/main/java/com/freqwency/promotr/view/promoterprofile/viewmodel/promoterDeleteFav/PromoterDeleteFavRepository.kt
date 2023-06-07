package com.freqwency.promotr.view.promoterprofile.viewmodel.promoterDeleteFav

import com.freqwency.promotr.application.PromotrApp
import com.freqwency.promotr.services.ApiService
import com.freqwency.promotr.view.logins.viewmodel.logout.LogoutResponse
import io.reactivex.Observable
import javax.inject.Inject

class PromoterDeleteFavRepository @Inject constructor(private val apiService: ApiService) {
    suspend fun getdeleteFavouritePromoter(promoterId: Int): Observable<LogoutResponse> {
        return apiService.deleteFavouritePromoter(PromotrApp.encryptedPrefs.bearerToken,promoterId)
    }
}