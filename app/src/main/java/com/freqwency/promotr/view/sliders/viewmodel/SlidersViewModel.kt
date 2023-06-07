package com.freqwency.promotr.view.sliders.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.freqwency.promotr.model.GetSlidersResponse
import com.freqwency.promotr.services.SlidersRepository
import com.freqwency.promotr.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class SlidersViewModel @Inject constructor(
    application: Application,
    private val repository: SlidersRepository
) :
    AndroidViewModel(application) {
    val progressIndicator = MutableLiveData<Boolean>()
    val errorResponse = MutableLiveData<Throwable>()
    val getSliderResponse = MutableLiveData<Event<GetSlidersResponse>>()

    fun slider() = viewModelScope.launch {
        sliders()
    }

    fun sliders() {
        progressIndicator.value = true
        repository.getSlider()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<GetSlidersResponse>() {
                override fun onNext(value: GetSlidersResponse) {
                    progressIndicator.value = false
                    getSliderResponse.value = Event(value)
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