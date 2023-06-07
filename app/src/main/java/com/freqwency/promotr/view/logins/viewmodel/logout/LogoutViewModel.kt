package com.freqwency.promotr.view.logins.viewmodel.logout

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
class LogoutViewModel @Inject constructor(
    application: Application,
    private val repository: LogoutRepository
) :
    AndroidViewModel(application) {
    val progressIndicator = MutableLiveData<Boolean>()
    val errorResponse = MutableLiveData<Throwable>()
    val mLogoutResponse = MutableLiveData<Event<LogoutResponse>>()
    var context: Context? = null

    fun logoutUser(progressDialog: CustomProgressDialog, activity: Activity) =
        viewModelScope.launch {
            getLogout(progressDialog,activity)
        }

    suspend fun getLogout(progressDialog: CustomProgressDialog, activity: Activity) {

        progressDialog.start(activity.getString(R.string.please_wait))
        progressIndicator.value = true
        repository.getLogout()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<LogoutResponse>() {
                override fun onNext(value: LogoutResponse) {
                    progressIndicator.value = false
                    progressDialog.stop()
                    mLogoutResponse.value = Event(value)
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