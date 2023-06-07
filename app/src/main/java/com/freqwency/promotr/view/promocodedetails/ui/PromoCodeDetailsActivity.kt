package com.freqwency.promotr.view.promocodedetails.ui

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.freqwency.promotr.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PromoCodeDetailsActivity : AppCompatActivity() {
    private lateinit var activity: Activity


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_promo)

        activity = this

    }

}