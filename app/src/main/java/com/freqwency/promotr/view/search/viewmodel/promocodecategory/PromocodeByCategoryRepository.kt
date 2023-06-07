package com.freqwency.promotr.view.search.viewmodel.promocodecategory

import com.freqwency.promotr.services.ApiService
import io.reactivex.Observable
import javax.inject.Inject

class PromocodeByCategoryRepository @Inject constructor(private val apiService: ApiService) {
    suspend fun getPromoCodeByCategory(id: Int): Observable<PromocodeByCategoryResponse> {
        return apiService.promoCodesByCategory(id)
    }
}