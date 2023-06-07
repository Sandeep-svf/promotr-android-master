package com.freqwency.promotr.view.promoterprofile.viewmodel.promoterListById

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
class PromotersListByIdViewModel @Inject constructor(
    application: Application,
    private val promotersListByIdRepository: PromotersListByIdRepository
) : AndroidViewModel(application){
    val progressIndicator = MutableLiveData<Boolean>()
    val errorResponse = MutableLiveData<Throwable>()
    val mPromoterListByIdResponse = MutableLiveData<Event<PromoterListByIdResponse>>()
    var context: Context? = null

    fun getPromotersListById(progressDialog: CustomProgressDialog, activity: Activity, id: Int) =
        viewModelScope.launch {
            PromotersListById(progressDialog,activity,id)
        }

    suspend fun PromotersListById(progressDialog: CustomProgressDialog, activity: Activity, id: Int) {
        progressIndicator.value = true
        progressDialog.start(activity.getString(R.string.please_wait))
        promotersListByIdRepository.getPromoterListById(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<PromoterListByIdResponse>() {
                override fun onNext(value: PromoterListByIdResponse) {
                    progressIndicator.value = false
                    progressDialog.stop()
                    mPromoterListByIdResponse.value = Event(value)
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