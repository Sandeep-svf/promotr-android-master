package com.freqwency.promotr.view.search.ui

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.freqwency.promotr.R
import com.freqwency.promotr.application.PromotrApp
import com.freqwency.promotr.databinding.ActivitySearchResultBinding
import com.freqwency.promotr.utils.ErrorUtil
import com.freqwency.promotr.view.search.SearchResultAdapter
import com.freqwency.promotr.view.search.SearchResultCategoryAdapter
import com.freqwency.promotr.view.search.viewmodel.promocodecategory.PromoCodeByCategoryViewModel
import com.freqwency.promotr.view.search.viewmodel.promocodedates.PromoCodeByDatesViewModel
import com.freqwency.promotr.viewmodel.FavoriteViewModel
import com.johncodeos.customprogressdialogexample.CustomProgressDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchResultActivity : AppCompatActivity() {

    val promoCodeByCategoryViewModel: PromoCodeByCategoryViewModel by viewModels()
    val promoCodeByDatesViewModel: PromoCodeByDatesViewModel by viewModels()

    private lateinit var activity: Activity
    private val progressDialog by lazy { CustomProgressDialog(this) }

    private lateinit var favoriteViewModel: FavoriteViewModel
    private var binding: ActivitySearchResultBinding? = null
    var categoryId = 0
    var categoryIdWithSearch: Int? = null
    var dateOfBirth: String? = null
    var discount: String? = null
    var stores: String? = null
    var type = ""
    var catName = ""
    lateinit var byCategoryLayout: RelativeLayout

    // This property is only valid between onCreateView and
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_result)

        //favoriteViewModel = ViewModelProvider(this).get(FavoriteViewModel::class.java)

        binding = ActivitySearchResultBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        activity = this

        PromotrApp.encryptedPrefs.storeName = ""
        PromotrApp.encryptedPrefs.dateOfBirth = ""
        PromotrApp.encryptedPrefs.discount=""
        PromotrApp.encryptedPrefs.category = 0

        if (intent.hasExtra("categoryId")) {
            categoryId = intent.getIntExtra("categoryId", 0).toInt()
            type = intent.getStringExtra("type").toString()
            catName = intent.getStringExtra("catName").toString()
        }

        if (type.equals("search")) {
            discount = intent.getStringExtra("discount").toString()

            stores = intent.getStringExtra("stores").toString()
            //discount = intent.getDoubleExtra("discount",0.0).toDouble()
            dateOfBirth = intent.getStringExtra("dateOfBirth").toString()
            categoryIdWithSearch = intent.getIntExtra("categoryIdWithSearch", 0).toInt()
            getPromoCodeBySearch()
            binding!!.byCategoryLayout.visibility = View.GONE
            binding!!.bySearchLayout.visibility = View.VISIBLE
        } else {
            binding!!.byCategoryLayout.visibility = View.VISIBLE
            binding!!.bySearchLayout.visibility = View.GONE
            binding!!.categoryNameTxt.text = catName
            getPromoCodeByCategorys()
        }
        Log.e(
            "sliders",
            "  Ids:   " + categoryIdWithSearch + "   Type: " + type + "   CatN: " + catName + "  Dis:  " + discount + "  Date: " + dateOfBirth
        )

        binding!!.byCategoryClose.setOnClickListener {
            finish()
        }
        binding!!.btnClose.setOnClickListener {
            finish()
        }
        binding!!.searchView.setOnClickListener {
            finish()
        }


        binding!!.storesBtn.setOnClickListener {
            stores = ""
            getPromoCodeBySearch()
        }

        binding!!.categoryBtn.setOnClickListener {
            categoryIdWithSearch = 0
            getPromoCodeBySearch()
        }

        binding!!.discountBtn.setOnClickListener {
            discount = ""
            getPromoCodeBySearch()
        }

        binding!!.dateBtn.setOnClickListener {
            dateOfBirth = ""
            getPromoCodeBySearch()
        }


        val suggestions: RecyclerView = binding!!.rcvfavoritess
        suggestions.setHasFixedSize(true)
        // Creating an instance of our NameAdapter class and setting it to our RecyclerView


        promoCodeByCategoryObserver()

        promoCodeBySearchObserver()
    }

    private fun promoCodeByCategoryObserver() {
        promoCodeByCategoryViewModel.progressIndicator.observe(this@SearchResultActivity, Observer {
        })
        promoCodeByCategoryViewModel.mPromocodeByCategoryResponse.observe(
            this@SearchResultActivity
        ) {
            val list = it.peekContent().data?.category?.promoCodes

            Log.e("promoterId", "+Id:    " + list!!.size)

            binding!!.rcvfavoritess.isVerticalFadingEdgeEnabled = true
            binding!!.rcvfavoritess.isVerticalScrollBarEnabled = true
            binding!!.rcvfavoritess.layoutManager = LinearLayoutManager(this@SearchResultActivity)

            binding!!.rcvfavoritess.adapter = SearchResultCategoryAdapter(this@SearchResultActivity, list)
        }

        promoCodeByCategoryViewModel.errorResponse.observe(this@SearchResultActivity) {
            ErrorUtil.handlerGeneralError(this@SearchResultActivity, it)
        }
    }

    fun getPromoCodeByCategorys() {
        promoCodeByCategoryViewModel.getpromoCodeByCategory(
            progressDialog,
            activity,
            categoryId
        )
    }


    fun getPromoCodeBySearch() {
        try {
            if (stores.equals("")) {
                stores = null
                PromotrApp.encryptedPrefs.storeName = "null"
                binding!!.storisLauout.visibility = View.GONE
            } else {
                binding!!.storesTxt.text = stores
            }

            if (categoryIdWithSearch == 0) {
                categoryIdWithSearch = null
                PromotrApp.encryptedPrefs.category = -1
                binding!!.categoryLauout.visibility = View.GONE
            } else {
                binding!!.categoryTxt.text = catName
            }

            if (discount.equals("")) {
                discount = null
                PromotrApp.encryptedPrefs.discount = "null"
                binding!!.discoutLauout.visibility = View.GONE
            } else {
                binding!!.discoutTxt.text = discount
            }

            if (dateOfBirth.equals("")) {
                dateOfBirth = null
                PromotrApp.encryptedPrefs.dateOfBirth = "null"
                binding!!.dateLauout.visibility = View.GONE
            } else {
                binding!!.dateTxt.text = dateOfBirth
            }

            // promoCodeByDatesViewModel.getpromoCodeByDates(progressDialog, activity, categoryId, dateOfBirth, discount.toDouble())
            promoCodeByDatesViewModel.getpromoCodeByDates(
                progressDialog, activity, categoryIdWithSearch, dateOfBirth,
                discount?.toDouble()
            )
        } catch (fg: NumberFormatException) {
        }
    }

    private fun promoCodeBySearchObserver() {
        promoCodeByDatesViewModel.progressIndicator.observe(this@SearchResultActivity, Observer {
        })
        promoCodeByDatesViewModel.mPromocodeByDatesResponse.observe(
            this@SearchResultActivity
        ) {
            val list = it.peekContent().data?.promoCodes
            Log.e("promoterId", "+Ids:    " + list!!.size)
            binding!!.rcvfavoritess.isVerticalFadingEdgeEnabled = true
            binding!!.rcvfavoritess.isVerticalScrollBarEnabled = true
            binding!!.rcvfavoritess.layoutManager = LinearLayoutManager(this@SearchResultActivity)

            binding!!.rcvfavoritess.adapter = SearchResultAdapter(this@SearchResultActivity, list)
        }

        promoCodeByDatesViewModel.errorResponse.observe(this@SearchResultActivity) {
            ErrorUtil.handlerGeneralError(this@SearchResultActivity, it)
        }
    }
}