package com.freqwency.promotr.view.promoterprofile.ui

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.developer.kalert.KAlertDialog
import com.freqwency.promotr.R
import com.freqwency.promotr.application.PromotrApp
import com.freqwency.promotr.databinding.ActivityPromoterProfileBinding
import com.freqwency.promotr.utils.ErrorUtil
import com.freqwency.promotr.view.promoterprofile.adapters.PromoterPromoCodeAdapter
import com.freqwency.promotr.view.promoterprofile.viewmodel.promoterDeleteFav.PromoterDeleteFavViewModel
import com.freqwency.promotr.view.promoterprofile.viewmodel.promoterListById.PromoterListByIdResponse
import com.freqwency.promotr.view.promoterprofile.viewmodel.promoterListById.PromotersListByIdViewModel
import com.freqwency.promotr.view.promoterprofile.viewmodel.promoterSaveFav.PromoterBody
import com.freqwency.promotr.view.promoterprofile.viewmodel.promoterSaveFav.PromoterSaveFavViewModel
import com.johncodeos.customprogressdialogexample.CustomProgressDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PromoterProfileActivity : AppCompatActivity() {
    private var promoterProfileBinding: ActivityPromoterProfileBinding? = null
    private lateinit var promoterBody: PromoterBody

    private lateinit var activity: Activity

    val promoterDeleteFavViewModel: PromoterDeleteFavViewModel by viewModels()
    val promoterSaveFavViewModel: PromoterSaveFavViewModel by viewModels()
    val promotersListByIdViewModel: PromotersListByIdViewModel by viewModels()
    private val progressDialog by lazy { CustomProgressDialog(this) }

    var promoterPromoList: ArrayList<PromoterListByIdResponse.Promocodes> = ArrayList()

    var promoterId = 0
    var isFavorites: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        promoterProfileBinding = ActivityPromoterProfileBinding.inflate(layoutInflater)
        setContentView(promoterProfileBinding!!.root)

        activity = this

        if (intent.hasExtra("promoterId")) {
            promoterId = intent.getIntExtra("promoterId", 0)
        }

        val suggestions: RecyclerView = promoterProfileBinding!!.rcvPromocode
        suggestions.setHasFixedSize(true)

        promoterProfileBinding!!.favoriteBtn.setOnClickListener {

            val token = PromotrApp.encryptedPrefs.bearerToken
            if (token.equals("")) {
                errorDialogs()
            } else {
                if (isFavorites) {
                    promoterDeleteFav()

                } else {
                    promoterSaveFav()
                }
            }
        }

        promoterProfileBinding!!.backBtn.setOnClickListener {
            finish()
        }

        PromotersListByIdObserver()
        // PromotersListById()
        promoterSaveFavObserver()
        promoterDeleteFavObserver()
    }

    private fun errorDialogs() {
        KAlertDialog(this, KAlertDialog.ERROR_TYPE)
            .setTitleText(getString(R.string.login_error))
            .setContentText(getString(R.string.login_to_add_favorites))
            .confirmButtonColor(R.color.secondary)
            .setConfirmClickListener(getString(R.string.OK), R.color.secondary_text_color, null)
            .show()
    }

    /**
     * Promoter List By Id
     */
    private fun PromotersListById() {
        promotersListByIdViewModel.getPromotersListById(progressDialog, activity, promoterId)
    }
    private fun PromotersListByIdObserver() {
        promotersListByIdViewModel.progressIndicator.observe(
            this@PromoterProfileActivity,
            Observer {
            })
        promotersListByIdViewModel.mPromoterListByIdResponse.observe(
            this@PromoterProfileActivity
        ) {
            promoterPromoList = it.peekContent().data?.promoter?.promocodes!!
            var about = it.peekContent().data?.promoter!!.about
            var first_name = it.peekContent().data?.promoter!!.user?.first_name
            var last_name = it.peekContent().data?.promoter!!.user?.last_name
            isFavorites = it.peekContent().data?.promoter!!.isFavorite!!

            if (it.peekContent().data?.promoter?.isFavorite == true) {
                Glide
                    .with(activity)
                    .load(R.drawable.fav_selected)
                    .into(promoterProfileBinding!!.favoriteBtn)
                val button = findViewById<View>(R.id.favoriteBtn) as ImageButton
                button.setColorFilter(getColor(R.color.secondary)) // White Tint
            } else
            {
                Glide
                    .with(activity)
                    .load(R.drawable.favorites)
                    .into(promoterProfileBinding!!.favoriteBtn)
                val button = findViewById<View>(R.id.favoriteBtn) as ImageButton
                button.setColorFilter(Color.argb(255, 255, 255, 255)) // White Tint
            }

            val url = it.peekContent().data?.promoter!!.full_image_url
            Glide
                .with(activity)
                .load(url)
                .placeholder(R.drawable.ic_promo)
                .into(promoterProfileBinding!!.imageView)

            promoterProfileBinding!!.nameTxt.text = first_name + " " + last_name
            promoterProfileBinding!!.aboutTxt.text = about

            promoterProfileBinding!!.rcvPromocode.isVerticalFadingEdgeEnabled = true
            promoterProfileBinding!!.rcvPromocode.isVerticalScrollBarEnabled = true
            promoterProfileBinding!!.rcvPromocode.layoutManager =
                LinearLayoutManager(activity)
            promoterProfileBinding!!.rcvPromocode.adapter =
                PromoterPromoCodeAdapter(activity, promoterPromoList)

        }

        promotersListByIdViewModel.errorResponse.observe(this@PromoterProfileActivity) {
            ErrorUtil.handlerGeneralError(this@PromoterProfileActivity, it)
        }
    }



    /**
     * Promoter Save Favourite
     */
    private fun promoterSaveFav() {
        promoterBody = PromoterBody(
            promoter_id = promoterId
        )
        promoterSaveFavViewModel.getpromoterSaveFav(progressDialog, activity, promoterBody)
    }
    private fun promoterSaveFavObserver() {
        promoterSaveFavViewModel.progressIndicator.observe(
            this@PromoterProfileActivity,
            Observer {
            })
        promoterSaveFavViewModel.mPromoterSaveFavResponse.observe(
            this@PromoterProfileActivity
        ) {
            /*Glide
                .with(activity)
                .load(R.drawable.fav_selected)
                .into(promoterProfileBinding!!.favoriteBtn)*/
            PromotersListById()

        }

        promoterSaveFavViewModel.errorResponse.observe(this@PromoterProfileActivity) {
            ErrorUtil.handlerGeneralError(this@PromoterProfileActivity, it)
        }
    }


    /**
     * Promoter Delete Favourite
     */
    private fun promoterDeleteFav() {
        promoterDeleteFavViewModel.getdeleteFavouritePromoter(progressDialog, activity, promoterId)
    }
    private fun promoterDeleteFavObserver() {
        promoterDeleteFavViewModel.progressIndicator.observe(
            this@PromoterProfileActivity,
            Observer {
            })
        promoterDeleteFavViewModel.mPromoterDeleteFavResponse.observe(
            this@PromoterProfileActivity
        ) {
            Glide
                .with(activity)
                .load(R.drawable.fav_selected)
                .into(promoterProfileBinding!!.favoriteBtn)
            PromotersListById()
        }

        promoterDeleteFavViewModel.errorResponse.observe(this@PromoterProfileActivity) {
            ErrorUtil.handlerGeneralError(this@PromoterProfileActivity, it)
        }
    }


    override fun onResume() {
        super.onResume()
        promotersListByIdViewModel.getPromotersListById(progressDialog, activity, promoterId)
    }
}