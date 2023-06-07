package com.freqwency.promotr.view.dashboard.viewmodel.promotersList

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.freqwency.promotr.R
import com.freqwency.promotr.utils.Event
import com.freqwency.promotr.view.dashboard.viewmodel.promocode.PromocodeResponse
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
class PromotersListViewModel @Inject constructor(
    application: Application,
    private val promotersListRepository: PromotersListRepository
) : AndroidViewModel(application) {
    val progressIndicator = MutableLiveData<Boolean>()
    val errorResponse = MutableLiveData<Throwable>()
    val mPromotersListResponse = MutableLiveData<Event<PromotersListResponse>>()
    var context: Context? = null

    fun getPromotersList(progressDialog: CustomProgressDialog, activity: Activity) =
        viewModelScope.launch {
            PromotersList(progressDialog,activity)
        }

    suspend fun PromotersList(progressDialog: CustomProgressDialog, activity: Activity) {
        progressIndicator.value = true
        progressDialog.start(activity.getString(R.string.please_wait))
        promotersListRepository.getPromoterList()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<PromotersListResponse>() {
                override fun onNext(value: PromotersListResponse) {
                    progressIndicator.value = false
                    progressDialog.stop()
                    mPromotersListResponse.value = Event(value)
                }

                override fun onError(e: Throwable) {
                    progressIndicator.value = false
                    progressDialog.stop()
                    errorResponse.value = e
                }

                override fun onComplete() {
                    progressIndicator.value = false
                    progressDialog.stop()
                }
            })
    }
}