package com.freqwency.promotr.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.freqwency.promotr.R
import com.freqwency.promotr.adapter.CountriesAdapter
import com.freqwency.promotr.application.PromotrApp
import com.freqwency.promotr.extensions.vertical
import com.freqwency.promotr.model.CountryModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object DialogsManager {
    @SuppressLint("InflateParams")
    fun openCountryBottomSheet(activity: Activity,  myCallBack: (country: CountryModel) -> Unit) {
        val dialog = BottomSheetDialog(activity)
        // Inflating the layout file
        val view = activity.layoutInflater.inflate(
            R.layout.countries_buttom_sheet,
            null
        )
        val exitIcon = view.findViewById<ImageView>(R.id.exitIcon)
        val countriesRecyclerView = view.findViewById<RecyclerView>(R.id.countriesRecyclerView)

        exitIcon.setOnClickListener {
            dialog.dismiss()
        }


        //Create the countries adapter
        val countriesAdapter = CountriesAdapter(PromotrApp.encryptedPrefs.appLanguage == "ar")
        //Create the Recyclerview Layout Manager

        //Assign Layout Manager to the Recyclerview
        countriesRecyclerView.vertical()
        //Assign the Adapter to the Recyclerview
        countriesRecyclerView.adapter = countriesAdapter
        countriesAdapter.addData(populateCountryList())
        countriesAdapter.countryClick = { country ->
            myCallBack.invoke(country)
            dialog.dismiss()
        }
        // Enable dialog cancellation.
        dialog.setCancelable(true)
        // Setting content view to our view.
        dialog.setContentView(view)
        setPeekHeight(dialog)
        //Show method to display a dialog.
        dialog.show()
    }

    //Design the height of the BottomSheetDialog
    private fun setPeekHeight(dialog: BottomSheetDialog) {
        (dialog as? BottomSheetDialog)?.behavior?.apply {
            isFitToContents = false
            state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    private fun populateCountryList():  ArrayList<CountryModel> {
        val assetsFile = GlobalFunctions.loadJSONFromAssets("CountryCodes.json")
        val gson = Gson()
        return  gson.fromJson(
            assetsFile, object : TypeToken<ArrayList<CountryModel>>() {}.type
        )

    }
}