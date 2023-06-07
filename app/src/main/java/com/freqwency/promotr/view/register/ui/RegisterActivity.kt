package com.freqwency.promotr.view.register.ui

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.developer.kalert.KAlertDialog
import com.freqwency.promotr.R
import com.freqwency.promotr.application.PromotrApp
import com.freqwency.promotr.databinding.ActivityRegisterBinding
import com.freqwency.promotr.utils.DialogsManager
import com.freqwency.promotr.utils.ErrorUtil
import com.freqwency.promotr.utils.GlobalFunctions
import com.freqwency.promotr.view.activities.BaseActivity
import com.freqwency.promotr.view.register.viewmodel.RegisterViewModel
import com.freqwency.promotr.view.register.viewmodel.RegistrationBody
import com.freqwency.promotr.view.verifymobile.ui.VerifyMobileActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.johncodeos.customprogressdialogexample.CustomProgressDialog
import com.skydoves.powerspinner.IconSpinnerAdapter
import com.skydoves.powerspinner.IconSpinnerItem
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class RegisterActivity : BaseActivity() {

    lateinit var registerBinding: ActivityRegisterBinding
    private lateinit var registerRequest: RegistrationBody
    var gender = ""
    var country = ""
    var dateOfBirth = ""
    var device_token = ""
    var cal = Calendar.getInstance()

    private lateinit var activity: Activity

    val registerViewModel: RegisterViewModel by viewModels()
    private val progressDialog by lazy { CustomProgressDialog(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        registerBinding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(registerBinding.root)

        activity = this

        GlobalFunctions.addUIPadding(registerBinding.root)
        registerBinding.llBackBtn.setOnClickListener {
            this.finish()
        }

        registerBinding.btnSignUp.setOnClickListener {
            validateInputs()
        }

        registerBinding.spinnerViewCountry.setOnClickListener {
            DialogsManager.openCountryBottomSheet(this) { countryModel ->
                registerBinding.llMobileLayout.visibility = View.VISIBLE
                registerBinding.etCountryCode.setText(countryModel.phoneCode)
                registerBinding.spinnerViewCountry.setText(if (PromotrApp.encryptedPrefs.appLanguage == "en") countryModel.name else countryModel.nameAR)
                country = countryModel.name!!
            }
        }

        registerBinding.dateLayout.setOnClickListener(View.OnClickListener { arg0: View? ->
            val calendar = Calendar.getInstance()
            val year = calendar[Calendar.YEAR]
            val month = calendar[Calendar.MONTH]
            val day = calendar[Calendar.DAY_OF_MONTH]
            val datePicker =
                DatePickerDialog(
                    this@RegisterActivity,
                    { view, year, monthOfYear, dayOfMonth ->
                        val month = monthOfYear + 1
                        cal = Calendar.getInstance()
                        cal.set(year, monthOfYear, dayOfMonth)
                        cal.add(Calendar.DATE, 1)

                        registerBinding.dateTxt.setText(
                            String.format(
                                Locale.ENGLISH,
                                "%02d-%02d-%04d",
                                dayOfMonth,
                                month,
                                year
                            )
                        )

                        dateOfBirth = registerBinding.dateTxt.text as String
                    }, year, month, day
                )
            datePicker.datePicker.maxDate = System.currentTimeMillis()
            datePicker.show()
        })

        // access the items of the lis
        val languages = resources.getStringArray(R.array.Languages)

        // access the spinner
        val spinner = findViewById<Spinner>(R.id.spinner)
        if (spinner != null) {
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item, languages
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter

            spinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                }
            }
        }

        registerBinding.spinnerViewGender.apply {
            setSpinnerAdapter(IconSpinnerAdapter(this))
            setItems(
                arrayListOf(
                    IconSpinnerItem(text = getString(R.string.male)),
                    IconSpinnerItem(text = getString(R.string.female)),
                )
            )
            setOnSpinnerItemSelectedListener<IconSpinnerItem> { _, _, _, item ->
                gender = item.text as String
            }
            getSpinnerRecyclerView().layoutManager = GridLayoutManager(baseContext, 1)
        }

        FirebaseApp.getApps(activity)
        init()

        registerObserver()
    }

    private fun init() {

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
                // Toast.makeText(this@SettingsActivity, FCMToken, Toast.LENGTH_SHORT).show()
            })

       /* FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(
            this@RegisterActivity,
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
        FirebaseMessaging.getInstance().isAutoInitEnabled = true*/
    }

    private fun validateInputs() {
        with(registerBinding) {
            if (etFirstName.text.isNullOrBlank()) {
                alerDialogs(getString(R.string.please_enter_first_name))
            } else if (etLastName.text.isNullOrBlank()) {
                alerDialogs(getString(R.string.please_enter_last_name))
            } else if (etEmail.text.isNullOrBlank()) {
                alerDialogs(getString(R.string.please_enter_email))
            } else if (gender == null || gender.equals("")) {
                alerDialogs(getString(R.string.please_select_gender))
            } else if (dateOfBirth == null || dateOfBirth.equals("")) {
                alerDialogs(getString(R.string.please_select_date))
            } else if (country == null || country.equals("")) {
                alerDialogs(getString(R.string.please_select_country))
            } else if (etMobileNumber.text.isNullOrBlank()) {
                alerDialogs(getString(R.string.please_enter_mobile))
            } else if (etPassword.text.isNullOrBlank() || etPassword.text.length < 8) {
                alerDialogs(getString(R.string.please_enter_password))
            } else if (etConfirmPassword.text.isNullOrBlank()) {
                alerDialogs(getString(R.string.please_enter_confirm_password))
            } else if (etPassword.text.isNotEmpty() && etConfirmPassword.text.isNotEmpty()) {
                if (etPassword.text.toString() != etConfirmPassword.text.toString()) {
                    alerDialogs(getString(R.string.passwod_confirm_password))
                } else {
                    registerCall()
                }
            }
        }
    }

    private fun alerDialogs(str: String) {
        KAlertDialog(this, KAlertDialog.ERROR_TYPE)
            .setTitleText(getString(R.string.signup_error))
            .setContentText(str)
            .confirmButtonColor(R.color.secondary)
            .setConfirmClickListener(getString(R.string.OK), R.color.secondary_text_color, null)
            .show()
    }

    private fun registerCall() {
        registerRequest = RegistrationBody(
            firstName = registerBinding.etFirstName.text.toString(),
            lastName = registerBinding.etLastName.text.toString(),
            mobileNumber = registerBinding.etCountryCode.text.toString() + registerBinding.etMobileNumber.text.toString(),
            email = registerBinding.etEmail.text.toString(),
            country = country,
            password = registerBinding.etPassword.text.toString(),
            passwordConfirmation = registerBinding.etConfirmPassword.text.toString(),
            gender = gender,
            hasAcceptedTerms = true,
            deviceName = PromotrApp.encryptedPrefs.deviceID,
            dateOfBirth = dateOfBirth
        )

        registerViewModel.registerUser(registerRequest, progressDialog, activity)
    }

    private fun registerObserver() {

        registerViewModel.progressIndicator.observe(this, Observer {
        })
        registerViewModel.mRegisterResponse.observe(
            this
        ) {
            PromotrApp.encryptedPrefs.token = it.peekContent().data?.accessToken ?: ""
            PromotrApp.encryptedPrefs.bearerToken = "Bearer " + (it.peekContent().data?.accessToken ?: "")
            PromotrApp.encryptedPrefs.user = it.peekContent().data?.user
            PromotrApp.encryptedPrefs.userId = it.peekContent().data?.user!!.id.toString()

            val intent = Intent(this@RegisterActivity, VerifyMobileActivity::class.java)
            //val intent = Intent(this@RegisterActivity, VerifyWithEmailActivity::class.java)
            intent.putExtra("resendOTP", false)
            intent.putExtra("mobileNumber", it.peekContent().data?.user?.mobileNumber ?: "")
            intent.putExtra("device_token", device_token)
            startActivity(intent)
            finish()
        }

        registerViewModel.errorResponse.observe(this) {
            ErrorUtil.handlerGeneralError(this@RegisterActivity,  it)
            errorDialogs()
        }
    }

    private fun errorDialogs() {
        KAlertDialog(this, KAlertDialog.ERROR_TYPE)
            .setTitleText(getString(R.string.register_error))
            .setContentText(getString(R.string.already_email))
            .confirmButtonColor(R.color.secondary)
            .setConfirmClickListener(getString(R.string.OK),R.color.secondary_text_color,null)
            .show()
    }
}