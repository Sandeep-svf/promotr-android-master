package com.freqwency.promotr.view.forgotpassword.viewmodel

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.freqwency.promotr.R
import com.freqwency.promotr.utils.*
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
class ForgotViewModel @Inject constructor(
    application: Application,
    private val repository: ForgotRepository
) :
    AndroidViewModel(application) {
    val progressIndicator = MutableLiveData<Boolean>()
    val errorResponse = MutableLiveData<Throwable>()
    val mLoginResponse = MutableLiveData<Event<ForgotResponse>>()
    var context: Context? =null
    
    fun ForgotPass(forgotBody: ForgotBody, progressDialog: CustomProgressDialog, activity: Activity) = viewModelScope.launch {
        getForgots(forgotBody,progressDialog,activity)
    }

    suspend fun getForgots(forgotBody: ForgotBody,progressDialog: CustomProgressDialog,activity: Activity) {
        progressDialog.start(activity.getString(R.string.please_wait))
        progressIndicator.value = true
        repository.getForgot(
            forgotBody)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<ForgotResponse>() {
                override fun onNext(value: ForgotResponse) {
                    progressIndicator.value = false
                    progressDialog.stop()
                    mLoginResponse.value = Event(value)
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