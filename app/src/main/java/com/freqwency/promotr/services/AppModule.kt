package com.freqwency.promotr.services

import com.freqwency.promotr.BuildConfig
import com.freqwency.promotr.view.dashboard.viewmodel.category.CategoryRepository
import com.freqwency.promotr.view.dashboard.viewmodel.promocode.PromocodeRepository
import com.freqwency.promotr.view.favourite.viewmodel.PromocodeShowFavRepository
import com.freqwency.promotr.view.forgotpassword.viewmodel.ForgotRepository
import com.freqwency.promotr.view.logins.viewmodel.login.LoginRepository
import com.freqwency.promotr.view.promocodedetails.viewmodel.promoCodeDetails.PromocodeDetailsRepository
import com.freqwency.promotr.view.promocodedetails.viewmodel.promoCodeSaveFav.PromocodeSaveFavRepository
import com.freqwency.promotr.view.register.viewmodel.RegisterRepository
import com.freqwency.promotr.view.search.viewmodel.promocodecategory.PromocodeByCategoryRepository
import com.freqwency.promotr.view.search.viewmodel.promocodedates.PromocodeByDatesRepository
import com.freqwency.promotr.view.userprofile.viewmodel.userprofile.UserProfileRepository
import com.freqwency.promotr.view.verifymobile.viewmodel.regquestOTP.GetsOtpRepository
import com.freqwency.promotr.view.verifymobile.viewmodel.verifyOTP.VerifyOtpRepository
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    @Provides
    fun providesBaseUrl(): String {
        return BuildConfig.API_KEY
    }

    @Provides
    fun providesHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    @Provides
    fun providesOkhttpClient(interceptor: HttpLoggingInterceptor): OkHttpClient {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .callTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
        return okHttpClient.build()
    }

    @Provides
    fun providesConverterFactory(): Converter.Factory {
        return GsonConverterFactory.create()
    }

    var gson: Gson = GsonBuilder()
        .setLenient()
        .create()

    @Provides
    fun providesRetrofit(
        client: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            //.baseUrl(baseUrl)
            .baseUrl(BuildConfig.API_KEY)
            //.addConverterFactory(converterFactory)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(client)
            .build()
    }

    @Provides
    fun providesRetrofitService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    //sliders
    @Singleton
    @Provides
    fun providesSlidersRepository(
        apiService: ApiService
    ): SlidersRepository {
        return SlidersRepository(apiService)
    }

    //login
    @ExperimentalCoroutinesApi
    @Provides
    fun providesloginRepository(
        apiService: ApiService
    ): LoginRepository {
        return LoginRepository(apiService)
    }

    //register
    @ExperimentalCoroutinesApi
    @Provides
    fun providesregisterRepository(
        apiService: ApiService
    ): RegisterRepository {
        return RegisterRepository(apiService)
    }

    //getOTP
    @ExperimentalCoroutinesApi
    @Provides
    fun providesgetOtpRepository(
        apiService: ApiService
    ): GetsOtpRepository {
        return GetsOtpRepository(apiService)
    }

    //verifyOTP
    @ExperimentalCoroutinesApi
    @Provides
    fun providesVerifyOtpRepository(
        apiService: ApiService
    ): VerifyOtpRepository {
        return VerifyOtpRepository(apiService)
    }

    //forgot Password
    @ExperimentalCoroutinesApi
    @Provides
    fun providesForgotPassRepository(
        apiService: ApiService
    ): ForgotRepository {
        return ForgotRepository(apiService)
    }

    //category List
    @Singleton
    @Provides
    fun providesCategoryRepository(
        apiService: ApiService
    ): CategoryRepository {
        return CategoryRepository(apiService)
    }

    //promoCode List
    @Singleton
    @Provides
    fun providesPromoCodeRepository(
        apiService: ApiService
    ): PromocodeRepository {
        return PromocodeRepository(apiService)
    }

    //Promocodes by Category
    @Singleton
    @Provides
    fun providesPromoCodeByCategoryRepository(
        apiService: ApiService
    ): PromocodeByCategoryRepository {
        return PromocodeByCategoryRepository(apiService)
    }

    //Promocodes Filter
    @Singleton
    @Provides
    fun providesPromoCodeByDatesRepository(
        apiService: ApiService
    ): PromocodeByDatesRepository {
        return PromocodeByDatesRepository(apiService)
    }

    //promoCode Details
    @Singleton
    @Provides
    fun providesPromoCodeDetailRepository(
        apiService: ApiService
    ): PromocodeDetailsRepository {
        return PromocodeDetailsRepository(apiService)
    }

    //Promocodes Save Favourite
    @ExperimentalCoroutinesApi
    @Provides
    fun providesSaveFavRepository(
        apiService: ApiService
    ): PromocodeSaveFavRepository {
        return PromocodeSaveFavRepository(apiService)
    }

    //Promocodes Show Favourite List
    @Singleton
    @Provides
    fun providesShowFavRepository(
        apiService: ApiService
    ): PromocodeShowFavRepository {
        return PromocodeShowFavRepository(apiService)
    }

    //User Profile
    @Singleton
    @Provides
    fun providesUSerProfileRepository(
        apiService: ApiService
    ): UserProfileRepository {
        return UserProfileRepository(apiService)
    }

}