package com.freqwency.promotr.view.notification.viewmodel.updatenotification

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.freqwency.promotr.R
import com.freqwency.promotr.utils.Event
import com.freqwency.promotr.view.notification.viewmodel.deletenotification.DeleteNotificationBody
import com.freqwency.promotr.view.notification.viewmodel.deletenotification.DeleteNotificationResponse
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
class UpdateNotificationtViewModel @Inject constructor(
    application: Application,
    private val repository: UpdateNotificationRepository
) : AndroidViewModel(application) {
    val progressIndicator = MutableLiveData<Boolean>()
    val errorResponse = MutableLiveData<Throwable>()
    val mUpdateNotificationResponse = MutableLiveData<Event<DeleteNotificationResponse>>()
    var context: Context? = null

    fun getupdateUserNotification(
        updateNotificationBody: UpdateNotificationBody,
        progressDialog: CustomProgressDialog,
        activity: Activity
    ) =
        viewModelScope.launch {
            UpdateUserNotification(updateNotificationBody, progressDialog, activity)
        }

    suspend fun UpdateUserNotification(
        updateNotificationBody: UpdateNotificationBody,
        progressDialog: CustomProgressDialog,
        activity: Activity
    ) {
        progressDialog.start(activity.getString(R.string.please_wait))
        progressIndicator.value = true
        repository.getupdateUserNotification(updateNotificationBody)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<DeleteNotificationResponse>() {
                override fun onNext(value: DeleteNotificationResponse) {
                    progressIndicator.value = false
                    progressDialog.stop()
                    mUpdateNotificationResponse.value = Event(value)
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