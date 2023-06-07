package com.freqwency.promotr.view.verifymobile.viewmodel.regquestOTP

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
class GetsOtpViewModel @Inject constructor(
    application: Application,
    private val repository: GetsOtpRepository
) :
    AndroidViewModel(application) {
    val progressIndicator = MutableLiveData<Boolean>()
    val errorResponse = MutableLiveData<Throwable>()
    val mGetOTPResponse = MutableLiveData<Event<GetsOTPResponse>>()
    var context: Context? = null


    fun otpByNumber(
        requestOTPRequest: RequestOTPRequest,
        progressDialog: CustomProgressDialog,
        activity: Activity
    ) = viewModelScope.launch {
        getOtp(requestOTPRequest,progressDialog,activity)
    }

    suspend fun getOtp(requestOTPRequest: RequestOTPRequest,progressDialog: CustomProgressDialog,activity: Activity) {

        progressDialog.start(activity.getString(R.string.please_wait))
        progressIndicator.value = true
        repository.getOTP(
            requestOTPRequest
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<GetsOTPResponse>() {
                override fun onNext(value: GetsOTPResponse) {
                    progressIndicator.value = false
                    progressDialog.stop()
                    mGetOTPResponse.value = Event(value)
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