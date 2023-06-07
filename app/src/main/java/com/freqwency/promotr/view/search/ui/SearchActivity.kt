package com.freqwency.promotr.view.search.ui

import android.R
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import com.freqwency.promotr.application.PromotrApp
import com.freqwency.promotr.databinding.ActivitySearchBinding
import com.freqwency.promotr.utils.serializable
import com.freqwency.promotr.view.activities.BaseActivity
import com.freqwency.promotr.view.dashboard.viewmodel.category.CategoryResponse
import com.skydoves.powerspinner.IconSpinnerAdapter
import com.skydoves.powerspinner.IconSpinnerItem
import java.util.*
import kotlin.collections.ArrayList

class SearchActivity : BaseActivity() {

    var list: List<String> = java.util.ArrayList()
    var cal = Calendar.getInstance()
    var dateOfBirth = ""
    var discount = ""
    var categoryId = 0
    var catName = ""
    lateinit var searchBinding: ActivitySearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        searchBinding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(searchBinding.root)

        //val sliders = intent.getSerializableExtra("actions") as ArrayList<CategoryResponse.Categorys>
        val sliders = intent.serializable<ArrayList<CategoryResponse.Categorys>>("actions")
        Log.e("sliders", " " + sliders!![0].id)

        searchBinding.btnClose.setOnClickListener {
            finish()
        }

        searchBinding.spinnerNew.setOnItemSelectedListener(object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                (parent.getChildAt(0) as TextView).setTextColor(Color.BLACK)
                val item: String = searchBinding.spinnerNew.getSelectedItem().toString()
                if (item == "Choose") {
                } else {
                    categoryId = sliders[position].id
                    catName = sliders[position].name
                    Log.e("sliders", " idd  " + categoryId)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })

        list = java.util.ArrayList<String>()
        for (i in sliders.indices) {
            val category_name: String = sliders[i].name
            (list as java.util.ArrayList<String>).add(category_name)
        }

        //Create an ArrayAdapter
        //val countryNameSpinnerAdapter = SpinnerAdapter(this, R.layout.simple_spinner_item, list)
        val countryNameSpinnerAdapter = SpinnerAdapter(this, com.freqwency.promotr.R.layout.spinner_item, list)
        countryNameSpinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        countryNameSpinnerAdapter.addAll(list)
        countryNameSpinnerAdapter.add("Choose")
        searchBinding.spinnerNew.setAdapter(countryNameSpinnerAdapter)
        searchBinding.spinnerNew.setSelection(countryNameSpinnerAdapter.getCount())

        /*val aa = ArrayAdapter(this, android.R.layout.simple_spinner_item, list)
       aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
       // Set Adapter to Spinner
       searchBinding.spinnerNew!!.setAdapter(aa)*/

        /*  searchBinding.spinnerViewCategory.apply {
              setSpinnerAdapter(IconSpinnerAdapter(this))
              setItems(
                  arrayListOf(
                      IconSpinnerItem(text = "Shoe"),
                      IconSpinnerItem(text = "Restaurants"),
                      IconSpinnerItem(text = "Home"),
                      IconSpinnerItem(text = "Addidas"),
                      IconSpinnerItem(text = "Building"),
                  )
              )
              setOnSpinnerItemSelectedListener<IconSpinnerItem> { _, _, _, item ->
              }
              getSpinnerRecyclerView().layoutManager = GridLayoutManager(baseContext, 1)
          }*/

        searchBinding.spinnerViewDiscount.apply {
            setSpinnerAdapter(IconSpinnerAdapter(this))
            setItems(
                arrayListOf(
                    IconSpinnerItem(text = "0.10"),
                    IconSpinnerItem(text = "0.20"),
                    IconSpinnerItem(text = "0.30"),
                    IconSpinnerItem(text = "0.40"),
                    IconSpinnerItem(text = "0.50"),
                )
            )
            setOnSpinnerItemSelectedListener<IconSpinnerItem> { _, _, _, item ->
                discount = item.text as String
            }
            getSpinnerRecyclerView().layoutManager = GridLayoutManager(baseContext, 1)
        }

        searchBinding.datesLayout.setOnClickListener(View.OnClickListener { arg0: View? ->
            val calendar = Calendar.getInstance()
            val year = calendar[Calendar.YEAR]
            val month = calendar[Calendar.MONTH]
            val day = calendar[Calendar.DAY_OF_MONTH]
            val datePicker =
                DatePickerDialog(
                    this@SearchActivity,
                    { view, year, monthOfYear, dayOfMonth ->
                        val month = monthOfYear + 1
                        cal = Calendar.getInstance()
                        cal.set(year, monthOfYear, dayOfMonth)
                        cal.add(Calendar.DATE, 1)

                        searchBinding.dateTxt.setText(
                            String.format(
                                Locale.ENGLISH,
                                "%04d-%02d-%02d",
                                year,
                                month,
                                dayOfMonth
                            )
                        )
//"%02d-%02d-%04d",
                        dateOfBirth = searchBinding.dateTxt.text as String
                    }, year, month, day
                )
            // datePicker.datePicker.maxDate = System.currentTimeMillis()
            datePicker.datePicker.minDate = System.currentTimeMillis()
            datePicker.show()
        })

        val button: Button = searchBinding.btnsearch
        button.setOnClickListener {
            validateInputs()
        }
    }

    private fun validateInputs() {

        var stores = searchBinding.storesTxt.text.toString()
        var dateOfBirths = searchBinding.dateTxt.text.toString()

        val intent = Intent(applicationContext, SearchResultActivity::class.java)
        intent.putExtra("discount", discount)
        intent.putExtra("dateOfBirth", dateOfBirths)
        intent.putExtra("categoryId", categoryId)
        intent.putExtra("categoryIdWithSearch", categoryId)
        intent.putExtra("catName", catName)
        intent.putExtra("type", "search")
        intent.putExtra("stores", stores)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        applicationContext.startActivity(intent)
        finish()
    }

    class SpinnerAdapter(
        context: Context?,
        private val textViewResourceId: Int,
        private val strings: List<String>
    ) :
        ArrayAdapter<String?>(context!!, textViewResourceId) {
        override fun getCount(): Int {
            val count = super.getCount()
            return if (count > 0) {
                count - 1
            } else {
                count
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val storeName = PromotrApp.encryptedPrefs.storeName
        val dateOfBirth = PromotrApp.encryptedPrefs.dateOfBirth
        val discount = PromotrApp.encryptedPrefs.discount
        val category = PromotrApp.encryptedPrefs.category
        Log.e("promoterId", "+Id:    " + " " + storeName)

        if (storeName.equals("null")) {
            searchBinding.storesTxt.text = Editable.Factory.getInstance().newEditable("")
        }

        if (dateOfBirth.equals("null")) {
            searchBinding.dateTxt.text = ""
        }

        if (discount.equals("null")) {
            searchBinding.spinnerViewDiscount.text = ""
            this.discount = ""
        }

        if (category==-1) {
            //Create an ArrayAdapter
           // val countryNameSpinnerAdapter = SpinnerAdapter(this, R.layout.simple_spinner_item, list)
            val countryNameSpinnerAdapter = SpinnerAdapter(this, com.freqwency.promotr.R.layout.spinner_item, list)
            countryNameSpinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
            countryNameSpinnerAdapter.addAll(list)
            countryNameSpinnerAdapter.add("Choose")
            searchBinding.spinnerNew.setAdapter(countryNameSpinnerAdapter)
            searchBinding.spinnerNew.setSelection(countryNameSpinnerAdapter.getCount())
            categoryId = 0
        }
    }
}