package com.freqwency.promotr.view.promocodedetails.viewmodel.promoCodeDetails

import com.freqwency.promotr.application.PromotrApp
import com.freqwency.promotr.services.ApiService
import io.reactivex.Observable
import javax.inject.Inject

class PromocodeDetailsRepository @Inject constructor(private val apiService: ApiService) {
    suspend fun getPromoCodeDetails(id: Int): Observable<PromocodeDetailsResponse> {

        if (PromotrApp.encryptedPrefs.bearerToken.equals("")) {
            return apiService.promoCodesDettails(id)
        } else {
            return apiService.promoCodesDettailsProtected(PromotrApp.encryptedPrefs.bearerToken, id)
        }
    }
}