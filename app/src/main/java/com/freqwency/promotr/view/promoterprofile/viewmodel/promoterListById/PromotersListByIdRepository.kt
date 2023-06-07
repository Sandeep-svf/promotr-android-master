package com.freqwency.promotr.view.promoterprofile.viewmodel.promoterListById

import com.freqwency.promotr.application.PromotrApp
import com.freqwency.promotr.services.ApiService
import io.reactivex.Observable
import javax.inject.Inject

class PromotersListByIdRepository @Inject constructor(private val apiService: ApiService) {
    suspend fun getPromoterListById(id: Int): Observable<PromoterListByIdResponse> {

        if (PromotrApp.encryptedPrefs.bearerToken.equals("")) {
            return apiService.getPromoterListById(id)
        } else {
            return apiService.getPromoterListByIdProtected(PromotrApp.encryptedPrefs.bearerToken,id)
        }
    }
}