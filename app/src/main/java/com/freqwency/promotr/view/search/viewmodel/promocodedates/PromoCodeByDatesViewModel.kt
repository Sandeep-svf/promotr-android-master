package com.freqwency.promotr.view.search.viewmodel.promocodedates

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.freqwency.promotr.R
import com.freqwency.promotr.utils.Event
import com.freqwency.promotr.view.dashboard.viewmodel.promocode.PromocodeResponse
import com.freqwency.promotr.view.search.viewmodel.promocodecategory.PromocodeByCategoryResponse
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
class PromoCodeByDatesViewModel @Inject constructor(application: Application,
                                                    private val promocodeByDatesRepository: PromocodeByDatesRepository
): AndroidViewModel(application) {
    val progressIndicator = MutableLiveData<Boolean>()
    val errorResponse = MutableLiveData<Throwable>()
    val mPromocodeByDatesResponse = MutableLiveData<Event<PromocodeResponse>>()
    var context: Context? = null

    fun getpromoCodeByDates(
        progressDialog: CustomProgressDialog,
        activity: Activity,
        i: Int?,
        dateOfBirth: String?,
        d: Double?
    ) =
        viewModelScope.launch {
            promoCodeByDates(progressDialog,activity,i,dateOfBirth,d)
        }

    suspend fun promoCodeByDates(progressDialog: CustomProgressDialog, activity: Activity, id: Int?, dateOfBirth: String?, dou: Double?) {
        progressDialog.start(activity.getString(R.string.please_wait))
        progressIndicator.value = true
        promocodeByDatesRepository.getpromoCodesWithDates(dou,id,dateOfBirth)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<PromocodeResponse>() {
                override fun onNext(value: PromocodeResponse) {
                    progressIndicator.value = false
                    progressDialog.stop()
                    mPromocodeByDatesResponse.value = Event(value)
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