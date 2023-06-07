package com.freqwency.promotr.view.AddPromoCode.viewmodel.DeletePromoCode

import com.freqwency.promotr.application.PromotrApp
import com.freqwency.promotr.services.ApiService
import com.freqwency.promotr.view.AddPromoCode.viewmodel.AddPromoCode.AddPromoCodeResponse
import io.reactivex.Observable
import javax.inject.Inject

class DeletePromoCodeRepository @Inject constructor(private val apiService: ApiService) {
    suspend fun getdeletePromoCodeForPromoter(promoCodeId: Int
    ): Observable<AddPromoCodeResponse> {
        return apiService.deletePromoCodeForPromoter(PromotrApp.encryptedPrefs.bearerToken,promoCodeId)
    }
}