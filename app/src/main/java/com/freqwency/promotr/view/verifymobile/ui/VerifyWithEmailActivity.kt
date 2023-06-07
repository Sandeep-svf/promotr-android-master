package com.freqwency.promotr.view.verifymobile.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.developer.kalert.KAlertDialog
import com.freqwency.promotr.R
import com.freqwency.promotr.application.PromotrApp
import com.freqwency.promotr.beans.VerifyOTPRequest
import com.freqwency.promotr.databinding.ActivityVerifyWithEmailBinding
import com.freqwency.promotr.utils.ErrorUtil
import com.freqwency.promotr.utils.GlobalFunctions
import com.freqwency.promotr.view.activities.BaseActivity
import com.freqwency.promotr.view.activities.MainActivity
import com.freqwency.promotr.view.verifymobile.viewmodel.verifyOTP.VerifyOtpViewModel
import com.johncodeos.customprogressdialogexample.CustomProgressDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VerifyWithEmailActivity : BaseActivity() {

    lateinit var binding: ActivityVerifyWithEmailBinding
    private lateinit var verifyOTPRequest: VerifyOTPRequest
    val verifyOtpViewModel: VerifyOtpViewModel by viewModels()
    private lateinit var activity: Activity
    var mobileNumber = ""
    private val progressDialog by lazy { CustomProgressDialog(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityVerifyWithEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        activity=this

        GlobalFunctions.addUIPadding(binding.root)
        if (intent.hasExtra("mobileNumber")) {
            mobileNumber = intent.getStringExtra("mobileNumber").toString()
        }

        binding.closeBtn.setOnClickListener {
            this.finish()
        }

        binding.submitBtn.setOnClickListener {
            if (binding.etOtp.text.isNullOrEmpty()) {
                errorDialogs(getString(R.string.please_enter_otp))
            } else {
                binding.etOtp.background = getDrawable(R.drawable.edittext)
                verifyOTP(binding.etOtp.text.toString())
            }
        }

        verifyOtpObserver()
    }

    private fun verifyOTP(otp: String) {
        verifyOTPRequest = VerifyOTPRequest(
            verifyOTP = otp.toInt(),
            mobileNumber = mobileNumber
        )
        verifyOtpViewModel.verifyOtpByNumber(verifyOTPRequest, progressDialog,activity)
    }

    private fun verifyOtpObserver() {
        verifyOtpViewModel.progressIndicator.observe(this, Observer {
        })
        verifyOtpViewModel.mVerifyOTPResponse.observe(
            this
        ) {
            PromotrApp.encryptedPrefs.isFirstTime = false
            startActivity(Intent(this@VerifyWithEmailActivity, MainActivity::class.java))
            finish()
        }

        verifyOtpViewModel.errorResponse.observe(this) {
            ErrorUtil.handlerGeneralError(this@VerifyWithEmailActivity,  it)
        }
    }

    private fun errorDialogs(str: String) {
        KAlertDialog(this, KAlertDialog.ERROR_TYPE)
            .setTitleText(getString(R.string.verify_email_error))
            .setContentText(str)
            .confirmButtonColor(R.color.secondary)
            .setConfirmClickListener(getString(R.string.OK), R.color.secondary_text_color, null)
            .show()
    }
}