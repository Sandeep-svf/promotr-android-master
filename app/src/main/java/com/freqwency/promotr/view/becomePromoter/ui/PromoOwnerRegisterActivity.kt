package com.freqwency.promotr.view.becomePromoter.ui

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.developer.kalert.KAlertDialog
import com.freqwency.promotr.R
import com.freqwency.promotr.application.PromotrApp
import com.freqwency.promotr.databinding.ActivityPromoOwnerRegisterBinding
import com.freqwency.promotr.utils.ErrorUtil
import com.freqwency.promotr.view.activities.MainActivity
import com.freqwency.promotr.view.becomePromoter.becomePromoterProfile.PromoterProfileViewModel
import com.freqwency.promotr.view.activities.BaseActivity
import com.johncodeos.customprogressdialogexample.CustomProgressDialog
import dagger.hilt.android.AndroidEntryPoint
import id.zelory.compressor.Compressor
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class PromoOwnerRegisterActivity : BaseActivity() {
    private val PERMISSION_REQUEST_CODE = 790
    private val REQUEST_CAMERA = 793
    private val SELECT_FILE = 794
    val promoterProfileViewModel: PromoterProfileViewModel by viewModels()
    private lateinit var binding: ActivityPromoOwnerRegisterBinding
    private lateinit var activity: Activity
    private var profileImage: File? = null
    var compressedImageFile: File? = null
    private val progressDialog by lazy { CustomProgressDialog(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPromoOwnerRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        activity = this

        val builder = VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        builder.detectFileUriExposure()

        val button: Button = binding.btnsignup

        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.uploadImageProfile.setOnClickListener {
            askPermissions()
        }

        button.setOnClickListener {
            getPromoterProfile()
        }

        promoterProfileObserver()
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
            /*if (options[item] == "Take Photo") {

                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

                try {
                    profileImage = createImageFile()
                } catch (ex: IOException) {
                }
                if (profileImage != null) {
                    Log.e("mylog", "Image file not null")
                    val photoURI = FileProvider.getUriForFile(
                        activity,
                        "com.freqwency.promotr",
                        profileImage!!
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_CAMERA)
                }
            } else*/

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

    @Throws(IOException::class)
    fun createImageFile(): File? {
        val timeStamp =
            SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName,
            ".jpg",
            storageDir
        )
        val mCurrentPhotoPath = image.absolutePath
        Log.d("mylog", "Path: $mCurrentPhotoPath")
        return image
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

                    Log.v("Path", "path:  " + path+" "+profileImage)
                    binding.uploadImageProfile.text = getString(R.string.uploaded_successfully)

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

    fun getPromoterProfile() {
        if (binding.etNickname.text.isNullOrEmpty()) {
            errorDialogs(getString(R.string.please_enter_nickname))
        }else if (binding.instagramEdt.text.isNullOrEmpty()) {
            errorDialogs(getString(R.string.please_enter_instagram_url))
        } else if (binding.aboutEdt.text.isNullOrEmpty()) {
            errorDialogs(getString(R.string.please_enter_about))
        } else {
            val instagram = binding.instagramEdt.text.toString()
            val facebook = binding.facebookEdt.text.toString()
            val abouts = binding.aboutEdt.text.toString()
            var lengths = abouts.length
            if (lengths >= 50) {
                val about = RequestBody.create(MultipartBody.FORM, abouts)
                val nickname = RequestBody.create(MultipartBody.FORM, binding.etNickname.text.toString())
                val instaUrl = RequestBody.create(MultipartBody.FORM, instagram)
                val facebookUrl = RequestBody.create(MultipartBody.FORM, facebook)
                val images = MultipartBody.Part.createFormData(
                    "profile_image",
                    compressedImageFile?.name,
                    RequestBody.create("image/*".toMediaTypeOrNull(), compressedImageFile!!)
                )

                promoterProfileViewModel.getBecomePromoterProfile(
                    progressDialog,
                    activity,
                    images,
                    about,
                    instaUrl,
                    facebookUrl,
                    nickname
                )
            } else {
                errorDialogs(getString(R.string.least_char))
            }
        }
    }

    private fun promoterProfileObserver() {

        promoterProfileViewModel.progressIndicator.observe(this) {
        }

        promoterProfileViewModel.mPromoterProfileResponse.observe(
            this
        ) {
            PromotrApp.encryptedPrefs.promoterId = ""+ it.peekContent()!!.data?.promoterInfo!!.id
            startActivity(Intent(this@PromoOwnerRegisterActivity, MainActivity::class.java))
            finish()
        }

        promoterProfileViewModel.errorResponse.observe(this) {
            ErrorUtil.handlerGeneralError(this@PromoOwnerRegisterActivity, it)
        }
    }

    private fun errorDialogs(str: String) {
        KAlertDialog(this, KAlertDialog.ERROR_TYPE)
            .setTitleText(getString(R.string.promoter_sign_up))
            .setContentText(str)
            .confirmButtonColor(R.color.secondary)
            .setConfirmClickListener(getString(R.string.OK), R.color.secondary_text_color, null)
            .show()
    }
}