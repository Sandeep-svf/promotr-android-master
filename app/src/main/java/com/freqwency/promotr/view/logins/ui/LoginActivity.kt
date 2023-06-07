package com.freqwency.promotr.view.logins.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.developer.kalert.KAlertDialog
import com.freqwency.promotr.R
import com.freqwency.promotr.application.PromotrApp
import com.freqwency.promotr.databinding.ActivityLoginBinding
import com.freqwency.promotr.utils.ErrorUtil
import com.freqwency.promotr.utils.GlobalFunctions
import com.freqwency.promotr.view.activities.BaseActivity
import com.freqwency.promotr.view.activities.MainActivity
import com.freqwency.promotr.view.forgotpassword.ui.ResetPasswordActivity
import com.freqwency.promotr.view.logins.viewmodel.login.LoginBody
import com.freqwency.promotr.view.logins.viewmodel.login.LoginViewModel
import com.freqwency.promotr.view.logins.viewmodel.updateFCMToken.FcmTokenBody
import com.freqwency.promotr.view.logins.viewmodel.updateFCMToken.UpdateFCMTokenViewModel
import com.freqwency.promotr.view.verifymobile.ui.VerifyMobileActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.johncodeos.customprogressdialogexample.CustomProgressDialog
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LoginActivity : BaseActivity(), View.OnClickListener {

    private lateinit var activity: Activity
    private lateinit var loginBinding: ActivityLoginBinding
    val viewModel: LoginViewModel by viewModels()
    val updateFCMTokenViewModel: UpdateFCMTokenViewModel by viewModels()
    var device_token = ""
    var mobile_number = ""
    var fcmToken: String? = null
    var mobileNumberVerified: Boolean = false

    private val progressDialog by lazy { CustomProgressDialog(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(loginBinding.root)

        activity = this
        GlobalFunctions.addUIPadding(loginBinding.rlMainView)
        loginBinding.btnSignIn.setOnClickListener(this)
        loginBinding.tvResetPass.setOnClickListener(this)

        with(loginBinding) {
            llBackBtn.setOnClickListener {
                finish()
            }
        }




        FirebaseApp.getApps(activity)
        init()
        loginObserver()
        updateFCMTokenObserver()
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_signIn -> {
                getLogin()
            }
            R.id.tv_resetPass -> {
                val intent = Intent(activity, ResetPasswordActivity::class.java)
                if (loginBinding.etEmail.text.isNotEmpty()) {
                    intent.putExtra("email", loginBinding.etEmail.text.toString())
                }
                activity.startActivity(intent)
            }
        }
    }

    /**
     * login
     */
    private fun getLogin() {

        if (loginBinding.etEmail.text.isNullOrEmpty()) {
            errorDialogs(getString(R.string.please_enter_email))
        } else if (loginBinding.etPassword.text.isNullOrEmpty()) {
            errorDialogs(getString(R.string.please_enter_password))
        } else {
            val loginBody = LoginBody(
                email = loginBinding.etEmail.text.toString(),
                password = loginBinding.etPassword.text.toString(),
                deviceName = PromotrApp.encryptedPrefs.deviceID
            )
            viewModel.loginUser(loginBody, progressDialog, activity)

        }
    }

    private fun loginObserver() {

        viewModel.progressIndicator.observe(this, Observer {
        })
        viewModel.mLoginResponse.observe(
            this
        ) {
            PromotrApp.encryptedPrefs.token = it.peekContent().data?.accessToken ?: ""
            PromotrApp.encryptedPrefs.bearerToken = "Bearer " + (it.peekContent().data?.accessToken ?: "")
            PromotrApp.encryptedPrefs.user = it.peekContent().data?.user
            PromotrApp.encryptedPrefs.FCMToken = it.peekContent().data?.user?.fcm_token.toString()
            PromotrApp.encryptedPrefs.promoterId = "" + (it.peekContent().data?.user!!.promoter_info?.PromoterId ?: "")
            PromotrApp.encryptedPrefs.userId = it.peekContent().data?.user!!.id.toString()
            // PromotrApp.encryptedPrefs.isFirstTime = false
            PromotrApp.encryptedPrefs.isFirstTime = false
            mobileNumberVerified = it.peekContent().data?.user?.mobileNumberVerified!!
            mobile_number = it.peekContent().data?.user?.mobileNumber!!
            fcmToken = it.peekContent().data?.user?.fcm_token

            Log.e("tokens", "  Token Checkss  "+fcmToken)

            if (fcmToken.equals(null)) {
                PromotrApp.encryptedPrefs.isNotification = false
                if (it.peekContent().data?.user?.mobileNumberVerified == true) {
                    val intent = Intent(activity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    activity.startActivity(intent)
                    finish()
                } else {
                    val intent = Intent(this@LoginActivity, VerifyMobileActivity::class.java)
                    intent.putExtra("resendOTP", true)
                    intent.putExtra("mobileNumber", it.peekContent().data?.user?.mobileNumber ?: "")
                    intent.putExtra("device_token", device_token)
                    startActivity(intent)
                    finish()
                }
            } else {
                getUpdateFCMToken()
            }
        }

        viewModel.errorResponse.observe(this) {
            ErrorUtil.handlerGeneralError(this@LoginActivity, it)
        }
    }


    /**
     * Update FCM Token
     */
    private fun getUpdateFCMToken() {

        val fcmTokenBody = FcmTokenBody(
            fcm_token = device_token
        )
        Log.e("Tokens", "   Id: " + fcmTokenBody)
        updateFCMTokenViewModel.getUpdateFCMToken(fcmTokenBody)
    }

    private fun updateFCMTokenObserver() {

        updateFCMTokenViewModel.progressIndicator.observe(this, Observer {
        })
        updateFCMTokenViewModel.mUpdateFCMTokenResponse.observe(
            this
        ) {
            PromotrApp.encryptedPrefs.isNotification = true
            if (mobileNumberVerified == true) {
                val intent = Intent(activity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                activity.startActivity(intent)
                finish()
            } else {
                val intent = Intent(this@LoginActivity, VerifyMobileActivity::class.java)
                intent.putExtra("resendOTP", true)
                intent.putExtra("mobileNumber", mobile_number)
                intent.putExtra("device_token", device_token)
                startActivity(intent)
                finish()
            }
            Log.e("tokens", "  Token Check  ")
        }

        updateFCMTokenViewModel.errorResponse.observe(this) {
            ErrorUtil.handlerGeneralError(this@LoginActivity, it)
        }
    }

    private fun errorDialogs(str: String) {
        KAlertDialog(this, KAlertDialog.ERROR_TYPE)
            .setTitleText(getString(R.string.login_error))
            .setContentText(str)
            .confirmButtonColor(R.color.secondary)
            .setConfirmClickListener(getString(R.string.OK), R.color.secondary_text_color, null)
            .show()
    }

  /*  private fun init() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(
            this@LoginActivity,
            object : OnSuccessListener<InstanceIdResult> {
                override fun onSuccess(instanceIdResult: InstanceIdResult) {
                    success(instanceIdResult)
                }

                private fun success(instanceIdResult: InstanceIdResult) {
                    // Get new Instance ID token
                    device_token = instanceIdResult.getToken()
                    Log.e("tokens", "tok  $device_token")
                }
            }).addOnFailureListener(object : OnFailureListener {
            override fun onFailure(e: Exception) {
                Log.e("tokens", "tok" + e.message)
                failure()
            }

            private fun failure() {
                init()
            }
        })
        FirebaseMessaging.getInstance().isAutoInitEnabled = true
    }*/

    private fun init()
    {
        FirebaseMessaging.getInstance().getToken()
            .addOnCompleteListener(OnCompleteListener<String?> { task ->
                if (!task.isSuccessful) {
                    Log.w("tokens", "FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new FCM registration token
                device_token = task.result

                // Log and toast
               // val msg = getString(R.string.msg_token_fmt, token)
                Log.d("tokens", "msg  "+device_token)
                //Toast.makeText(this@LoginActivity, device_token, Toast.LENGTH_SHORT).show()
            })
    }
}