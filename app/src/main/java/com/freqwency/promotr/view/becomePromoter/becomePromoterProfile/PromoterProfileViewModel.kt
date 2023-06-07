package com.freqwency.promotr.view.becomePromoter.becomePromoterProfile

import android.app.Activity
import android.app.Application
import android.content.Context
import android.util.Log
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
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class PromoterProfileViewModel @Inject constructor(application: Application,
                                                   private val promoterProfileRepository: PromoterProfileRepository
): AndroidViewModel(application) {
    val progressIndicator = MutableLiveData<Boolean>()
    val errorResponse = MutableLiveData<Throwable>()
    val mPromoterProfileResponse = MutableLiveData<Event<PromoterProfileResponse>>()
    var context: Context? = null

    fun getBecomePromoterProfile(progressDialog: CustomProgressDialog, activity: Activity,image: MultipartBody.Part, about: RequestBody, instaUrl: RequestBody,
                       facebookUrl: RequestBody, nickname: RequestBody) =
        viewModelScope.launch {
            BecomePromoterProfile(progressDialog,activity,image,about,instaUrl,facebookUrl, nickname)
        }

    suspend fun BecomePromoterProfile(progressDialog: CustomProgressDialog, activity: Activity, image: MultipartBody.Part, about: RequestBody, instaUrl: RequestBody,
                            facebookUrl: RequestBody, nickname: RequestBody
    ) {
        progressDialog.start(activity.getString(R.string.please_wait))
        progressIndicator.value = true
        promoterProfileRepository.getBecomePromoterProfile(image,about,instaUrl,facebookUrl, nickname)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<PromoterProfileResponse>() {
                override fun onNext(value: PromoterProfileResponse) {
                    progressIndicator.value = false
                    progressDialog.stop()
                    mPromoterProfileResponse.value = Event(value)
                }

                override fun onError(e: Throwable) {
                    progressIndicator.value = false
                    progressDialog.stop()
                    Log.e("mylog", "Error   "+e)
                    errorResponse.value = e
                }

                override fun onComplete() {
                    progressIndicator.value = false
                    progressDialog.stop()
                }
            })
    }
}