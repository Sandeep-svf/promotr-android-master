package com.freqwency.promotr.view.AddPromoCode.viewmodel.AddPromoCode

import com.freqwency.promotr.application.PromotrApp
import com.freqwency.promotr.services.ApiService
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class AddPromoCodeRepository @Inject constructor(private val apiService: ApiService) {
    suspend fun getAddPromoCodeForPromoter(
        categoryId: RequestBody,
        title: RequestBody,
        type: RequestBody,
        discount: RequestBody,
        storeName: RequestBody,
        storeLink: RequestBody,
        codeId: RequestBody,
        dateOfBirth: RequestBody,
        description: RequestBody,
        images: MultipartBody.Part
    ): Observable<AddPromoCodeResponse> {
        return apiService.addPromoCodeForPromoter(PromotrApp.encryptedPrefs.bearerToken,title,description,codeId,storeName,storeLink,discount,dateOfBirth,type,categoryId,images)
    }
}