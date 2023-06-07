package com.freqwency.promotr.view.AddPromoCode.viewmodel.ViewPromoCode

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
class ViewPromoCodeViewModel @Inject constructor(application: Application, private val viewPromoCodeRepository: ViewPromoCodeRepository
) : AndroidViewModel(application) {
    val progressIndicator = MutableLiveData<Boolean>()
    val errorResponse = MutableLiveData<Throwable>()
    val mViewPromoCodeResponse = MutableLiveData<Event<PromocodeResponse>>()
    var context: Context? = null

    fun getViewPromoCodeForPromoter(
        progressDialog: CustomProgressDialog,
        activity: Activity
    ) =
        viewModelScope.launch {
            ViewPromoCodeForPromoter(progressDialog, activity)
        }

    suspend fun ViewPromoCodeForPromoter(progressDialog: CustomProgressDialog, activity: Activity) {
        progressDialog.start(activity.getString(R.string.please_wait))
        progressIndicator.value = true
        viewPromoCodeRepository.getPromoCodeForPromoter()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<PromocodeResponse>() {
                override fun onNext(value: PromocodeResponse) {
                    progressIndicator.value = false
                    progressDialog.stop()
                    mViewPromoCodeResponse.value = Event(value)
                }

                override fun onError(e: Throwable) {
                    progressIndicator.value = false
                    progressDialog.stop()
                    errorResponse.value = e
                }

                override fun onComplete() {
                    progressDialog.stop()
                    progressIndicator.value = false
                }
            })
    }
}