package com.freqwency.promotr.view.register.viewmodel

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.freqwency.promotr.R
import com.freqwency.promotr.utils.Event
import com.freqwency.promotr.view.logins.viewmodel.RegistrationResponse
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
class RegisterViewModel @Inject constructor(
    application: Application,
    private val repository: RegisterRepository
) :
    AndroidViewModel(application) {
    val progressIndicator = MutableLiveData<Boolean>()
    val errorResponse = MutableLiveData<Throwable>()
    val mRegisterResponse = MutableLiveData<Event<RegistrationResponse>>()
    var context: Context? = null

    fun registerUser(
        registrationBody: RegistrationBody,
        progressDialog: CustomProgressDialog,
        activity: Activity
    ) = viewModelScope.launch {
        getRegister(registrationBody, progressDialog, activity)
    }

    suspend fun getRegister(
        registrationBody: RegistrationBody,
        progressDialog: CustomProgressDialog,
        activity: Activity
    ) {
        progressDialog.start(activity.getString(R.string.please_wait))
        progressIndicator.value = true
        repository.getRegister(
            registrationBody
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<RegistrationResponse>() {
                override fun onNext(value: RegistrationResponse) {
                    progressIndicator.value = false
                    progressDialog.stop()
                    mRegisterResponse.value = Event(value)
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