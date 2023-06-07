package com.freqwency.promotr.utils

import android.view.View
import android.view.ViewGroup
import androidx.core.view.*
import com.freqwency.promotr.application.PromotrApp
import com.freqwency.promotr.extensions.navigationBarHeight
import com.johncodeos.customprogressdialogexample.CustomProgressDialog
import java.util.*

object GlobalFunctions {

    fun getDeviceID(): String {
        return UUID.randomUUID().toString()
    }

    // padding as per status bar
    fun addUIPadding(view: View) {
        ViewCompat.setOnApplyWindowInsetsListener(view) { view1, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.statusBars())
            // Apply the insets as a margin to the view. Here the system is setting
            // only the bottom, left, and right dimensions, but apply whichever insets are
            // appropriate to your layout. You can also update the view padding
            // if that's more appropriate.
            view1.updatePadding(top = insets.top + 10, right = insets.right, bottom = insets.bottom)

            // Return CONSUMED if you don't want the window insets to keep being
            // passed down to descendant views.
            windowInsets
        }
    }

    // if you want to update the margins as per the navigation bar
    fun addUIMargin(view: View) {
        ViewCompat.setOnApplyWindowInsetsListener(view) { view1, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.navigationBars())
            view1.updateLayoutParams {
                (this as? ViewGroup.MarginLayoutParams)?.let {
                    updateMargins(
                        left = insets.left,
                        top = insets.top,
                        right = insets.right,
                        bottom = PromotrApp.instance.applicationContext.navigationBarHeight
                    )
                }
            }
            windowInsets
        }
    }

    fun addBottomMargin(view: View) {
        ViewCompat.setOnApplyWindowInsetsListener(view) { view1, windowInsets ->
            view1.updateLayoutParams {
                (this as? ViewGroup.MarginLayoutParams)?.let {
                    updateMargins(
                        bottom = PromotrApp.instance.applicationContext.navigationBarHeight
                    )
                }
            }
            windowInsets
        }
    }

    fun loadJSONFromAssets(fileName: String): String {
        return PromotrApp.instance.applicationContext.assets.open(fileName).bufferedReader()
            .use { reader ->
                reader.readText()
            }
    }

}
