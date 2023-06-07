package com.freqwency.promotr.view.dashboard.viewmodel.promotersList

import com.freqwency.promotr.services.ApiService
import io.reactivex.Observable
import javax.inject.Inject

class PromotersListRepository @Inject constructor(private val apiService: ApiService) {
    suspend fun getPromoterList(): Observable<PromotersListResponse> {
        return apiService.getPromoterList()
    }
}