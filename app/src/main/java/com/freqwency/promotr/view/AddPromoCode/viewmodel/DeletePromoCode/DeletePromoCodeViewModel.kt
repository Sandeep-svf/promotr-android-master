package com.freqwency.promotr.view.AddPromoCode.viewmodel.DeletePromoCode

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.freqwency.promotr.R
import com.freqwency.promotr.utils.Event
import com.freqwency.promotr.view.AddPromoCode.viewmodel.AddPromoCode.AddPromoCodeResponse
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
class DeletePromoCodeViewModel @Inject constructor(
    application: Application, private val deletePromoCodeRepository: DeletePromoCodeRepository
) : AndroidViewModel(application) {
    val progressIndicator = MutableLiveData<Boolean>()
    val errorResponse = MutableLiveData<Throwable>()
    val mDeletePromoCodeResponse = MutableLiveData<Event<AddPromoCodeResponse>>()
    var context: Context? = null

    fun getDeletePromoCodeForPromoter(
        progressDialog: CustomProgressDialog,
        activity: Activity,
        promoCodeId: Int
    ) =
        viewModelScope.launch {
            DeletePromoCodeForPromoter(
                progressDialog, activity,promoCodeId
            )
        }

    suspend fun DeletePromoCodeForPromoter(
        progressDialog: CustomProgressDialog, activity: Activity,promoCodeId: Int
    ) {
        progressDialog.start(activity.getString(R.string.please_wait))
        progressIndicator.value = true
        deletePromoCodeRepository.getdeletePromoCodeForPromoter(promoCodeId
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<AddPromoCodeResponse>() {
                override fun onNext(value: AddPromoCodeResponse) {
                    progressIndicator.value = false
                    progressDialog.stop()
                    mDeletePromoCodeResponse.value = Event(value)
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