package com.freqwency.promotr.view.verifymobile.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.developer.kalert.KAlertDialog
import com.freqwency.promotr.R
import com.freqwency.promotr.application.PromotrApp
import com.freqwency.promotr.beans.VerifyOTPRequest
import com.freqwency.promotr.databinding.ActivityVerifyMobileBinding
import com.freqwency.promotr.utils.ErrorUtil
import com.freqwency.promotr.utils.GlobalFunctions
import com.freqwency.promotr.view.activities.BaseActivity
import com.freqwency.promotr.view.activities.MainActivity
import com.freqwency.promotr.view.logins.viewmodel.updateFCMToken.UpdateFCMTokenViewModel
import com.freqwency.promotr.view.verifymobile.viewmodel.regquestOTP.GetsOtpViewModel
import com.freqwency.promotr.view.verifymobile.viewmodel.regquestOTP.RequestOTPRequest
import com.freqwency.promotr.view.verifymobile.viewmodel.verifyOTP.VerifyOtpViewModel
import com.johncodeos.customprogressdialogexample.CustomProgressDialog
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class VerifyMobileActivity : BaseActivity() {
    val updateFCMTokenViewModel: UpdateFCMTokenViewModel by viewModels()
    lateinit var binding: ActivityVerifyMobileBinding
    private lateinit var requestOTPRequest: RequestOTPRequest
    private lateinit var verifyOTPRequest: VerifyOTPRequest
    val getsOtpViewModel: GetsOtpViewModel by viewModels()
    val verifyOtpViewModel: VerifyOtpViewModel by viewModels()
    private lateinit var activity: Activity
    private val progressDialog by lazy { CustomProgressDialog(this) }
    var mobileNumber = ""
    var device_token = ""
    var bearerToken = ""
    var resendOTP: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerifyMobileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        activity = this

        GlobalFunctions.addUIPadding(binding.root)
        if (intent.hasExtra("mobileNumber")) {
            mobileNumber = intent.getStringExtra("mobileNumber").toString()
            device_token = intent.getStringExtra("device_token").toString()
            resendOTP = intent.getBooleanExtra("resendOTP", false)
        }

        bearerToken = PromotrApp.encryptedPrefs.bearerToken
        Log.e("tokens", "  OTP  "+bearerToken+"  "+device_token)
        if (resendOTP) {
            Log.e("tokens", "  OTP  ")
            getOTP()
        }

        binding.llBackBtn.setOnClickListener {
            this.finish()
        }

        binding.btnVerify.setOnClickListener {
            if (binding.etOtp.text.isNullOrEmpty()) {
                errorDialogs(getString(R.string.please_enter_mobile))
            } else {
                binding.etOtp.background = getDrawable(R.drawable.edittext)
                verifyOTP(binding.etOtp.text.toString())
            }
        }

        binding.tvResendOTP.setOnClickListener {
            getOTP()
        }
        binding.tvResendOTP.isEnabled = false
        startCounter()
        verifyOtpObserver()
        getOtpObserver()
       // updateFCMTokenObserver()

    }

    private fun startCounter() {
        //12 * 10000
        val countDownTimer = object : CountDownTimer( 120*1000, 1000) {
            // override object functions here, do it quicker by setting cursor on object, then type alt + enter ; implement members
            override fun onTick(p0: Long) {
                //binding.tvCounter.text = "0:${(p0 / 1000)}"
                val texts = java.lang.String.format(
                    Locale.getDefault(), "%d : %02d",
                    TimeUnit.MILLISECONDS.toMinutes(p0) % 60,
                    TimeUnit.MILLISECONDS.toSeconds(p0) % 60
                )
                binding.tvCounter.text = texts
            }

            override fun onFinish() {
                binding.tvResendOTP.isEnabled = true
            }
        }
        countDownTimer.start()
    }

    private fun getOTP() {

        requestOTPRequest = RequestOTPRequest(
            mobileNumber = mobileNumber
        )
        getsOtpViewModel.otpByNumber(requestOTPRequest, progressDialog, activity)
    }
    private fun getOtpObserver() {
        getsOtpViewModel.progressIndicator.observe(this, Observer {
        })
        getsOtpViewModel.mGetOTPResponse.observe(
            this
        ) {
            successDialogs(it.peekContent().data)
        }

        getsOtpViewModel.errorResponse.observe(this) {
        }
    }


    private fun verifyOTP(otp: String) {
        verifyOTPRequest = VerifyOTPRequest(
            verifyOTP = otp.toInt(),
            mobileNumber = mobileNumber
        )
        verifyOtpViewModel.verifyOtpByNumber(verifyOTPRequest, progressDialog, activity)
    }
    private fun verifyOtpObserver() {
        verifyOtpViewModel.progressIndicator.observe(this, Observer {
        })
        verifyOtpViewModel.mVerifyOTPResponse.observe(
            this
        ) {
            //getUpdateFCMToken()
            PromotrApp.encryptedPrefs.isFirstTime = false
            PromotrApp.encryptedPrefs.isNotification = false
            startActivity(Intent(this@VerifyMobileActivity, MainActivity::class.java))
            finish()
        }

        verifyOtpViewModel.errorResponse.observe(this) {
            ErrorUtil.handlerGeneralError(this@VerifyMobileActivity, it)
        }
    }

    private fun successDialogs(str: String) {
        KAlertDialog(this, KAlertDialog.SUCCESS_TYPE)
            .setContentText(str)
            .confirmButtonColor(R.color.secondary)
            .setConfirmClickListener(getString(R.string.OK), R.color.secondary_text_color, null)
            .show()
    }

    private fun errorDialogs(str: String) {
        KAlertDialog(this, KAlertDialog.ERROR_TYPE)
            .setTitleText(getString(R.string.verify_mobile_error))
            .setContentText(str)
            .confirmButtonColor(R.color.secondary)
            .setConfirmClickListener(getString(R.string.OK), R.color.secondary_text_color, null)
            .show()
    }


    /**
     * Update FCM Token
     */
   /* private fun getUpdateFCMToken() {

        val fcmTokenBody = FcmTokenBody(
            fcm_token = device_token
        )
        updateFCMTokenViewModel.getUpdateFCMToken(fcmTokenBody)
    }

    private fun updateFCMTokenObserver() {

        updateFCMTokenViewModel.progressIndicator.observe(this, Observer {
        })
        updateFCMTokenViewModel.mUpdateFCMTokenResponse.observe(
            this
        ) {
            PromotrApp.encryptedPrefs.isFirstTime = false
            PromotrApp.encryptedPrefs.isNotification = false
            startActivity(Intent(this@VerifyMobileActivity, MainActivity::class.java))
            finish()
            Log.e("tokens", "  Token Check  ")
        }

        updateFCMTokenViewModel.errorResponse.observe(this) {
            ErrorUtil.handlerGeneralError(this@VerifyMobileActivity, it)
        }
    }*/
}