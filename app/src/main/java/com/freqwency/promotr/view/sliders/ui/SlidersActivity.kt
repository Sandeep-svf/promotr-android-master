package com.freqwency.promotr.view.sliders.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.WindowCompat
import androidx.viewpager.widget.ViewPager
import com.freqwency.promotr.R
import com.freqwency.promotr.adapter.ViewPagerAdapter
import com.freqwency.promotr.application.PromotrApp
import com.freqwency.promotr.databinding.ActivityIntroBinding
import com.freqwency.promotr.utils.GlobalFunctions
import com.freqwency.promotr.utils.serializable
import com.freqwency.promotr.view.activities.BaseActivity
import com.freqwency.promotr.view.activities.MainActivity
import com.freqwency.promotr.view.logins.ui.LoginActivity
import com.freqwency.promotr.view.register.ui.RegisterActivity
import com.freqwency.promotr.model.GetSlidersResponse
import kotlin.collections.ArrayList

class SlidersActivity : BaseActivity(), View.OnClickListener {

    private lateinit var activity: Activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val introBinding: ActivityIntroBinding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(introBinding.root)

        activity = this

        GlobalFunctions.addUIPadding(introBinding.llBackground)
        val sliders = intent.serializable<ArrayList<GetSlidersResponse.SliderBean>>("sliders")
        val slidersList= ArrayList<GetSlidersResponse.SliderBean>()
        sliders?.forEach { slider ->
            slidersList.add(slider)
        }

        val viewPagerAdapter = ViewPagerAdapter(
            activity,
            slidersList, R.layout.pager_item
        )

        introBinding.viewpager.adapter = viewPagerAdapter
        introBinding.indicator.setViewPager(introBinding.viewpager)

        introBinding.tvSlider.text = slidersList[0].title
        introBinding.tvSubtitle.text = slidersList[0].subtitle

        introBinding.viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                introBinding.tvSlider.text = slidersList[position].title
                introBinding.tvSubtitle.text = slidersList[position].subtitle
            }
        })

        introBinding.btnSignIn.setOnClickListener(this)
        introBinding.btnSignUp.setOnClickListener(this)
        introBinding.skipBtn.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_signIn -> {
                val intent = Intent(activity, LoginActivity::class.java)
                activity.startActivity(intent)
            }

            R.id.btn_signUp -> {
                val intent = Intent(activity, RegisterActivity::class.java)
                activity.startActivity(intent)
            }

            R.id.skipBtn -> {
                PromotrApp.encryptedPrefs.bearerToken = ""
                PromotrApp.encryptedPrefs.promoterId = ""
                PromotrApp.encryptedPrefs.userId = ""
                PromotrApp.encryptedPrefs.isFirstTime = false
                val intent = Intent(activity, MainActivity::class.java)
                activity.startActivity(intent)
                activity.finish()

            }
        }
    }
}