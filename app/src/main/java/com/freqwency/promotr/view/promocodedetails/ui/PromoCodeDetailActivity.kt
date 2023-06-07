package com.freqwency.promotr.view.promocodedetails.ui

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.developer.kalert.KAlertDialog
import com.freqwency.promotr.R
import com.freqwency.promotr.application.PromotrApp
import com.freqwency.promotr.databinding.ActivityPromoCodeDetailBinding
import com.freqwency.promotr.utils.ErrorUtil
import com.freqwency.promotr.view.promocodedetails.viewmodel.promoCodeDeleteFav.PromoCodeDeleteFavViewModel
import com.freqwency.promotr.view.promocodedetails.viewmodel.promoCodeDetails.PromoCodeDetailsViewModel
import com.freqwency.promotr.view.promocodedetails.viewmodel.promoCodeSaveFav.PromoCodeSaveFavViewModel
import com.johncodeos.customprogressdialogexample.CustomProgressDialog
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


@AndroidEntryPoint
class PromoCodeDetailActivity : AppCompatActivity() {
    private var promoBinding: ActivityPromoCodeDetailBinding? = null
    val promoCodeByCategoryViewModel: PromoCodeDetailsViewModel by viewModels()
    val promoCodeSaveFavViewModel: PromoCodeSaveFavViewModel by viewModels()
    val promoCodeDeleteFavViewModel: PromoCodeDeleteFavViewModel by viewModels()
    private lateinit var activity: Activity
    private val progressDialog by lazy { CustomProgressDialog(this) }
    var categoryId = 0
    var promoCodeCategoryId = 0
    var store_website_url = ""
    var isFavorites: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        promoBinding = ActivityPromoCodeDetailBinding.inflate(layoutInflater)
        setContentView(promoBinding!!.root)

        activity = this

        if (intent.hasExtra("categoryId")) {
            categoryId = intent.getIntExtra("categoryId", 0)
        }

        promoBinding!!.backBtn.setOnClickListener {
            finish()
        }

        promoBinding!!.copyBtn.setOnClickListener {
            copyTextToClipboard()
        }

        promoBinding!!.openLinkBtn.setOnClickListener {
            // val uri: Uri = Uri.parse("http://www.google.com")
            try {
                if (store_website_url.equals("")) {
                } else {
                    var uri: Uri = Uri.parse(store_website_url)

                    if (!store_website_url.startsWith("http://") && !store_website_url.startsWith("https://")) {
                        uri = Uri.parse("http://$store_website_url")
                    }

                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    if (intent.resolveActivity(packageManager) != null) {
                        startActivity(intent)
                    }
                }
            } catch (e: Exception) {
                //Log.e("promoDetails", e.toString())
            }
        }

        promoBinding!!.favoriteBtn.setOnClickListener {
            val token = PromotrApp.encryptedPrefs.bearerToken
            if (token.equals("")) {
                errorDialogs()
            } else {
                if (isFavorites) {
                    getPromoCodeDeleteFav()
                } else {
                    getPromoCodeSaveFav()
                }
            }
        }

        getPromoCodeDetails()
        promoCodeDetailsObserver()
        promoCodeSaveFavObserver()
        promoCodeDeleteFavObserver()
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
        promoCodeByCategoryViewModel.getpromoCodeDetails(
            progressDialog,
            activity,
            categoryId
        )
    }

    private fun promoCodeDetailsObserver() {
        promoCodeByCategoryViewModel.progressIndicator.observe(
            this@PromoCodeDetailActivity,
            Observer {
            })
        promoCodeByCategoryViewModel.mPromocodeDetailsResponse.observe(
            this@PromoCodeDetailActivity
        ) {
            store_website_url = it.peekContent().data?.promocode?.store_website_url!!
            promoBinding?.promoCodetxt?.text = it.peekContent().data?.promocode?.code_id
            promoBinding?.termsConditiontxt?.text = it.peekContent().data?.promocode?.description
            promoBinding?.storeTxt?.text = it.peekContent().data?.promocode?.store_website_url
            promoCodeCategoryId = it.peekContent().data?.promocode?.id!!
            isFavorites = it.peekContent().data?.promocode?.isFavorite!!

            val expiry_dates = it.peekContent().data?.promocode?.expiry_date

            val inputFormatter: DateTimeFormatter =
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.ENGLISH)
            val outputFormatter: DateTimeFormatter =
                DateTimeFormatter.ofPattern("EEEE, dd MMM yyyy", Locale.ENGLISH)
            val date: LocalDate = LocalDate.parse(expiry_dates, inputFormatter)
            val formattedDate: String = outputFormatter.format(date)
            promoBinding?.expireDatetxt?.text = formattedDate

            if (it.peekContent().data?.promocode?.isFavorite == true) {
                Glide
                    .with(activity)
                    .load(R.drawable.fav_selected)
                    .into(promoBinding!!.favoriteBtn)

                val button = findViewById<View>(R.id.favoriteBtn) as ImageButton
                button.setColorFilter(getColor(R.color.secondary)) // White Tint
            }
            else
            {
                Glide
                    .with(activity)
                    .load(R.drawable.favorites)
                    .into(promoBinding!!.favoriteBtn)
                val button = findViewById<View>(R.id.favoriteBtn) as ImageButton
                button.setColorFilter(Color.argb(255, 255, 255, 255)) // White Tint
            }
        }

        promoCodeByCategoryViewModel.errorResponse.observe(this@PromoCodeDetailActivity) {
            ErrorUtil.handlerGeneralError(this@PromoCodeDetailActivity, it)
        }
    }


    /**
     * PromoCode Save Favourite
     */
    fun getPromoCodeSaveFav() {
        promoCodeSaveFavViewModel.getpromoCodeSaveFav(
            progressDialog,
            activity,
            promoCodeCategoryId
        )
    }

    private fun promoCodeSaveFavObserver() {
        promoCodeSaveFavViewModel.progressIndicator.observe(
            this@PromoCodeDetailActivity,
            Observer {
            })
        promoCodeSaveFavViewModel.mPromocodeSaveFavResponse.observe(
            this@PromoCodeDetailActivity
        ) {
            /*Glide
                .with(activity)
                .load(R.drawable.fav_selected)
                .into(promoBinding!!.favoriteBtn)
            val button = findViewById<View>(R.id.favoriteBtn) as ImageButton
            button.setColorFilter(Color.argb(255, 255, 255, 255)) // White Tint*/

            getPromoCodeDetails()
        }

        promoCodeSaveFavViewModel.errorResponse.observe(this@PromoCodeDetailActivity) {
            ErrorUtil.handlerGeneralError(this@PromoCodeDetailActivity, it)
        }
    }


    /**
     * PromoCode Delete Favourite
     */
    fun getPromoCodeDeleteFav() {
        promoCodeDeleteFavViewModel.getDeleteFavouritePromoCode(
            progressDialog,
            activity,
            promoCodeCategoryId
        )
    }

    private fun promoCodeDeleteFavObserver() {
        promoCodeDeleteFavViewModel.progressIndicator.observe(
            this@PromoCodeDetailActivity,
            Observer {
            })
        promoCodeDeleteFavViewModel.mPromocodeDeleteFavResponse.observe(
            this@PromoCodeDetailActivity
        ) {
            Glide
                .with(activity)
                .load(R.drawable.fav_selected)
                .into(promoBinding!!.favoriteBtn)
            //val button = findViewById<View>(R.id.favoriteBtn) as ImageButton
            //button.setColorFilter(Color.argb(255, 255, 255, 255)) // White Tint

            getPromoCodeDetails()
        }

        promoCodeDeleteFavViewModel.errorResponse.observe(this@PromoCodeDetailActivity) {
            ErrorUtil.handlerGeneralError(this@PromoCodeDetailActivity, it)
        }
    }


}