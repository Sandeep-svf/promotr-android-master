package com.freqwency.promotr.view.userprofile.ui

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.freqwency.promotr.databinding.ActivityProfileBinding
import com.freqwency.promotr.utils.ErrorUtil
import com.freqwency.promotr.view.activities.BaseActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.freqwency.promotr.R
import com.freqwency.promotr.application.PromotrApp
import com.freqwency.promotr.utils.DialogsManager
import com.freqwency.promotr.view.AddPromoCode.ui.AddNewPromoCodeActivity
import com.freqwency.promotr.view.becomePromoter.ui.PromoOwnerRegisterActivity
import com.freqwency.promotr.view.dashboard.viewmodel.category.CategoryResponse
import com.freqwency.promotr.view.dashboard.viewmodel.category.CategoryViewModel
import com.freqwency.promotr.view.logins.ui.LoginActivity
import com.freqwency.promotr.view.userprofile.viewmodel.DeleteAccount.DeleteAccountViewModel
import com.freqwency.promotr.view.userprofile.viewmodel.userprofile.UserProfileViewModel
import com.freqwency.promotr.view.userprofile.viewmodel.updateprofile.UpdateUserProfileViewModel
import com.johncodeos.customprogressdialogexample.CustomProgressDialog
import com.skydoves.powerspinner.IconSpinnerAdapter
import com.skydoves.powerspinner.IconSpinnerItem
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@AndroidEntryPoint
class ProfileActivity : BaseActivity() {
    private val categoryViewModel: CategoryViewModel by viewModels()
    private var catList: ArrayList<CategoryResponse.Categorys> = ArrayList()
    lateinit var dialogs: AlertDialog

    private val userProfileViewModel: UserProfileViewModel by viewModels()
    private val updateUserProfileViewModel: UpdateUserProfileViewModel by viewModels()
    private val deleteAccountViewModel: DeleteAccountViewModel by viewModels()
    private val progressDialog by lazy { CustomProgressDialog(this) }

    lateinit var binding: ActivityProfileBinding
    private lateinit var activity: Activity
    var firstName = ""
    var lastName = ""
    var mobileNumber = ""
    var emailId = ""
    var gender = ""
    var dateOfBirth = ""
    var country = ""
    var promoterId = ""

    var isEdit = false
    var cal = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = this
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        promoterId = PromotrApp.encryptedPrefs.promoterId

        if (promoterId == "") {
            binding.aboutTxt.visibility = View.GONE
            binding.promoterDetailsLayout.visibility = View.GONE
            binding.btnBecomePromoter.setText(getString(R.string.become_a_promo_owner))
        } else {
            binding.aboutTxt.visibility = View.VISIBLE
            binding.promoterDetailsLayout.visibility = View.VISIBLE
            binding.btnBecomePromoter.setText(getString(R.string.add_promocode))
        }

        binding.btnBecomePromoter.setOnClickListener {
            if (promoterId.equals("")) {
                val intent = Intent(activity, PromoOwnerRegisterActivity::class.java)
                activity.startActivity(intent)
            } else {
                val intent = Intent(activity, AddNewPromoCodeActivity::class.java)
                intent.putExtra("categoryName", "")
                intent.putExtra("categoryId", "")
                intent.putExtra("actions", catList)
                intent.putExtra("method", "add")
                activity.startActivity(intent)
            }
        }

        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.updateProfileBtn.setOnClickListener {
            getUpdateUserProfile(progressDialog)
        }
        updateEditView()

        binding.editBtn.setOnClickListener {
            this.isEdit = !this.isEdit
            updateEditView()
        }

        binding.datebtn.setOnClickListener { arg0: View? ->
            val calendar = Calendar.getInstance()
            val year = calendar[Calendar.YEAR]
            val month = calendar[Calendar.MONTH]
            val day = calendar[Calendar.DAY_OF_MONTH]
            val datePicker =
                DatePickerDialog(
                    activity,
                    { _, year, monthOfYear, dayOfMonth ->
                        val month = monthOfYear + 1
                        cal = Calendar.getInstance()
                        cal.set(year, monthOfYear, dayOfMonth)
                        cal.add(Calendar.DATE, 1)

                        binding.dareOfBirthTxt.setText(
                            String.format(
                                Locale.ENGLISH,
                                "%02d-%02d-%04d",
                                dayOfMonth,
                                month,
                                year
                            )
                        )

                        dateOfBirth = binding.dareOfBirthTxt.text as String
                    }, year, month, day
                )
            datePicker.datePicker.maxDate = System.currentTimeMillis()
            datePicker.show()
        }


        binding.spinnerViewCountry.setOnClickListener {
            DialogsManager.openCountryBottomSheet(activity) { countryModel ->
                binding.spinnerViewCountry.setText(if (PromotrApp.encryptedPrefs.appLanguage == "en") countryModel.name else countryModel.nameAR)
                country = countryModel.name!!
            }
        }

        binding.deleteAccountLayout.setOnClickListener {
            DeleteAccount_AlertDialog()
        }

        getUserProfile(progressDialog)
        UserProfileObserver()
        UpdateUserProfileObserver()
        DeleteAccountObserver()

        categoryObserver()
    }

    fun updateEditView() {
        binding.firstNameTxt.isEnabled = isEdit
        binding.lastNameTxt.isEnabled = isEdit
        binding.mobileTxt.isEnabled = isEdit
        // binding.emailTxt.isEnabled = true
        binding.datebtn.isEnabled = isEdit
        binding.spinnerViewGender.isEnabled = isEdit
        binding.spinnerViewCountry.isEnabled = isEdit

        binding.nickNameTxt.isEnabled = isEdit
        binding.instagramTxt.isEnabled = isEdit
        binding.facebookTxt.isEnabled = isEdit

        binding.btnBecomePromoter.visibility = if (isEdit) View.GONE else View.VISIBLE
        binding.updateProfileBtn.visibility = if (isEdit) View.VISIBLE else View.GONE
    }

    /**
     * Get User Profile  & Category List
     */
    fun getUserProfile(progressDialog: CustomProgressDialog) {
        userProfileViewModel.getUserProfile(progressDialog, activity)
        categoryViewModel.getCategory(progressDialog, activity)
    }
    private fun UserProfileObserver() {
        userProfileViewModel.progressIndicator.observe(this, Observer {
        })
        userProfileViewModel.mUserProfileResponse.observe(
            this
        ) {
            val users = it.peekContent().data?.user

            if (users != null) {
                emailId = users.email
                country = users.country
                dateOfBirth = users.date_of_birth
                gender = users.gender
            }

            if (promoterId != "") {
                val url = it.peekContent().data?.user!!.promoter_info?.full_image_url
                Glide
                    .with(activity)
                    .load(url)
                    .placeholder(R.drawable.slider1)
                    .into(binding.imageView)
            }

            if (users != null) {
                binding.aboutTxt.text = (users.promoter_info?.about ?: "")
                binding.nameTxt.text =
                    Editable.Factory.getInstance()
                        .newEditable(users.first_name + " " + users.last_name)

                binding.firstNameTxt.text =
                    Editable.Factory.getInstance().newEditable(users.first_name)
                binding.lastNameTxt.text = Editable.Factory.getInstance().newEditable(users.last_name)
                binding.emailTxt.text = Editable.Factory.getInstance().newEditable(users.email)
                binding.mobileTxt.text = Editable.Factory.getInstance().newEditable(users.mobile_number)
                binding.nickNameTxt.text = Editable.Factory.getInstance().newEditable(users.promoter_info?.nickname ?: "")
                binding.instagramTxt.text = Editable.Factory.getInstance().newEditable(users.promoter_info?.instagram_profile_url ?: "")
                binding.facebookTxt.text = Editable.Factory.getInstance().newEditable(users.promoter_info?.facebook_profile_url ?: "")
            }

            val birth_dates = users?.date_of_birth

            val inputFormatter: DateTimeFormatter =
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.ENGLISH)
            //val outputFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy", Locale.ENGLISH)
            val outputFormatter: DateTimeFormatter =
                DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.ENGLISH)
            val date: LocalDate = LocalDate.parse(birth_dates, inputFormatter)
            val formattedDate: String = outputFormatter.format(date)
            binding.dareOfBirthTxt.text = formattedDate

            binding.spinnerViewGender.text = gender

            binding.spinnerViewGender.apply {
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
                getSpinnerRecyclerView().layoutManager = GridLayoutManager(activity, 1)
                /*if (gender.equals("M")) {
                    selectItemByIndex(0) // select a default item.
                } else {
                    selectItemByIndex(1) // select a default item.
                }*/
                lifecycleOwner = this@ProfileActivity
            }

            binding.spinnerViewCountry.text = users?.country
        }

        userProfileViewModel.errorResponse.observe(this) {
            ErrorUtil.handlerGeneralError(this, it)
        }
    }
    private fun categoryObserver() {
        categoryViewModel.progressIndicator.observe(this, Observer {
        })
        categoryViewModel.mCategoryResponse.observe(
            this
        ) {
            catList = it.peekContent().data?.categorys!!
        }

        categoryViewModel.errorResponse.observe(this) {
            ErrorUtil.handlerGeneralError(this, it)
        }
    }


    /**
     * Update User Profile
     */
    fun getUpdateUserProfile(progressDialog: CustomProgressDialog) {
        firstName = binding.firstNameTxt.text.toString()
        lastName = binding.lastNameTxt.text.toString()
        mobileNumber = binding.mobileTxt.text.toString()
        emailId = binding.emailTxt.text.toString()
        //country = binding.etCountry.text.toString()
        dateOfBirth = binding.dareOfBirthTxt.text.toString()
        var nickname = binding.nickNameTxt.text.toString()
        var instagram_profile_url = binding.instagramTxt.text.toString()
        var facebook_profile_url = binding.facebookTxt.text.toString()

        /* val str = binding.nameTxt.text.toString()
         firstName = str.split(" ")[0]
         lastName = str.split(" ")[1]*/

        val jsonObject = JSONObject()
        jsonObject.put("first_name", firstName)
        jsonObject.put("last_name", lastName)
        jsonObject.put("mobile_number", mobileNumber)
        // jsonObject.put("email", emailId)
        jsonObject.put("gender", gender)
        jsonObject.put("date_of_birth", dateOfBirth)
        jsonObject.put("country", country)
        jsonObject.put("nickname", nickname)
        jsonObject.put("instagram_profile_url", instagram_profile_url)
        jsonObject.put("facebook_profile_url", facebook_profile_url)

        // Convert JSONObject to String
        val jsonObjectString = jsonObject.toString()
        // Create RequestBody ( We're not using any converter, like GsonConverter, MoshiConverter e.t.c, that's why we use RequestBody )
        val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())

        updateUserProfileViewModel.getUpdateUserProfile(progressDialog, activity, requestBody)
    }
    private fun UpdateUserProfileObserver() {
        updateUserProfileViewModel.progressIndicator.observe(this, Observer {
        })
        updateUserProfileViewModel.mUpdateUserProfileResponse.observe(
            this
        ) {
            isEdit = false
            var users = it.peekContent().data?.user

            binding.nameTxt.text = Editable.Factory.getInstance()
                .newEditable(users?.first_name + " " + users?.last_name)
            binding.emailTxt.text = Editable.Factory.getInstance().newEditable(users?.email)
            //binding.dareOfBirthTxt.text = users?.date_of_birth

            val birth_dates = users?.date_of_birth

            val inputFormatter: DateTimeFormatter =
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.ENGLISH)
            val outputFormatter: DateTimeFormatter =
                DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.ENGLISH)
            //val outputFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy", Locale.ENGLISH)
            val date: LocalDate = LocalDate.parse(birth_dates, inputFormatter)
            val formattedDate: String = outputFormatter.format(date)
            binding.dareOfBirthTxt.text = formattedDate
            updateEditView()
        }

        updateUserProfileViewModel.errorResponse.observe(this) {
            ErrorUtil.handlerGeneralError(this, it)
        }
    }


    private fun DeleteAccount_AlertDialog() {
        val inflater = this@ProfileActivity.layoutInflater
        val alertLayout: View = inflater.inflate(R.layout.custom_alert_dialog, null)
        val tvMessage = alertLayout.findViewById<TextView>(R.id.textViewMessage)
        val btnDelete = alertLayout.findViewById<TextView>(R.id.btnd_delete)
        val btncancel = alertLayout.findViewById<TextView>(R.id.btn_cancel)
        val alert = AlertDialog.Builder(activity)

        alert.setTitle(getString(R.string.confirm_delete_account))
        tvMessage.text = getString(R.string.are_you_sure_to_delete_acccount)
        alert.setView(alertLayout)
        alert.setCancelable(false)
        btncancel.setOnClickListener { v: View? ->
            dialogs!!.dismiss()
            //relative_logout.setEnabled(true)
        }
        btnDelete.setOnClickListener { v: View? ->
            // relative_logout.setEnabled(true)
            dialogs!!.dismiss()
            getDeleteAccount()
        }
        dialogs = alert.create()
        dialogs.show()
    }


    /**
     * Delete User Account
     */
    private fun getDeleteAccount() {
        deleteAccountViewModel.DeleteUserProfile(progressDialog, activity)
    }
    private fun DeleteAccountObserver() {
        deleteAccountViewModel.progressIndicator.observe(this, Observer {
        })
        deleteAccountViewModel.mDeleteAccountResponse.observe(
            this
        ) {
            PromotrApp.encryptedPrefs.bearerToken = ""
            PromotrApp.encryptedPrefs.promoterId = ""
            PromotrApp.encryptedPrefs.userId = ""
            PromotrApp.encryptedPrefs.user = null
            PromotrApp.encryptedPrefs.isFirstTime = true
            val intent = Intent(activity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            activity.startActivity(intent)
            finish()
        }

        deleteAccountViewModel.errorResponse.observe(this) {
            ErrorUtil.handlerGeneralError(this, it)
        }
    }
}