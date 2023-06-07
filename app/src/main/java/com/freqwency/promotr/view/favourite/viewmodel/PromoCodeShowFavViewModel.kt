package com.freqwency.promotr.view.favourite.viewmodel

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.freqwency.promotr.R
import com.freqwency.promotr.utils.Event
import com.freqwency.promotr.view.promocodedetails.viewmodel.promoCodeSaveFav.PromocodeSaveFavRepository
import com.freqwency.promotr.view.promocodedetails.viewmodel.promoCodeSaveFav.PromocodeSaveFavResponse
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
class PromoCodeShowFavViewModel @Inject constructor(application: Application,
                                                    private val promocodeShowFavRepository: PromocodeShowFavRepository
): AndroidViewModel(application) {
    val progressIndicator = MutableLiveData<Boolean>()
    val errorResponse = MutableLiveData<Throwable>()
    val mPromocodeShowFavResponse = MutableLiveData<Event<PromocodeShowFavResponse>>()
    var context: Context? = null

    fun getpromoCodeShowFav(progressDialog: CustomProgressDialog, activity: Activity) =
        viewModelScope.launch {
            promoCodeShowFav(progressDialog,activity)
        }

    suspend fun promoCodeShowFav(progressDialog: CustomProgressDialog, activity: Activity) {
        progressDialog.start(activity.getString(R.string.please_wait))
        progressIndicator.value = true
        promocodeShowFavRepository.getpromoCodeShowFav()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<PromocodeShowFavResponse>() {
                override fun onNext(value: PromocodeShowFavResponse) {
                    progressIndicator.value = false
                    progressDialog.stop()
                    mPromocodeShowFavResponse.value = Event(value)
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