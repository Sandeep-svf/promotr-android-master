package com.freqwency.promotr.view.userprofile.viewmodel.updateprofile

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.freqwency.promotr.R
import com.freqwency.promotr.utils.Event
import com.freqwency.promotr.view.userprofile.viewmodel.UserProfileResponse
import com.johncodeos.customprogressdialogexample.CustomProgressDialog
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class UpdateUserProfileViewModel @Inject constructor(application: Application,
                                                     private val updateUserProfileRepository: UpdateUserProfileRepository
): AndroidViewModel(application) {
    val progressIndicator = MutableLiveData<Boolean>()
    val errorResponse = MutableLiveData<Throwable>()
    val mUpdateUserProfileResponse = MutableLiveData<Event<UserProfileResponse>>()

    fun getUpdateUserProfile(
        progressDialog: CustomProgressDialog,
        activity: Activity,
        requestBody: RequestBody
    ) =
        viewModelScope.launch {
            UpdateUserProfile(progressDialog,activity,requestBody)
        }

    suspend fun UpdateUserProfile(progressDialog: CustomProgressDialog, activity: Activity, requestBody: RequestBody) {
        progressDialog.start(activity.getString(R.string.please_wait))
        progressIndicator.value = true
        updateUserProfileRepository.getUpdateUserProfile(requestBody)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<UserProfileResponse>() {
                override fun onNext(value: UserProfileResponse) {
                    progressIndicator.value = false
                    progressDialog.stop()
                    mUpdateUserProfileResponse.value = Event(value)
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