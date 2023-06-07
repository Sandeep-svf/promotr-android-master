package com.freqwency.promotr.view.promoterprofile.viewmodel.promoterShowFav

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
class PromoterShowFavViewModel @Inject constructor(application: Application,
                                                   private val promoterShowFavRepository: PromoterShowFavRepository
): AndroidViewModel(application){
    val progressIndicator = MutableLiveData<Boolean>()
    val errorResponse = MutableLiveData<Throwable>()
    val mPromoterShowFavResponse = MutableLiveData<Event<PromoterShowFavResponse>>()
    var context: Context? = null

    fun getpromoterShowFav(progressDialog: CustomProgressDialog, activity: Activity) =
        viewModelScope.launch {
            promoterShowFav(progressDialog,activity)
        }

    suspend fun promoterShowFav(progressDialog: CustomProgressDialog, activity: Activity) {
        progressDialog.start(activity.getString(R.string.please_wait))
        progressIndicator.value = true
        promoterShowFavRepository.getpromoterShowFav()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<PromoterShowFavResponse>() {
                override fun onNext(value: PromoterShowFavResponse) {
                    progressIndicator.value = false
                    progressDialog.stop()
                    mPromoterShowFavResponse.value = Event(value)
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