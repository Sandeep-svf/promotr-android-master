package com.freqwency.promotr.view.search.viewmodel.promocodedates

import com.freqwency.promotr.services.ApiService
import com.freqwency.promotr.view.dashboard.viewmodel.promocode.PromocodeResponse
import io.reactivex.Observable
import javax.inject.Inject

class PromocodeByDatesRepository @Inject constructor(private val apiService: ApiService) {
    suspend fun getpromoCodesWithDates(
        dou: Double?,
        id: Int?,
        dateOfBirth: String?
    ): Observable<PromocodeResponse> {
        return apiService.promoCodesWithDates(dou, id, dateOfBirth)
    }
}