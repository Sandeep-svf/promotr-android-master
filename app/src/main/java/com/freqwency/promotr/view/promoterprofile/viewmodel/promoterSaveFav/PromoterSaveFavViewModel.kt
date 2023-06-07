package com.freqwency.promotr.view.promoterprofile.viewmodel.promoterSaveFav

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
class PromoterSaveFavViewModel @Inject constructor(application: Application,
                                                   private val promoterSaveFavRepository: PromoterSaveFavRepository
): AndroidViewModel(application) {
    val progressIndicator = MutableLiveData<Boolean>()
    val errorResponse = MutableLiveData<Throwable>()
    val mPromoterSaveFavResponse = MutableLiveData<Event<PromoterSaveFavResponse>>()
    var context: Context? = null

    fun getpromoterSaveFav(progressDialog: CustomProgressDialog, activity: Activity,promoterBody: PromoterBody) =
        viewModelScope.launch {
            promoterSaveFav(progressDialog,activity,promoterBody)
        }

    suspend fun promoterSaveFav(progressDialog: CustomProgressDialog, activity: Activity,promoterBody: PromoterBody) {
        progressDialog.start(activity.getString(R.string.please_wait))
        progressIndicator.value = true
        promoterSaveFavRepository.getpromoterSaveFav(promoterBody)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<PromoterSaveFavResponse>() {
                override fun onNext(value: PromoterSaveFavResponse) {
                    progressIndicator.value = false
                    progressDialog.stop()
                    mPromoterSaveFavResponse.value = Event(value)
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