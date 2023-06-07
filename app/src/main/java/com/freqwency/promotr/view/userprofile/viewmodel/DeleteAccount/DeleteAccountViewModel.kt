package com.freqwency.promotr.view.userprofile.viewmodel.DeleteAccount

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.freqwency.promotr.R
import com.freqwency.promotr.utils.Event
import com.freqwency.promotr.view.logins.viewmodel.logout.LogoutResponse
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
class DeleteAccountViewModel  @Inject constructor(
    application: Application,
    private val repository: DeleteAccountRepository
) :
    AndroidViewModel(application) {

    val progressIndicator = MutableLiveData<Boolean>()
    val errorResponse = MutableLiveData<Throwable>()
    val mDeleteAccountResponse = MutableLiveData<Event<LogoutResponse>>()
    var context: Context? = null

    fun DeleteUserProfile(progressDialog: CustomProgressDialog, activity: Activity) =
        viewModelScope.launch {
            getDeleteUserProfile(progressDialog,activity)
        }

    suspend fun getDeleteUserProfile(progressDialog: CustomProgressDialog, activity: Activity) {

        progressDialog.start(activity.getString(R.string.please_wait))
        progressIndicator.value = true
        repository.getDeleteUserProfile()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<LogoutResponse>() {
                override fun onNext(value: LogoutResponse) {
                    progressIndicator.value = false
                    progressDialog.stop()
                    mDeleteAccountResponse.value = Event(value)
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