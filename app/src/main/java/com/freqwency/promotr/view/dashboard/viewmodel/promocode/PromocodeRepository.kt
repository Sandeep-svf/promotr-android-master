package com.freqwency.promotr.view.dashboard.viewmodel.promocode

import com.freqwency.promotr.services.ApiService
import com.freqwency.promotr.view.dashboard.viewmodel.category.CategoryResponse
import io.reactivex.Observable
import javax.inject.Inject

class PromocodeRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun getPromoCode(): Observable<PromocodeResponse> {
        return apiService.promoCodes()
    }
}