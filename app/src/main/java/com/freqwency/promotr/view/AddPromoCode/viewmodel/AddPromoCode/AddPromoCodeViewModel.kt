package com.freqwency.promotr.view.AddPromoCode.viewmodel.AddPromoCode

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
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class AddPromoCodeViewModel @Inject constructor(application: Application, private val addPromoCodeRepository: AddPromoCodeRepository
) : AndroidViewModel(application) {
    val progressIndicator = MutableLiveData<Boolean>()
    val errorResponse = MutableLiveData<Throwable>()
    val mAddPromoCodeResponse = MutableLiveData<Event<AddPromoCodeResponse>>()
    var context: Context? = null

    fun getAddPromoCodeForPromoter(
        progressDialog: CustomProgressDialog,
        activity: Activity,
        categoryId: RequestBody,
        title: RequestBody,
        type: RequestBody,
        discount: RequestBody,
        storeName: RequestBody,
        storeLink: RequestBody,
        codeId: RequestBody,
        dateOfBirth: RequestBody,
        description: RequestBody,
        images: MultipartBody.Part
    ) =
        viewModelScope.launch {
            AddPromoCodeForPromoter(progressDialog, activity,categoryId,title,type,discount,storeName,storeLink,codeId,dateOfBirth,description,images)
        }

    suspend fun AddPromoCodeForPromoter(progressDialog: CustomProgressDialog, activity: Activity,
                                        categoryId: RequestBody,
                                        title: RequestBody,
                                        type: RequestBody,
                                        discount: RequestBody,
                                        storeName: RequestBody,
                                        storeLink: RequestBody,
                                        codeId: RequestBody,
                                        dateOfBirth: RequestBody,
                                        description: RequestBody,
                                        images: MultipartBody.Part) {
        progressDialog.start(activity.getString(R.string.please_wait))
        progressIndicator.value = true
        addPromoCodeRepository.getAddPromoCodeForPromoter(categoryId,title,type,discount,storeName,storeLink,codeId,dateOfBirth,description,images)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<AddPromoCodeResponse>() {
                override fun onNext(value: AddPromoCodeResponse) {
                    progressIndicator.value = false
                    progressDialog.stop()
                    mAddPromoCodeResponse.value = Event(value)
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