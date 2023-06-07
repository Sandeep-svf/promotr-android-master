package com.freqwency.promotr.view.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.freqwency.promotr.R
import com.freqwency.promotr.application.PromotrApp
import com.freqwency.promotr.databinding.ActivitySplashBinding
import com.freqwency.promotr.utils.ErrorUtil
import com.freqwency.promotr.utils.GlobalFunctions
import com.freqwency.promotr.view.logins.viewmodel.updateFCMToken.FcmTokenBody
import com.freqwency.promotr.view.logins.viewmodel.updateFCMToken.UpdateFCMTokenViewModel
import com.freqwency.promotr.model.GetSlidersResponse
import com.freqwency.promotr.view.sliders.ui.SlidersActivity
import com.freqwency.promotr.view.sliders.viewmodel.SlidersViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp

import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity() {
    private val slidersViewModel: SlidersViewModel by viewModels()
    private val updateFCMTokenViewModel: UpdateFCMTokenViewModel by viewModels()
    private lateinit var activity: Activity
    var slidersList: ArrayList<GetSlidersResponse.SliderBean>? = null
    lateinit var splashAnimation: Animation
    private var deviceToken = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        PromotrApp.encryptedPrefs.appLanguage = "en"
        if (PromotrApp.encryptedPrefs.deviceID.isEmpty()) {
            PromotrApp.encryptedPrefs.deviceID = GlobalFunctions.getDeviceID()
        }

        val splashBinding: ActivitySplashBinding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(splashBinding.root)

        activity = this

        splashAnimation = AnimationUtils.loadAnimation(this, R.anim.anim_splash)
        splashBinding.ivLogo.animation = splashAnimation

        FirebaseApp.getApps(activity)
        init()

        splashAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                if (PromotrApp.encryptedPrefs.isFirstTime) {
                    // get Sliders
                    getSliders()
                    splashAnimation.repeatMode = 1
                } else {
                    val userId = PromotrApp.encryptedPrefs.userId
                    if (PromotrApp.encryptedPrefs.isNotification && !userId.equals("")) {
                        getUpdateFCMToken()
                    } else {
                        Handler(Looper.getMainLooper()).postDelayed({
                            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                            finish()
                        }, 1000)
                    }
                }
            }

            override fun onAnimationRepeat(animation: Animation?) {
            }
        })

        observer()
    }

    private fun observer() {
        slidersViewModel.progressIndicator.observe(this, Observer {
        })
        slidersViewModel.getSliderResponse.observe(
            this
        ) {
            slidersList = it.peekContent().data?.appSliders
            val intent = Intent(this@SplashActivity, SlidersActivity::class.java)
            intent.putExtra("sliders", slidersList)
            startActivity(intent)
            finish()
        }

        slidersViewModel.errorResponse.observe(this) {
            ErrorUtil.handlerGeneralError(this@SplashActivity, it)
        }
    }

    fun getSliders() {
        slidersViewModel.slider()
    }

    /**
     * Update FCM Token
     */
    private fun getUpdateFCMToken() {
        val fcmTokenBody = FcmTokenBody(
            fcm_token = deviceToken
        )
        updateFCMTokenViewModel.getUpdateFCMToken(fcmTokenBody)
    }

    private fun init() {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("tokens", "FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }
                // Get new FCM registration token
                deviceToken = task.result
                Log.d("tokens", "msg  $deviceToken")
            })
    }
}