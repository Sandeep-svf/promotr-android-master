package com.freqwency.promotr.view.forgotpassword.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.developer.kalert.KAlertDialog
import com.freqwency.promotr.R
import com.freqwency.promotr.databinding.ActivityResetPasswordBinding
import com.freqwency.promotr.utils.ErrorUtil
import com.freqwency.promotr.utils.GlobalFunctions
import com.freqwency.promotr.view.activities.BaseActivity
import com.freqwency.promotr.view.forgotpassword.viewmodel.ForgotBody
import com.freqwency.promotr.view.forgotpassword.viewmodel.ForgotViewModel
import com.freqwency.promotr.view.logins.ui.LoginActivity
import com.johncodeos.customprogressdialogexample.CustomProgressDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResetPasswordActivity : BaseActivity() {
    lateinit var binding: ActivityResetPasswordBinding
    private lateinit var activity: Activity

    val forgotViewModel: ForgotViewModel by viewModels()

    private val progressDialog by lazy { CustomProgressDialog(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        activity = this

        GlobalFunctions.addUIPadding(binding.root)
        binding.llBackBtn.setOnClickListener {
            this.finish()
        }

        if (intent.hasExtra("email")) {
            binding.etEmail.setText(intent.getStringExtra("email"))
        }

        binding.btnVerify.setOnClickListener {
            if (binding.etEmail.text.isNullOrEmpty()) {
                errorDialogs(getString(R.string.please_enter_email))
            } else {
                binding.etEmail.background = getDrawable(R.drawable.edittext)
                resetPassword()
            }
        }
        observer()
    }

    private fun errorDialogs(str: String) {
        KAlertDialog(this, KAlertDialog.ERROR_TYPE)
            .setTitleText(getString(R.string.reset_pass_error))
            .setContentText(str)
            .confirmButtonColor(R.color.secondary)
            .setConfirmClickListener(getString(R.string.OK), R.color.secondary_text_color, null)
            .show()
    }

    private fun observer() {

        forgotViewModel.progressIndicator.observe(this, Observer {
        })
        forgotViewModel.mLoginResponse.observe(
            this
        ) {
            val intent = Intent(this@ResetPasswordActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        forgotViewModel.errorResponse.observe(this) {
            ErrorUtil.handlerGeneralError(this@ResetPasswordActivity, it)
        }
    }

    private fun resetPassword() {
        val forgotBody = ForgotBody(
            email = binding.etEmail.text.toString()
        )
        forgotViewModel.ForgotPass(forgotBody, progressDialog, activity)
    }
}