package com.freqwency.promotr.view.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.freqwency.promotr.R
import com.freqwency.promotr.application.PromotrApp
import com.freqwency.promotr.databinding.ActivitySettingsBinding
import com.freqwency.promotr.utils.ErrorUtil
import com.freqwency.promotr.view.logins.viewmodel.logout.LogoutViewModel
import com.freqwency.promotr.view.logins.viewmodel.updateFCMToken.FcmTokenBody
import com.freqwency.promotr.view.logins.viewmodel.updateFCMToken.UpdateFCMTokenViewModel
import com.freqwency.promotr.view.sliders.ui.SlidersActivity
import com.freqwency.promotr.model.GetSlidersResponse
import com.freqwency.promotr.view.sliders.viewmodel.SlidersViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.johncodeos.customprogressdialogexample.CustomProgressDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsActivity : BaseActivity() {
    lateinit var dialogs: AlertDialog
    private lateinit var activity: Activity
    private val viewModel: LogoutViewModel by viewModels()
    private val updateFCMTokenViewModel: UpdateFCMTokenViewModel by viewModels()
    private val slidersViewModel: SlidersViewModel by viewModels()
    var slidersList: ArrayList<GetSlidersResponse.SliderBean>? = null
    private val progressDialog by lazy { CustomProgressDialog(this) }

    lateinit var binding: ActivitySettingsBinding
    private var FCMToken = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        activity = this

        binding.switchToggle.isChecked = PromotrApp.encryptedPrefs.isNotification

        FCMToken = PromotrApp.encryptedPrefs.FCMToken

        binding.switchToggle.setOnCheckedChangeListener { buttonView, isChecked ->
            /* val msg: String = if (isChecked) {
                 "Switch Button is Checked"
             } else {
                 "Switch Button is UnChecked"
             }
             showToast(msg)*/

            if (PromotrApp.encryptedPrefs.user === null) {
                PromotrApp.encryptedPrefs.isNotification = isChecked
            } else {
                if (isChecked) {
                    PromotrApp.encryptedPrefs.isNotification = true
                    getUpdateFCMToken(FCMToken)
                } else {
                    PromotrApp.encryptedPrefs.isNotification = false
                    getUpdateFCMToken("")
                }
            }
        }

        binding.llBackBtn.setOnClickListener {
            finish()
        }

        if (PromotrApp.encryptedPrefs.user === null) {
            binding.signOutLayout.visibility = View.GONE
            binding.notificationLayout.visibility = View.VISIBLE
            binding.views.visibility = View.GONE
        } else {
            binding.signOutLayout.visibility = View.VISIBLE
            binding.notificationLayout.visibility = View.VISIBLE
            binding.views.visibility = View.VISIBLE
        }

        binding.signOutLayout.setOnClickListener {
            Logout_AlertDialog()
        }

        FirebaseApp.getApps(activity)
        init()
        getSliders()
        logoutObserver()
        updateFCMTokenObserver()
        observer()
    }

    private fun init() {
        FirebaseMessaging.getInstance().getToken()
            .addOnCompleteListener(OnCompleteListener<String?> { task ->
                if (!task.isSuccessful) {
                    Log.w("tokens", "FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new FCM registration token
                FCMToken = task.result

                Log.d("tokens", "msg  " + FCMToken)
            })


        /*FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(
            this@SettingsActivity,
            object : OnSuccessListener<InstanceIdResult> {
                override fun onSuccess(instanceIdResult: InstanceIdResult) {
                    success(instanceIdResult)
                }

                private fun success(instanceIdResult: InstanceIdResult) {
                    // Get new Instance ID token
                    FCMToken = instanceIdResult.getToken()
                    Log.e("tokens", "toks  $FCMToken")
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
        FirebaseMessaging.getInstance().isAutoInitEnabled = true*/
    }

    fun getSliders() {
        slidersViewModel.slider()
    }

    private fun observer() {
        slidersViewModel.progressIndicator.observe(this, Observer {
        })
        slidersViewModel.getSliderResponse.observe(
            this
        ) {
            slidersList = it.peekContent().data?.appSliders
        }

        slidersViewModel.errorResponse.observe(this) {
            ErrorUtil.handlerGeneralError(this@SettingsActivity, it)
        }
    }

    private fun showToast(msg: String) {
        Toast.makeText(this@SettingsActivity, msg, Toast.LENGTH_SHORT).show()
    }

    private fun Logout_AlertDialog() {
        val inflater = this@SettingsActivity.layoutInflater
        val alertLayout: View = inflater.inflate(R.layout.custom_alert_dialog, null)
        val tvMessage = alertLayout.findViewById<TextView>(R.id.textViewMessage)
        val btnDelete = alertLayout.findViewById<TextView>(R.id.btnd_delete)
        val btncancel = alertLayout.findViewById<TextView>(R.id.btn_cancel)
        val alert = AlertDialog.Builder(activity)

        alert.setTitle(getString(R.string.confirm_logout))
        tvMessage.text = getString(R.string.are_you_sure_to_logout)
        alert.setView(alertLayout)
        alert.setCancelable(false)
        btncancel.setOnClickListener { v: View? ->
            dialogs.dismiss()
            //relative_logout.setEnabled(true)
        }
        btnDelete.setOnClickListener { v: View? ->
            // relative_logout.setEnabled(true)
            dialogs.dismiss()
            getLogout()
        }
        dialogs = alert.create()
        dialogs.show()
    }

    private fun getLogout() {
        viewModel.logoutUser(progressDialog, activity)
    }

    private fun logoutObserver() {
        viewModel.progressIndicator.observe(this, Observer {
        })
        viewModel.mLogoutResponse.observe(
            this
        ) {
            PromotrApp.encryptedPrefs.bearerToken = ""
            PromotrApp.encryptedPrefs.promoterId = ""
            PromotrApp.encryptedPrefs.userId = ""
            PromotrApp.encryptedPrefs.user = null
            PromotrApp.encryptedPrefs.isFirstTime = true
            //val intent = Intent(activity, MainActivity::class.java)
            val intent = Intent(activity, SlidersActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra("sliders", slidersList)
            activity.startActivity(intent)
            finish()
        }

        viewModel.errorResponse.observe(this) {
            ErrorUtil.handlerGeneralError(this, it)
        }
    }

    /**
     * Update FCM Token
     */
    private fun getUpdateFCMToken(FCMToken: String) {

        val fcmTokenBody = FcmTokenBody(
            fcm_token = FCMToken
        )
        Log.e("Closet_Item", "   Id: " + fcmTokenBody)
        updateFCMTokenViewModel.getUpdateFCMToken(fcmTokenBody)
    }


    private fun updateFCMTokenObserver() {

        updateFCMTokenViewModel.progressIndicator.observe(this, Observer {
        })
        updateFCMTokenViewModel.mUpdateFCMTokenResponse.observe(
            this
        ) {
        }

        updateFCMTokenViewModel.errorResponse.observe(this) {
            ErrorUtil.handlerGeneralError(this@SettingsActivity, it)
        }
    }
}