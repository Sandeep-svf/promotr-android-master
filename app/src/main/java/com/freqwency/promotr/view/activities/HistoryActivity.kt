package com.freqwency.promotr.view.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.freqwency.promotr.databinding.ActivityHistoryBinding
import com.freqwency.promotr.utils.ErrorUtil
import com.freqwency.promotr.view.AddPromoCode.viewmodel.ViewPromoCode.ViewPromoCodeViewModel
import com.freqwency.promotr.view.dashboard.adapters.PromoCodesAdapter
import com.johncodeos.customprogressdialogexample.CustomProgressDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoryActivity : AppCompatActivity() {
    lateinit var binding: ActivityHistoryBinding
    val viewPromoCodeViewModel: ViewPromoCodeViewModel by viewModels()
    private val progressDialog by lazy { CustomProgressDialog(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val suggestions: RecyclerView = binding.rcvhistory
        suggestions.setHasFixedSize(true)

        binding.backBtn.setOnClickListener {
          finish()
        }

        promoCodeObserver()
    }

    private fun promoCodeObserver() {
        viewPromoCodeViewModel.progressIndicator.observe(this, Observer {
        })
        viewPromoCodeViewModel.mViewPromoCodeResponse.observe(
            this
        ) {
            val list = it.peekContent().data?.promoCodes

            binding.rcvhistory.isHorizontalScrollBarEnabled = true
            binding.rcvhistory.isHorizontalFadingEdgeEnabled = true
            //binding.rcvongoing.layoutManager = GridLayoutManager(activity, 3, GridLayoutManager.HORIZONTAL, false)
            binding.rcvhistory.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
            binding.rcvhistory.adapter = PromoCodesAdapter(this, list)
        }

        viewPromoCodeViewModel.errorResponse.observe(this) {
            ErrorUtil.handlerGeneralError(this, it)
        }
    }

    override fun onResume() {
        super.onResume()
        viewPromoCodeViewModel.getViewPromoCodeForPromoter(progressDialog, this)
    }
}