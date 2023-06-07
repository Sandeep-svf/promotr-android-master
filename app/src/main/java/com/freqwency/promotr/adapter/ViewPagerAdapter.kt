package com.freqwency.promotr.adapter

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.freqwency.promotr.R
import com.freqwency.promotr.model.GetSlidersResponse


class ViewPagerAdapter(
    private val activity: Activity,
    private var sliders: ArrayList<GetSlidersResponse.SliderBean>,
    private var inflatedLayout: Int
) : PagerAdapter() {

    private var mLayoutInflater: LayoutInflater =
        activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView = mLayoutInflater.inflate(inflatedLayout, container, false)
        val imageView: ImageView = itemView.findViewById(R.id.image)


        val url = sliders[position].fullImageUrl
        Glide
            .with(activity)
            .load(url)
            .into(imageView)

        Log.e("positions","  Title "+sliders[position].title)

        container.addView(itemView)
        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as RelativeLayout)
    }

    override fun getCount(): Int {
        return sliders.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }
}
