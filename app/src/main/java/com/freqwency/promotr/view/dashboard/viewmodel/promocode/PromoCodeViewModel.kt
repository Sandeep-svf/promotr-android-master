package com.freqwency.promotr.view.dashboard.viewmodel.promocode

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.freqwency.promotr.R
import com.freqwency.promotr.utils.Event
import com.freqwency.promotr.view.dashboard.viewmodel.category.CategoryRepository
import com.freqwency.promotr.view.dashboard.viewmodel.category.CategoryResponse
import com.johncodeos.customprogressdialogexample.CustomProgressDialog
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class PromoCodeViewModel @Inject constructor(
    application: Application,
    private val promocodeRepository: PromocodeRepository
) : AndroidViewModel(application) {
    val progressIndicator = MutableLiveData<Boolean>()
    val errorResponse = MutableLiveData<Throwable>()
    val mPromocodeResponse = MutableLiveData<Event<PromocodeResponse>>()
    var context: Context? = null

    fun getpromoCode() =
        viewModelScope.launch {
            promoCode()
        }

    suspend fun promoCode() {
        progressIndicator.value = true
        promocodeRepository.getPromoCode()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<PromocodeResponse>() {
                override fun onNext(value: PromocodeResponse) {
                    progressIndicator.value = false
                    mPromocodeResponse.value = Event(value)
                }

                override fun onError(e: Throwable) {
                    progressIndicator.value = false
                    errorResponse.value = e
                }

                override fun onComplete() {
                    progressIndicator.value = false
                }
            })
    }
}