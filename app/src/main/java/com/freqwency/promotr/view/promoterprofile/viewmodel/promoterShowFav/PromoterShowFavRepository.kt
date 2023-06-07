package com.freqwency.promotr.view.promoterprofile.viewmodel.promoterShowFav

import com.freqwency.promotr.application.PromotrApp
import com.freqwency.promotr.services.ApiService
import io.reactivex.Observable
import javax.inject.Inject

class PromoterShowFavRepository @Inject constructor(private val apiService: ApiService) {
    suspend fun getpromoterShowFav(): Observable<PromoterShowFavResponse> {
        return apiService.promoterShowFav(PromotrApp.encryptedPrefs.bearerToken)
    }
}