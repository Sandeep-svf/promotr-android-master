package com.freqwency.promotr.view.logins.viewmodel.updateFCMToken

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.freqwency.promotr.utils.Event
import com.freqwency.promotr.view.logins.viewmodel.logout.LogoutResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class UpdateFCMTokenViewModel @Inject constructor(
    application: Application,
    private val updateFCMTokenRepository: UpdateFCMTokenRepository
) : AndroidViewModel(application) {
    val progressIndicator = MutableLiveData<Boolean>()
    val errorResponse = MutableLiveData<Throwable>()
    val mUpdateFCMTokenResponse = MutableLiveData<Event<LogoutResponse>>()

    fun getUpdateFCMToken(fcmTokenBody: FcmTokenBody) =
        viewModelScope.launch {
            updateFCMToken(fcmTokenBody)
        }

    private suspend fun updateFCMToken(fcmTokenBody: FcmTokenBody) {
        progressIndicator.value = true
        updateFCMTokenRepository.getUpdateFCMToken(fcmTokenBody)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<LogoutResponse>() {
                override fun onNext(value: LogoutResponse) {
                    progressIndicator.value = false
                    mUpdateFCMTokenResponse.value = Event(value)
                }

                override fun onError(e: Throwable) {
                    progressIndicator.value = false
                    errorResponse.value = e
                }

                override fun onComplete() {
                    progressIndicator.value = false
                }
            })
    }
}