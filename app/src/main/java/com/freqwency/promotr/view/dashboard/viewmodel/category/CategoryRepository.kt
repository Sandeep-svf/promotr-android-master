package com.freqwency.promotr.view.dashboard.viewmodel.category

import com.freqwency.promotr.services.ApiService
import io.reactivex.Observable
import javax.inject.Inject

class CategoryRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun getCategory(): Observable<CategoryResponse> {
        return apiService.categories()
    }
}