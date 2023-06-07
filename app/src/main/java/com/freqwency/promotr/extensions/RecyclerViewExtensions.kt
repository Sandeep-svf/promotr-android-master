package com.freqwency.promotr.extensions

import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import androidx.recyclerview.widget.RecyclerView.HORIZONTAL
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import com.freqwency.promotr.application.PromotrApp

fun RecyclerView.horizontal(snappy: Boolean = false) {
    setHasFixedSize(true)
    isVerticalScrollBarEnabled = false
    isHorizontalScrollBarEnabled = false
    itemAnimator = null
    overScrollMode = View.OVER_SCROLL_NEVER
    layoutManager = LinearLayoutManager(PromotrApp.instance.applicationContext, HORIZONTAL, false)
    if (snappy) {
        this.onFlingListener = null
        val helper: SnapHelper = LinearSnapHelper()
        helper.attachToRecyclerView(this)
    }
}

fun RecyclerView.vertical(snappy: Boolean = false) {
    setHasFixedSize(true)
    itemAnimator = null
    isVerticalScrollBarEnabled = false
    isHorizontalScrollBarEnabled = false
    overScrollMode = View.OVER_SCROLL_NEVER
    layoutManager = LinearLayoutManager(PromotrApp.instance.applicationContext, VERTICAL, false)
    if (snappy) {
        this.onFlingListener = null
        val helper: SnapHelper = LinearSnapHelper()
        helper.attachToRecyclerView(this)
    }
}

fun RecyclerView.gridVertical() {
    setHasFixedSize(true)
    itemAnimator = null
    layoutManager = GridLayoutManager(
        context,
        VERTICAL
    )
}

fun RecyclerView.grid2Span(spanCount: Int = 2) {
    setHasFixedSize(true)
    itemAnimator = null
    layoutManager = GridLayoutManager(
        context, // Context
        spanCount, // Number
    )
}


fun RecyclerView.grid2SpanHorizontal() {
    setHasFixedSize(true)
    itemAnimator = null
    layoutManager = GridLayoutManager(
        context, // Context
        2,// Number
        GridLayoutManager.HORIZONTAL,
        false
    )
}

fun View.setMargin(left: Int? = null, top: Int? = null, right: Int? = null, bottom: Int? = null) {
    val params = (layoutParams as? ViewGroup.MarginLayoutParams)
    params?.setMargins(
        left ?: params.leftMargin,
        top ?: params.topMargin,
        right ?: params.rightMargin,
        bottom ?: params.bottomMargin
    )
    layoutParams = params
}

// for grid
class SpacingItemDecorator (private val padding: Int) : RecyclerView.ItemDecoration()
{
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    )
    {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.top = padding
//        outRect.bottom = padding
        outRect.left = padding
        outRect.right = padding
    }
}