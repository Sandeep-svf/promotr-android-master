package com.freqwency.promotr.view.promocodedetails.viewmodel.promoCodeDetails

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.freqwency.promotr.R
import com.freqwency.promotr.utils.Event
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
class PromoCodeDetailsViewModel @Inject constructor(application: Application,
                                                    private val promocodeDetailsRepository: PromocodeDetailsRepository
): AndroidViewModel(application){
    val progressIndicator = MutableLiveData<Boolean>()
    val errorResponse = MutableLiveData<Throwable>()
    val mPromocodeDetailsResponse = MutableLiveData<Event<PromocodeDetailsResponse>>()
    var context: Context? = null

    fun getpromoCodeDetails(progressDialog: CustomProgressDialog, activity: Activity, i: Int) =
        viewModelScope.launch {
            promoCodeByCategory(progressDialog,activity,i)
        }

    suspend fun promoCodeByCategory(progressDialog: CustomProgressDialog, activity: Activity, id: Int) {
        progressDialog.start(activity.getString(R.string.please_wait))
        progressIndicator.value = true
        promocodeDetailsRepository.getPromoCodeDetails(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<PromocodeDetailsResponse>() {
                override fun onNext(value: PromocodeDetailsResponse) {
                    progressIndicator.value = false
                    progressDialog.stop()
                    mPromocodeDetailsResponse.value = Event(value)
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