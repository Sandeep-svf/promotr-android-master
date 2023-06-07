package com.freqwency.promotr.view.verifymobile.viewmodel.verifyOTP

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.freqwency.promotr.R
import com.freqwency.promotr.beans.VerifyOTPRequest
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
class VerifyOtpViewModel @Inject constructor(
    application: Application,
    private val repository: VerifyOtpRepository
) :
    AndroidViewModel(application) {
    val progressIndicator = MutableLiveData<Boolean>()
    val errorResponse = MutableLiveData<Throwable>()
    val mVerifyOTPResponse = MutableLiveData<Event<VerifyOTPResponse>>()
    var context: Context? = null

    fun verifyOtpByNumber(
        verifyOTPRequest: VerifyOTPRequest,
        progressDialog: CustomProgressDialog,activity: Activity
    ) = viewModelScope.launch {
        verifyOtp(verifyOTPRequest, progressDialog,activity)
    }

    suspend fun verifyOtp(
        verifyOTPRequest: VerifyOTPRequest,
        progressDialog: CustomProgressDialog,activity: Activity
    ) {
        progressDialog.start(activity.getString(R.string.please_wait))
        progressIndicator.value = true
        repository.getVerifyOTP(
            verifyOTPRequest
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<VerifyOTPResponse>() {
                override fun onNext(value: VerifyOTPResponse) {
                    progressIndicator.value = false
                    progressDialog.stop()
                    mVerifyOTPResponse.value = Event(value)
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