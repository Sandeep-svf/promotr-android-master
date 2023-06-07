package com.freqwency.promotr.view.becomePromoter.becomePromoterProfile
import com.freqwency.promotr.application.PromotrApp
import com.freqwency.promotr.services.ApiService
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class PromoterProfileRepository @Inject constructor(private val apiService: ApiService) {
    suspend fun getBecomePromoterProfile(image: MultipartBody.Part, about: RequestBody, instaUrl: RequestBody,
                                         facebookUrl: RequestBody, nickname: RequestBody
    ): Observable<PromoterProfileResponse> {
        return apiService.becomePromoterProfile(PromotrApp.encryptedPrefs.bearerToken,image,about,instaUrl,facebookUrl, nickname)
    }
}