package com.freqwency.promotr.services

import com.freqwency.promotr.application.PromotrApp
import com.freqwency.promotr.beans.VerifyOTPRequest
import com.freqwency.promotr.view.AddPromoCode.viewmodel.AddPromoCode.AddPromoCodeResponse
import com.freqwency.promotr.view.dashboard.viewmodel.category.CategoryResponse
import com.freqwency.promotr.view.dashboard.viewmodel.promocode.PromocodeResponse
import com.freqwency.promotr.view.dashboard.viewmodel.promotersList.PromotersListResponse
import com.freqwency.promotr.view.favourite.viewmodel.PromocodeShowFavResponse
import com.freqwency.promotr.view.forgotpassword.viewmodel.ForgotBody
import com.freqwency.promotr.view.forgotpassword.viewmodel.ForgotResponse
import com.freqwency.promotr.view.logins.viewmodel.login.LoginBody
import com.freqwency.promotr.view.logins.viewmodel.logout.LogoutResponse
import com.freqwency.promotr.view.logins.viewmodel.RegistrationResponse
import com.freqwency.promotr.view.promocodedetails.viewmodel.promoCodeDetails.PromocodeDetailsResponse
import com.freqwency.promotr.view.promocodedetails.viewmodel.promoCodeSaveFav.PromocodeSaveFavResponse
import com.freqwency.promotr.view.becomePromoter.becomePromoterProfile.PromoterProfileResponse
import com.freqwency.promotr.view.logins.viewmodel.updateFCMToken.FcmTokenBody
import com.freqwency.promotr.view.notification.viewmodel.deletenotification.DeleteNotificationBody
import com.freqwency.promotr.view.notification.viewmodel.deletenotification.DeleteNotificationResponse
import com.freqwency.promotr.view.notification.viewmodel.notificationlist.NotificationListResponse
import com.freqwency.promotr.view.notification.viewmodel.updatenotification.UpdateNotificationBody
import com.freqwency.promotr.view.promoterprofile.viewmodel.promoterListById.PromoterListByIdResponse
import com.freqwency.promotr.view.promoterprofile.viewmodel.promoterSaveFav.PromoterBody
import com.freqwency.promotr.view.promoterprofile.viewmodel.promoterSaveFav.PromoterSaveFavResponse
import com.freqwency.promotr.view.promoterprofile.viewmodel.promoterShowFav.PromoterShowFavResponse
import com.freqwency.promotr.view.register.viewmodel.RegistrationBody
import com.freqwency.promotr.view.search.viewmodel.promocodecategory.PromocodeByCategoryResponse
import com.freqwency.promotr.model.GetSlidersResponse
import com.freqwency.promotr.view.userprofile.viewmodel.UserProfileResponse
import com.freqwency.promotr.view.verifymobile.viewmodel.regquestOTP.GetsOTPResponse
import com.freqwency.promotr.view.verifymobile.viewmodel.regquestOTP.RequestOTPRequest
import com.freqwency.promotr.view.verifymobile.viewmodel.verifyOTP.VerifyOTPResponse
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*


interface ApiService {

    //login
    @Headers("Content-Type:application/json")
    @POST("auth/login")
    fun login(
        @Query("locale") locale: String = PromotrApp.encryptedPrefs.appLanguage,
        @Body body: LoginBody
    ): Observable<RegistrationResponse>

    //Register
    //  @Headers("Content-Type:application/json")
    @Headers("Accept:application/json")
    @POST("auth/register")
    fun register(
        @Query("locale") locale: String = PromotrApp.encryptedPrefs.appLanguage,
        @Body body: RegistrationBody
    ): Observable<RegistrationResponse>

    //GetOTP
    @Headers("Content-Type:application/json")
    @POST("auth/otps")
    fun getOTP(
        @Query("locale") locale: String = PromotrApp.encryptedPrefs.appLanguage,
        @Body body: RequestOTPRequest
    ): Observable<GetsOTPResponse>

    //VerifyOTP
    @Headers("Content-Type:application/json")
    @POST("auth/otp-verifications")
    fun verifyOTP(
        @Query("locale") locale: String = PromotrApp.encryptedPrefs.appLanguage,
        @Body body: VerifyOTPRequest
    ): Observable<VerifyOTPResponse>

    //ForgotPass
    @Headers("Content-Type:application/json")
    @POST("auth/forgot-password")
    fun ForgotPassword(
        @Query("locale") locale: String = PromotrApp.encryptedPrefs.appLanguage,
        @Body body: ForgotBody
    ): Observable<ForgotResponse>

    //GetSliders
    @GET("public/app-sliders")
    fun getSliders(): Observable<GetSlidersResponse>

    //category List
    @GET("categories")
    fun categories(): Observable<CategoryResponse>

    //PromoCode List
    @GET("promo-codes/of-the-day")
    fun promoCodes(): Observable<PromocodeResponse>

    //Promocodes by Category
    @GET("categories/{categoryID}/promo-codes")
    fun promoCodesByCategory(@Path("categoryID") id: Int): Observable<PromocodeByCategoryResponse>

    //Promocodes Filter
    @GET("promo-codes")
    fun promoCodesWithDates(
        @Query("discount_amount") discount_amount: Double?,
        @Query("category_id") category_id: Int?,
        @Query("expiry_date") expiry_date: String?
    ): Observable<PromocodeResponse>


    //Promocodes Details
    @GET("promo-codes/{id}")
    fun promoCodesDettails(@Path("id") id: Int
    ): Observable<PromocodeDetailsResponse>

    //Promocodes With Protected Details
    @GET("protected/promo-codes/{id}")
    fun promoCodesDettailsProtected(
        @Header("Authorization") token: String = PromotrApp.encryptedPrefs.bearerToken,
        @Path("id") id: Int
    ): Observable<PromocodeDetailsResponse>


    //Promocodes Save Favourite
    @Headers("Accept:application/json")
    @POST("protected/user-promo-codes")
    fun promoCodesSaveFav(
        @Header("Authorization") token: String = PromotrApp.encryptedPrefs.bearerToken,
        @Query("promocode_id") promocode_id: Int
    ): Observable<PromocodeSaveFavResponse>

    //Promocodes Show Favourite List
    @GET("protected/user-promo-codes")
    fun promoCodeShowFav(
        @Header("Authorization") token: String = PromotrApp.encryptedPrefs.bearerToken
    ): Observable<PromocodeShowFavResponse>

    //Delete Favourite PromoCode
    @DELETE("protected/user-promo-codes/{id}")
    fun deleteFavouritePromoCode(
        @Header("Authorization") token: String = PromotrApp.encryptedPrefs.bearerToken,
        @Path("id") id: Int
    ): Observable<LogoutResponse>

    //User Profile
    @GET("protected/user-profile")
    fun userProfile(
        @Header("Authorization") token: String = PromotrApp.encryptedPrefs.bearerToken
    ): Observable<UserProfileResponse>

    //Update User Profile
    @PUT("protected/user-profile")
    fun updateUserProfile(
        @Header("Authorization") token: String = PromotrApp.encryptedPrefs.bearerToken,
        @Body requestBody: RequestBody
    ): Observable<UserProfileResponse>


    //Delete User Profile
    @DELETE("protected/user-profile")
    fun deleteUserProfile(
        @Header("Authorization") token: String = PromotrApp.encryptedPrefs.bearerToken
    ): Observable<LogoutResponse>

    //Promoter Profile
    @Multipart
    @Headers("Accept:application/json")
    @POST("protected/user-profile/become-a-promoter")
    fun becomePromoterProfile(
        @Header("Authorization") token: String = PromotrApp.encryptedPrefs.bearerToken,
        @Part Attachment: MultipartBody.Part,
        @Part("about") About: RequestBody,
        @Part("instagram_profile_url") Instagram_url: RequestBody,
        @Part("facebook_profile_url") Facebook_url: RequestBody,
        @Part("nickname") nickname: RequestBody
    ): Observable<PromoterProfileResponse>

    //add PromoCode For Promoter
    @Multipart
    @Headers("Accept:application/json")
    @POST("protected/promoter/promo-codes")
    fun addPromoCodeForPromoter(
        @Header("Authorization") token: String = PromotrApp.encryptedPrefs.bearerToken,
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody,
        @Part("code_id") code_id: RequestBody,
        @Part("store_name") store_name: RequestBody,
        @Part("store_website_url") store_website_url: RequestBody,
        @Part("discount_amount") discount_amount: RequestBody,
        @Part("expiry_date") expiry_date: RequestBody,
        @Part("type") type: RequestBody,
        @Part("category_id") category_id: RequestBody,
        @Part Attachment: MultipartBody.Part,
    ): Observable<AddPromoCodeResponse>

    //List PromoCode For Promoter
    @GET("protected/promoter/promo-codes")
    fun getPromoCodeForPromoter(
        @Header("Authorization") token: String = PromotrApp.encryptedPrefs.bearerToken
    ): Observable<PromocodeResponse>

    //Edit PromoCode For Promoter
    @Headers("Accept:application/json")
    @POST("protected/promoter/promo-codes/{id}")
    fun editPromoCodeForPromoter(
        @Header("Authorization") token: String = PromotrApp.encryptedPrefs.bearerToken,
        @Path("id") id: Int,
        @Query("title") title: String,
        @Query("description") description: String,
        @Query("code_id") code_id: String,
        @Query("store_name") store_name: String,
        @Query("store_website_url") store_website_url: String,
        @Query("discount_amount") discount_amount: String,
        @Query("expiry_date") expiry_date: String,
        @Query("type") type: String,
        @Query("category_id") category_id: Int,
        @Query("_method") method: String,
    ): Observable<AddPromoCodeResponse>

    //Delete PromoCode For Promoter
    @DELETE("protected/promoter/promo-codes/{id}")
    fun deletePromoCodeForPromoter(
        @Header("Authorization") token: String = PromotrApp.encryptedPrefs.bearerToken,
        @Path("id") id: Int
    ): Observable<AddPromoCodeResponse>

    //logout
    @DELETE("protected/logout")
    fun logout(@Header("Authorization") token: String = PromotrApp.encryptedPrefs.bearerToken): Observable<LogoutResponse>

    //Promoters List
    @GET("promoters")
    fun getPromoterList(): Observable<PromotersListResponse>

    //Promoters List By Id
    @GET("promoters/{id}")
    fun getPromoterListById(@Path("id") id: Int
    ): Observable<PromoterListByIdResponse>

    //Promoters With Protected List By Id
    @GET("protected/promoters/{id}")
    fun getPromoterListByIdProtected(
        @Header("Authorization") token: String = PromotrApp.encryptedPrefs.bearerToken,
        @Path("id") id: Int
    ): Observable<PromoterListByIdResponse>

    //Promoter Save Favourite
    @POST("protected/user-favorites")
    fun promoterSaveFav(
        @Header("Authorization") token: String = PromotrApp.encryptedPrefs.bearerToken,
        @Body body: PromoterBody
    ): Observable<PromoterSaveFavResponse>

    //Promoter Show Favourite List
    @GET("protected/user-favorites")
    fun promoterShowFav(
        @Header("Authorization") token: String = PromotrApp.encryptedPrefs.bearerToken
    ): Observable<PromoterShowFavResponse>

    //Delete Favourite Promoter
    @DELETE("protected/user-favorites/{id}")
    fun deleteFavouritePromoter(
        @Header("Authorization") token: String = PromotrApp.encryptedPrefs.bearerToken,
        @Path("id") id: Int
    ): Observable<LogoutResponse>

    //Update FCM Token
    @Headers("Accept:application/json")
    @PUT("protected/notifications/token")
    fun updateFCMToken(
        @Header("Authorization") token: String = PromotrApp.encryptedPrefs.bearerToken,
        @Body body: FcmTokenBody
    ): Observable<LogoutResponse>

    //Notification List
    @GET("protected/notifications")
    fun notificationList(
        @Header("Authorization") token: String = PromotrApp.encryptedPrefs.bearerToken
    ): Observable<NotificationListResponse>


    //Delete Notification
    @HTTP(method = "DELETE", path = "protected/notifications", hasBody = true)
    fun DeleteNotification(
        @Header("Authorization") token: String = PromotrApp.encryptedPrefs.bearerToken,
        @Body deleteNotificationBody: DeleteNotificationBody
    ): Observable<DeleteNotificationResponse>


    //Update User Notification
    @PUT("protected/notifications")
    fun updateUserNotification(
        @Header("Authorization") token: String = PromotrApp.encryptedPrefs.bearerToken,
        @Body requestBody: UpdateNotificationBody
    ): Observable<DeleteNotificationResponse>
}