package com.freqwency.promotr.view.AddPromoCode.ui

import android.Manifest
import android.R
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.developer.kalert.KAlertDialog
import com.freqwency.promotr.databinding.ActivityAddNewPromoCodeBinding
import com.freqwency.promotr.utils.ErrorUtil
import com.freqwency.promotr.view.AddPromoCode.viewmodel.AddPromoCode.AddPromoCodeViewModel
import com.freqwency.promotr.view.AddPromoCode.viewmodel.EditPromoCode.EditPromoCodeViewModel
import com.freqwency.promotr.view.dashboard.viewmodel.category.CategoryResponse
import com.freqwency.promotr.view.promocodedetails.viewmodel.promoCodeDetails.PromoCodeDetailsViewModel
import com.freqwency.promotr.view.search.ui.SearchActivity
import com.johncodeos.customprogressdialogexample.CustomProgressDialog
import com.skydoves.powerspinner.IconSpinnerAdapter
import com.skydoves.powerspinner.IconSpinnerItem
import dagger.hilt.android.AndroidEntryPoint
import id.zelory.compressor.Compressor
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@AndroidEntryPoint
class AddNewPromoCodeActivity : AppCompatActivity() {
    private lateinit var activity: Activity
    val addPromoCodeViewModel: AddPromoCodeViewModel by viewModels()
    val editPromoCodeViewModel: EditPromoCodeViewModel by viewModels()
    val promoCodeByCategoryViewModel: PromoCodeDetailsViewModel by viewModels()
    lateinit var binding: ActivityAddNewPromoCodeBinding
    private val progressDialog by lazy { CustomProgressDialog(this) }
    var list: List<String> = java.util.ArrayList()
    var cal = Calendar.getInstance()
    var dateOfBirth = ""
    var categoryId = 0
    var promoCodeId = 0
    var type = ""
    var method = ""
    var categoryName = ""

    private val PERMISSION_REQUEST_CODE = 790
    private val REQUEST_CAMERA = 793
    private val SELECT_FILE = 794
    private var profileImage: File? = null
    var compressedImageFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddNewPromoCodeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        activity = this

        val sliders =
            intent.getSerializableExtra("actions") as ArrayList<CategoryResponse.Categorys>
        method = intent.getStringExtra("method").toString()
        categoryName = intent.getStringExtra("categoryName").toString()
        promoCodeId = intent.getIntExtra("categoryId", 0)

        binding.spinnerNew.setOnItemSelectedListener(object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                (parent.getChildAt(0) as TextView).setTextColor(Color.BLACK)
                val item: String = binding.spinnerNew.getSelectedItem().toString()
                if (item == "Choose") {
                } else {
                    categoryId = sliders[position].id
                    //catName = sliders[position].name
                    Log.e("sliders", " " + categoryId)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })

        list = java.util.ArrayList<String>()
        for (i in sliders.indices) {
            val category_name: String = sliders[i].name
            (list as java.util.ArrayList<String>).add(category_name)
        }

        //Create an ArrayAdapter
        val countryNameSpinnerAdapter =
            SearchActivity.SpinnerAdapter(this, R.layout.simple_spinner_item, list)
        countryNameSpinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        countryNameSpinnerAdapter.addAll(list)
        countryNameSpinnerAdapter.add("Choose")
        binding.spinnerNew.setAdapter(countryNameSpinnerAdapter)
        binding.spinnerNew.setSelection(countryNameSpinnerAdapter.getCount())

        val compareValue: String = categoryName
        if (compareValue == "") {
        } else {
            val spinnerPosition: Int = countryNameSpinnerAdapter.getPosition(compareValue)
            binding.spinnerNew.setSelection(spinnerPosition)
        }

        binding.spinnerType.apply {
            setSpinnerAdapter(IconSpinnerAdapter(this))
            setItems(
                arrayListOf(
                    IconSpinnerItem(text = "open"),
                    IconSpinnerItem(text = "close"),
                )
            )
            setOnSpinnerItemSelectedListener<IconSpinnerItem> { _, _, _, item ->
                type = item.text as String
            }
            getSpinnerRecyclerView().layoutManager = GridLayoutManager(baseContext, 1)
        }

        binding.datebtn.setOnClickListener(View.OnClickListener { arg0: View? ->
            val calendar = Calendar.getInstance()
            val year = calendar[Calendar.YEAR]
            val month = calendar[Calendar.MONTH]
            val day = calendar[Calendar.DAY_OF_MONTH]
            val datePicker =
                DatePickerDialog(
                    this@AddNewPromoCodeActivity,
                    { view, year, monthOfYear, dayOfMonth ->
                        val month = monthOfYear + 1
                        cal = Calendar.getInstance()
                        cal.set(year, monthOfYear, dayOfMonth)
                        cal.add(Calendar.DATE, 1)

                        binding.dateTxt.setText(
                            String.format(
                                Locale.ENGLISH,
                                "%02d-%02d-%04d",
                                dayOfMonth,
                                month,
                                year
                            )
                        )
                        dateOfBirth = binding.dateTxt.text as String
                    }, year, month, day
                )
            datePicker.datePicker.minDate = System.currentTimeMillis()
            datePicker.show()
        })

        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.cancelBtn.setOnClickListener {
            finish()
        }

        binding.applyBtn.setOnClickListener {
            if (method.equals("edit")) {
                validateInputsEdit()
            } else {
                validateInputs()
            }
        }

        if (method.equals("edit")) {
            binding.promoCodeHeading.text =
                getString(com.freqwency.promotr.R.string.edit_promo_code)
            getPromoCodeDetails()
        } else {
            binding.promoCodeHeading.text =
                getString(com.freqwency.promotr.R.string.add_new_promo_code)
        }

        binding.uploadImageProfile.setOnClickListener {
            askPermissions()
        }

        addPromoCodesObserver()
        editPromoCodesObserver()
        promoCodeDetailsObserver()
    }

    private fun askPermissions() {
        val permissions = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(
                    Objects.requireNonNull<Any>(
                        activity
                    ) as Context, permission
                ) == PackageManager.PERMISSION_DENIED
            ) {
                requestPermissions(
                    permissions,
                    PERMISSION_REQUEST_CODE
                )
                return
            }
        }
        selectImage()
    }

    private fun selectImage() {
        val options =
            arrayOf<CharSequence>("Choose from Gallery", "Cancel")
        val builder =
            AlertDialog.Builder(activity)
        builder.setItems(
            options
        ) { dialog: DialogInterface, item: Int ->
            if (options[item] == "Choose from Gallery") {
                val photoPickerIntent = Intent(Intent.ACTION_PICK)
                photoPickerIntent.type = "image/*"
                startActivityForResult(
                    photoPickerIntent,
                    SELECT_FILE
                )
            } else if (options[item] == "Cancel") {
                dialog.dismiss()
            }
        }
        val cameradialog = builder.create()
        cameradialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                GlobalScope.launch {
                    compressedImageFile = profileImage?.let {
                        Compressor.compress(
                            activity,
                            it
                        )
                    }
                }
            } else if (requestCode == SELECT_FILE) {
                val selectedImageURI = data!!.data
                val path: String = getPathFromURI(selectedImageURI!!)!!

                if (path != null) {
                    //api call
                    profileImage = File(path)

                    Log.v("Path", "path:  " + path + " " + profileImage)
                    binding.uploadImageProfile.text =
                        getString(com.freqwency.promotr.R.string.uploaded_successfully)

                    //var compressedImageFile:File?=null
                    GlobalScope.launch {
                        compressedImageFile = profileImage?.let {
                            Compressor.compress(
                                activity,
                                it
                            )
                        }
                    }
                }
            }
        }
    }

    private fun getPathFromURI(contentUri: Uri): String? {
        var res: String? = null
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = activity.contentResolver.query(contentUri, proj, null, null, null)
        if (Objects.requireNonNull(cursor)!!.moveToFirst()) {
            val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            res = cursor.getString(column_index)
        }
        cursor!!.close()
        return res
    }


    private fun validateInputs() {
        with(binding) {

            if (categoryId == null || categoryId.equals(0)) {
                alerDialogs(getString(com.freqwency.promotr.R.string.please_select_category))
            } else if (titleEdt.text.isNullOrBlank()) {
                alerDialogs(getString(com.freqwency.promotr.R.string.please_enter_name))
            } else if (type == null || type.equals("")) {
                alerDialogs(getString(com.freqwency.promotr.R.string.please_select_type))
            } else if (discountAmountEdt.text.isNullOrBlank()) {
                alerDialogs(getString(com.freqwency.promotr.R.string.please_enter_discount))
            } else if (storeNameEdt.text.isNullOrBlank()) {
                alerDialogs(getString(com.freqwency.promotr.R.string.please_enter_store_name))
            } else if (storeLinkEdt.text.isNullOrBlank()) {
                alerDialogs(getString(com.freqwency.promotr.R.string.please_enter_store_link))
            } else if (codeIdEdt.text.isNullOrBlank()) {
                alerDialogs(getString(com.freqwency.promotr.R.string.please_enter_code_id))
            } else if (dateOfBirth == null || dateOfBirth.equals("")) {
                alerDialogs(getString(com.freqwency.promotr.R.string.please_select_date))
            } else if (descriptionEdt.text.isNullOrBlank()) {
                alerDialogs(getString(com.freqwency.promotr.R.string.please_enter_terms_and_conditions))
            } else {
                addPromoCodes()
            }
        }
    }

    private fun validateInputsEdit() {
        with(binding) {

            if (categoryId == null || categoryId.equals(0)) {
                alerDialogs(getString(com.freqwency.promotr.R.string.please_select_category))
            } else if (titleEdt.text.isNullOrBlank()) {
                alerDialogs(getString(com.freqwency.promotr.R.string.please_enter_name))
            } else if (type == null || type.equals("")) {
                alerDialogs(getString(com.freqwency.promotr.R.string.please_select_type))
            } else if (discountAmountEdt.text.isNullOrBlank()) {
                alerDialogs(getString(com.freqwency.promotr.R.string.please_enter_discount))
            } else if (storeNameEdt.text.isNullOrBlank()) {
                alerDialogs(getString(com.freqwency.promotr.R.string.please_enter_store_name))
            } else if (storeLinkEdt.text.isNullOrBlank()) {
                alerDialogs(getString(com.freqwency.promotr.R.string.please_enter_store_link))
            } else if (codeIdEdt.text.isNullOrBlank()) {
                alerDialogs(getString(com.freqwency.promotr.R.string.please_enter_code_id))
            } else if (dateOfBirth == null || dateOfBirth.equals("")) {
                alerDialogs(getString(com.freqwency.promotr.R.string.please_select_date))
            } else if (descriptionEdt.text.isNullOrBlank()) {
                alerDialogs(getString(com.freqwency.promotr.R.string.please_enter_terms_and_conditions))
            } else {
                EditPromoCodes()
            }
        }
    }

    /**
     * Add Promoode for Promoter
     */
    fun addPromoCodes() {
        val titles = binding.titleEdt.text.toString()
        val discounts = binding.discountAmountEdt.text.toString()
        val storeNames = binding.storeNameEdt.text.toString()
        val storeLinks = binding.storeLinkEdt.text.toString()
        val codeIds = binding.codeIdEdt.text.toString()
        val descriptions = binding.descriptionEdt.text.toString()

        val title = RequestBody.create(MultipartBody.FORM, titles)
        val discount = RequestBody.create(MultipartBody.FORM, discounts)
        val storeName = RequestBody.create(MultipartBody.FORM, storeNames)
        val storeLink = RequestBody.create(MultipartBody.FORM, storeLinks)
        val codeId = RequestBody.create(MultipartBody.FORM, codeIds)
        val description = RequestBody.create(MultipartBody.FORM, descriptions)
        val categoryIds = RequestBody.create(MultipartBody.FORM, categoryId.toString())
        val types = RequestBody.create(MultipartBody.FORM, type)
        val dateOfBirths = RequestBody.create(MultipartBody.FORM, dateOfBirth)
        val images = MultipartBody.Part.createFormData(
            "promocode_image",
            compressedImageFile?.name,
            RequestBody.create("image/*".toMediaTypeOrNull(), compressedImageFile!!)
        )

        addPromoCodeViewModel.getAddPromoCodeForPromoter(
            progressDialog,
            activity,
            categoryIds,
            title,
            types,
            discount,
            storeName,
            storeLink,
            codeId,
            dateOfBirths,
            description,
            images
        )
    }

    private fun addPromoCodesObserver() {
        addPromoCodeViewModel.progressIndicator.observe(this, androidx.lifecycle.Observer {
        })
        addPromoCodeViewModel.mAddPromoCodeResponse.observe(
            this
        ) {
            //startActivity(Intent(this@AddNewPromoCodeActivity, MainActivity::class.java))
            finish()
        }

        addPromoCodeViewModel.errorResponse.observe(this) {
            ErrorUtil.handlerGeneralError(this, it)
        }
    }


    /**
     * Promoode details for Promoter
     */
    fun getPromoCodeDetails() {
        promoCodeByCategoryViewModel.getpromoCodeDetails(progressDialog, activity, promoCodeId)
    }

    private fun promoCodeDetailsObserver() {
        promoCodeByCategoryViewModel.progressIndicator.observe(
            this@AddNewPromoCodeActivity,
            androidx.lifecycle.Observer {
            })
        promoCodeByCategoryViewModel.mPromocodeDetailsResponse.observe(
            this@AddNewPromoCodeActivity
        ) {
            binding.titleEdt.text =
                Editable.Factory.getInstance().newEditable(it.peekContent().data?.promocode?.title)
            binding.spinnerType.setText(it.peekContent().data?.promocode?.type)
            binding.discountAmountEdt.text = Editable.Factory.getInstance()
                .newEditable(it.peekContent().data?.promocode?.discount_amount)
            binding.storeNameEdt.text = Editable.Factory.getInstance()
                .newEditable(it.peekContent().data?.promocode?.store_name)
            binding.storeLinkEdt.text = Editable.Factory.getInstance()
                .newEditable(it.peekContent().data?.promocode?.store_website_url)
            binding.codeIdEdt.text = Editable.Factory.getInstance()
                .newEditable(it.peekContent().data?.promocode?.code_id)
            binding.descriptionEdt.text = Editable.Factory.getInstance()
                .newEditable(it.peekContent().data?.promocode?.description)

            val expiry_dates = it.peekContent().data?.promocode?.expiry_date
            val full_image_url = it.peekContent().data?.promocode?.full_image_url

            if (!full_image_url.equals(null)) {
                binding.uploadImageProfile.text =
                    getString(com.freqwency.promotr.R.string.uploaded_successfully)
            }

            val inputFormatter: DateTimeFormatter =
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.ENGLISH)
            val outputFormatter: DateTimeFormatter =
                DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.ENGLISH)
            val date: LocalDate = LocalDate.parse(expiry_dates, inputFormatter)
            val formattedDate: String = outputFormatter.format(date)
            binding.dateTxt.text = formattedDate

            categoryId = it.peekContent().data?.promocode?.category_id!!
            type = it.peekContent().data?.promocode?.type!!
            dateOfBirth = formattedDate
        }

        promoCodeByCategoryViewModel.errorResponse.observe(this@AddNewPromoCodeActivity) {
            ErrorUtil.handlerGeneralError(this@AddNewPromoCodeActivity, it)
        }
    }


    /**
     * Edit Promoode for Promoter
     */
    fun EditPromoCodes() {
        val title = binding.titleEdt.text.toString()
        val discount = binding.discountAmountEdt.text.toString()
        val storeName = binding.storeNameEdt.text.toString()
        val storeLink = binding.storeLinkEdt.text.toString()
        val codeId = binding.codeIdEdt.text.toString()
        val description = binding.descriptionEdt.text.toString()

        editPromoCodeViewModel.getEditPromoCodeForPromoter(
            progressDialog,
            activity,
            categoryId,
            title,
            type,
            discount,
            storeName,
            storeLink,
            codeId,
            dateOfBirth,
            description,
            promoCodeId
        )
    }

    private fun editPromoCodesObserver() {
        editPromoCodeViewModel.progressIndicator.observe(this, androidx.lifecycle.Observer {
        })
        editPromoCodeViewModel.mEditPromoCodeResponse.observe(
            this
        ) {
            //startActivity(Intent(this@AddNewPromoCodeActivity, MainActivity::class.java))
            finish()
        }

        editPromoCodeViewModel.errorResponse.observe(this) {
            ErrorUtil.handlerGeneralError(this, it)
        }
    }


    private fun alerDialogs(str: String) {
        KAlertDialog(this, KAlertDialog.ERROR_TYPE)
            .setTitleText(getString(com.freqwency.promotr.R.string.add_new_promo_code))
            .setContentText(str)
            .confirmButtonColor(com.freqwency.promotr.R.color.secondary)
            .setConfirmClickListener(
                getString(com.freqwency.promotr.R.string.OK),
                com.freqwency.promotr.R.color.secondary_text_color,
                null
            )
            .show()
    }
}