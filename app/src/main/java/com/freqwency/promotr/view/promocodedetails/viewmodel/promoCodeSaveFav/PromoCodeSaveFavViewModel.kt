package com.freqwency.promotr.view.promocodedetails.viewmodel.promoCodeSaveFav

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
class PromoCodeSaveFavViewModel @Inject constructor(application: Application,
                                                    private val promocodeSaveFavRepository: PromocodeSaveFavRepository
): AndroidViewModel(application) {
    val progressIndicator = MutableLiveData<Boolean>()
    val errorResponse = MutableLiveData<Throwable>()
    val mPromocodeSaveFavResponse = MutableLiveData<Event<PromocodeSaveFavResponse>>()
    var context: Context? = null

    fun getpromoCodeSaveFav(progressDialog: CustomProgressDialog, activity: Activity, id: Int) =
        viewModelScope.launch {
            promoCodeSaveFav(progressDialog,activity,id)
        }

    suspend fun promoCodeSaveFav(progressDialog: CustomProgressDialog, activity: Activity, id: Int) {
        progressDialog.start(activity.getString(R.string.please_wait))
        progressIndicator.value = true
        promocodeSaveFavRepository.getPromoCodeSaveFav(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<PromocodeSaveFavResponse>() {
                override fun onNext(value: PromocodeSaveFavResponse) {
                    progressIndicator.value = false
                    progressDialog.stop()
                    mPromocodeSaveFavResponse.value = Event(value)
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