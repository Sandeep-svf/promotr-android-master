package com.freqwency.promotr.services

import com.freqwency.promotr.model.GetSlidersResponse
import com.freqwency.promotr.services.ApiService
import io.reactivex.Observable
import javax.inject.Inject

class SlidersRepository @Inject constructor(private val apiService: ApiService) {
    fun getSlider(): Observable<GetSlidersResponse> {
        return apiService.getSliders()
    }
}