package com.freqwency.promotr.view.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.freqwency.promotr.R
import com.freqwency.promotr.databinding.FragmentPromoterProfileBinding
import com.freqwency.promotr.utils.ErrorUtil
import com.freqwency.promotr.view.AddPromoCode.ui.AddNewPromoCodeActivity
import com.freqwency.promotr.view.AddPromoCode.viewmodel.ViewPromoCode.ViewPromoCodeViewModel
import com.freqwency.promotr.view.activities.HistoryActivity
import com.freqwency.promotr.view.dashboard.adapters.PromoCodesAdapter
import com.freqwency.promotr.view.dashboard.viewmodel.category.CategoryResponse
import com.freqwency.promotr.view.dashboard.viewmodel.category.CategoryViewModel
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.johncodeos.customprogressdialogexample.CustomProgressDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PromoterProfileFragment : Fragment() {
    val categoryViewModel: CategoryViewModel by viewModels()
    val viewPromoCodeViewModel: ViewPromoCodeViewModel by viewModels()
    var catList : ArrayList<CategoryResponse.Categorys> = ArrayList()

    private lateinit var activity: Activity
    private var _binding: FragmentPromoterProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        activity = requireActivity()

        _binding = FragmentPromoterProfileBinding.inflate(inflater, container, false)

        val progressDialog by lazy { CustomProgressDialog(activity) }

        val suggestions: RecyclerView = binding.rcvongoing
        suggestions.setHasFixedSize(true)
        // Creating an instance of our NameAdapter class and setting it to our RecyclerView

        val button: Button = binding.btnAddnew
        button.setOnClickListener {
            val intent = Intent(activity, AddNewPromoCodeActivity::class.java)
            intent.putExtra("categoryName", "")
            intent.putExtra("categoryId", "")
            intent.putExtra("actions", catList)
            intent.putExtra("method", "add")
            activity.startActivity(intent)
        }

        val textView: TextView = binding.historyTxt
        textView.setOnClickListener {
            val intent = Intent(activity, HistoryActivity::class.java)
            activity.startActivity(intent)
        }

        getCategorys(progressDialog)
        categoryObserver()
        promoCodeObserver()

        setUpPieChartData()
        return binding.root
    }

    private fun setUpPieChartData() {
        val yVals = ArrayList<PieEntry>()
        yVals.add(PieEntry(21f))
        yVals.add(PieEntry(10f))
        yVals.add(PieEntry(9f))
        yVals.add(PieEntry(12f))
        yVals.add(PieEntry(12.5f))

        val dataSet = PieDataSet(yVals, "")
        dataSet.valueTextSize=0f
        val colors = java.util.ArrayList<Int>()
        colors.add(resources.getColor(R.color.secondary_light))
        colors.add(resources.getColor(R.color.primary_dark))
        colors.add(resources.getColor(R.color.secondary_dark))
        colors.add(resources.getColor(R.color.black))
        colors.add(resources.getColor(R.color.primary))

        dataSet.setColors(colors)
        val data = PieData(dataSet)
        binding.pieChart.data = data
        binding.pieChart.centerTextRadiusPercent = 0f
        binding.pieChart.isDrawHoleEnabled = false
        binding.pieChart.legend.isEnabled = false
        binding.pieChart.description.isEnabled = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun categoryObserver() {
        categoryViewModel.progressIndicator.observe(viewLifecycleOwner, Observer {
        })
        categoryViewModel.mCategoryResponse.observe(
            viewLifecycleOwner
        ) {
            catList = it.peekContent().data?.categorys!!
        }

        categoryViewModel.errorResponse.observe(viewLifecycleOwner) {
            ErrorUtil.handlerGeneralError(requireContext(), it)
        }
    }

    private fun promoCodeObserver() {
        viewPromoCodeViewModel.progressIndicator.observe(viewLifecycleOwner, Observer {
        })
        viewPromoCodeViewModel.mViewPromoCodeResponse.observe(
            viewLifecycleOwner
        ) {
            val list = it.peekContent().data?.promoCodes

            binding.rcvongoing.isHorizontalScrollBarEnabled = true
            binding.rcvongoing.isHorizontalFadingEdgeEnabled = true
            //binding.rcvongoing.layoutManager = GridLayoutManager(activity, 3, GridLayoutManager.HORIZONTAL, false)
            binding.rcvongoing.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
            binding.rcvongoing.adapter = PromoCodesAdapter(requireContext(), list)
        }

        viewPromoCodeViewModel.errorResponse.observe(viewLifecycleOwner) {
            ErrorUtil.handlerGeneralError(requireContext(), it)
        }
    }

    fun getCategorys(progressDialog: CustomProgressDialog) {
        categoryViewModel.getCategory(progressDialog, activity)
       // viewPromoCodeViewModel.getViewPromoCodeForPromoter(progressDialog, activity)
    }

    override fun onResume() {
        super.onResume()
        val progressDialog by lazy { CustomProgressDialog(activity) }
        viewPromoCodeViewModel.getViewPromoCodeForPromoter(progressDialog, activity)
    }

}