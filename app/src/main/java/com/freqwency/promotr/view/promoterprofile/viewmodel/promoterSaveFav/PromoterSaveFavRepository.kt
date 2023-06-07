package com.freqwency.promotr.view.promoterprofile.viewmodel.promoterSaveFav

import com.freqwency.promotr.application.PromotrApp
import com.freqwency.promotr.services.ApiService
import io.reactivex.Observable
import javax.inject.Inject

class PromoterSaveFavRepository @Inject constructor(private val apiService: ApiService) {
    suspend fun getpromoterSaveFav(promoterBody: PromoterBody): Observable<PromoterSaveFavResponse> {
        return apiService.promoterSaveFav(PromotrApp.encryptedPrefs.bearerToken,promoterBody)
    }
}