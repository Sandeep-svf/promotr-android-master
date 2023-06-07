package com.freqwency.promotr.view.promocodedetails.ui

import android.app.Activity
import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import com.developer.kalert.KAlertDialog
import com.freqwency.promotr.R
import com.freqwency.promotr.databinding.ActivityPromoCodeDetailPromoterBinding
import com.freqwency.promotr.utils.ErrorUtil
import com.freqwency.promotr.view.AddPromoCode.ui.AddNewPromoCodeActivity
import com.freqwency.promotr.view.AddPromoCode.viewmodel.DeletePromoCode.DeletePromoCodeViewModel
import com.freqwency.promotr.view.dashboard.viewmodel.category.CategoryResponse
import com.freqwency.promotr.view.dashboard.viewmodel.category.CategoryViewModel
import com.freqwency.promotr.view.promocodedetails.viewmodel.promoCodeDetails.PromoCodeDetailsViewModel
import com.johncodeos.customprogressdialogexample.CustomProgressDialog
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@AndroidEntryPoint
class PromoCodeDetailPromoterActivity : AppCompatActivity() {

    private var promoBinding: ActivityPromoCodeDetailPromoterBinding? = null
    val categoryViewModel: CategoryViewModel by viewModels()
    val deletePromoCodeViewModel: DeletePromoCodeViewModel by viewModels()
    val promoCodeByCategoryViewModel: PromoCodeDetailsViewModel by viewModels()
    var catList : ArrayList<CategoryResponse.Categorys> = ArrayList()
    private lateinit var activity: Activity
    private val progressDialog by lazy { CustomProgressDialog(this) }
    var categoryId = 0
    var promoCodeCategoryId = 0
    var store_website_url = ""
    var categoryName = ""
    lateinit var dialogs: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        promoBinding = ActivityPromoCodeDetailPromoterBinding.inflate(layoutInflater)
        setContentView(promoBinding!!.root)

        activity = this

        if (intent.hasExtra("categoryId")) {
            categoryId = intent.getIntExtra("categoryId", 0)
        }

        promoBinding!!.backBtn.setOnClickListener {
            finish()
        }

        promoBinding!!.deleteBtn.setOnClickListener {
            DeletePromoCode_AlertDialog()
        }

        promoBinding!!.editBtn.setOnClickListener {
            val intent = Intent(activity, AddNewPromoCodeActivity::class.java)
            intent.putExtra("categoryName", categoryName)
            intent.putExtra("categoryId", categoryId)
            intent.putExtra("actions", catList)
            intent.putExtra("method", "edit")
            activity.startActivity(intent)
        }

        promoBinding!!.copyBtn.setOnClickListener {
            copyTextToClipboard()
        }

        promoBinding!!.openLinkBtn.setOnClickListener {
            // val uri: Uri = Uri.parse("http://www.google.com")
            try {
                if (store_website_url.equals("")) {
                } else {
                    val uri: Uri = Uri.parse(store_website_url)
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    startActivity(intent)
                }
            }catch (e : Exception)
            {}
        }

        getPromoCodeDetails()
        promoCodeDetailsObserver()
        categoryObserver()
        deletePromoCodesObserver()
    }

    private fun errorDialogs() {
        KAlertDialog(this, KAlertDialog.ERROR_TYPE)
            .setTitleText(getString(R.string.login_error))
            .setContentText(getString(R.string.login_to_add_favorites))
            .confirmButtonColor(R.color.secondary)
            .setConfirmClickListener(getString(R.string.OK), R.color.secondary_text_color, null)
            .show()
    }

    private fun copyTextToClipboard() {
        val textToCopy = promoBinding!!.promoCodetxt.text
        val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("text", textToCopy)
        clipboardManager.setPrimaryClip(clipData)
        Toast.makeText(this, "Text copied to clipboard", Toast.LENGTH_LONG).show()
    }

    private fun pasteTextFromClipboard() {
        val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        promoBinding!!.storeTxt.text = clipboardManager.primaryClip?.getItemAt(0)?.text
    }


    /**
     * PromoCode Details
     */
    fun getPromoCodeDetails() {
        promoCodeByCategoryViewModel.getpromoCodeDetails(progressDialog, activity, categoryId)
        categoryViewModel.getCategory(progressDialog, activity)
    }
    private fun promoCodeDetailsObserver() {
        promoCodeByCategoryViewModel.progressIndicator.observe(this@PromoCodeDetailPromoterActivity, androidx.lifecycle.Observer {
        })
        promoCodeByCategoryViewModel.mPromocodeDetailsResponse.observe(
            this@PromoCodeDetailPromoterActivity
        ) {
            store_website_url = it.peekContent().data?.promocode?.store_website_url!!
            promoBinding?.promoCodetxt?.text = it.peekContent().data?.promocode?.code_id
            promoBinding?.termsConditiontxt?.text = it.peekContent().data?.promocode?.description
            promoBinding?.storeTxt?.text = it.peekContent().data?.promocode?.store_website_url
            promoCodeCategoryId = it.peekContent().data?.promocode?.id!!

            val expiry_dates = it.peekContent().data?.promocode?.expiry_date
            categoryName = it.peekContent().data?.promocode?.category?.name!!

            val inputFormatter: DateTimeFormatter =
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.ENGLISH)
            val outputFormatter: DateTimeFormatter =
                DateTimeFormatter.ofPattern("EEEE, dd MMM yyyy", Locale.ENGLISH)
            val date: LocalDate = LocalDate.parse(expiry_dates, inputFormatter)
            val formattedDate: String = outputFormatter.format(date)
            promoBinding?.expireDatetxt?.text = formattedDate
        }

        promoCodeByCategoryViewModel.errorResponse.observe(this@PromoCodeDetailPromoterActivity) {
            ErrorUtil.handlerGeneralError(this@PromoCodeDetailPromoterActivity, it)
        }
    }


    /**
     * Category List
     */
    private fun categoryObserver() {
        categoryViewModel.progressIndicator.observe(this, androidx.lifecycle.Observer {
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

    override fun onResume() {
        super.onResume()
        getPromoCodeDetails()
    }

    private fun DeletePromoCode_AlertDialog() {
        val inflater = this@PromoCodeDetailPromoterActivity.layoutInflater
        val alertLayout: View = inflater.inflate(R.layout.custom_alert_dialog, null)
        val tvMessage = alertLayout.findViewById<TextView>(R.id.textViewMessage)
        val btnDelete = alertLayout.findViewById<TextView>(R.id.btnd_delete)
        val btncancel = alertLayout.findViewById<TextView>(R.id.btn_cancel)
        val alert = AlertDialog.Builder(activity)

        alert.setTitle(getString(R.string.confirm_delete_promocode))
        tvMessage.text = getString(R.string.are_you_sure_to_delete_promocode)
        alert.setView(alertLayout)
        alert.setCancelable(false)
        btncancel.setOnClickListener { v: View? ->
            dialogs.dismiss()
            //relative_logout.setEnabled(true)
        }
        btnDelete.setOnClickListener { v: View? ->
            // relative_logout.setEnabled(true)
            dialogs.dismiss()
            DeletePromoCode()
        }
        dialogs = alert.create()
        dialogs.show()
    }

    /**
     * Delete Promocode
     */
    private fun DeletePromoCode() {
        deletePromoCodeViewModel.getDeletePromoCodeForPromoter(
            progressDialog,
            activity,
            categoryId
        )
    }
    private fun deletePromoCodesObserver() {
        deletePromoCodeViewModel.progressIndicator.observe(this, androidx.lifecycle.Observer {
        })
        deletePromoCodeViewModel.mDeletePromoCodeResponse.observe(
            this
        ) {
            //startActivity(Intent(this@AddNewPromoCodeActivity, MainActivity::class.java))
            finish()
        }

        deletePromoCodeViewModel.errorResponse.observe(this) {
            ErrorUtil.handlerGeneralError(this, it)
        }
    }
}