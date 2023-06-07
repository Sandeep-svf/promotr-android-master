package com.freqwency.promotr.view.AddPromoCode.viewmodel.EditPromoCode

import com.freqwency.promotr.application.PromotrApp
import com.freqwency.promotr.services.ApiService
import com.freqwency.promotr.view.AddPromoCode.viewmodel.AddPromoCode.AddPromoCodeResponse
import io.reactivex.Observable
import javax.inject.Inject

class EditPromoCodeRepository @Inject constructor(private val apiService: ApiService) {
    suspend fun getEditPromoCodeForPromoter(
        categoryId: Int,
        title: String,
        type: String,
        discount: String,
        storeName: String,
        storeLink: String,
        codeId: String,
        dateOfBirth: String,
        description: String,
        promoCodeId: Int
    ): Observable<AddPromoCodeResponse> {
        return apiService.editPromoCodeForPromoter(PromotrApp.encryptedPrefs.bearerToken,promoCodeId,title,description,codeId,storeName,storeLink,discount,dateOfBirth,type,categoryId,"PUT")
    }
}