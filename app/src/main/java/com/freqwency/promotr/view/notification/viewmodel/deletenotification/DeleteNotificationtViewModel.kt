package com.freqwency.promotr.view.notification.viewmodel.deletenotification

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
class DeleteNotificationtViewModel @Inject constructor(
    application: Application,
    private val repository: DeleteNotificationRepository
) : AndroidViewModel(application) {
    val progressIndicator = MutableLiveData<Boolean>()
    val errorResponse = MutableLiveData<Throwable>()
    val mDeleteNotificationResponse = MutableLiveData<Event<DeleteNotificationResponse>>()
    var context: Context? = null

    fun getDeleteNotification(
        deleteNotificationBody: DeleteNotificationBody,
        progressDialog: CustomProgressDialog,
        activity: Activity
    ) =
        viewModelScope.launch {
            DeleteNotification(deleteNotificationBody, progressDialog, activity)
        }

    suspend fun DeleteNotification(
        deleteNotificationBody: DeleteNotificationBody,
        progressDialog: CustomProgressDialog,
        activity: Activity
    ) {
        progressDialog.start(activity.getString(R.string.please_wait))
        progressIndicator.value = true
        repository.getDeleteNotification(deleteNotificationBody)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<DeleteNotificationResponse>() {
                override fun onNext(value: DeleteNotificationResponse) {
                    progressIndicator.value = false
                    progressDialog.stop()
                    mDeleteNotificationResponse.value = Event(value)
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