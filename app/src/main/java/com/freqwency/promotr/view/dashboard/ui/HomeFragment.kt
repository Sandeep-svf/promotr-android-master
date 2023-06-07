package com.freqwency.promotr.view.dashboard.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.freqwency.promotr.R
import com.freqwency.promotr.application.PromotrApp
import com.freqwency.promotr.databinding.FragmentHomeBinding
import com.freqwency.promotr.utils.ErrorUtil
import com.freqwency.promotr.view.dashboard.adapters.CategoriesAdapter
import com.freqwency.promotr.view.dashboard.adapters.DashBoardPromoCodesAdapter
import com.freqwency.promotr.view.dashboard.adapters.DashPromoterAdapter
import com.freqwency.promotr.view.dashboard.viewmodel.category.CategoryResponse
import com.freqwency.promotr.view.dashboard.viewmodel.category.CategoryViewModel
import com.freqwency.promotr.view.dashboard.viewmodel.promocode.PromoCodeViewModel
import com.freqwency.promotr.view.dashboard.viewmodel.promotersList.PromotersListViewModel
import com.freqwency.promotr.view.search.ui.SearchActivity
import com.johncodeos.customprogressdialogexample.CustomProgressDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {
    val categoryViewModel: CategoryViewModel by viewModels()
    val promoCodeViewModel: PromoCodeViewModel by viewModels()
    val promotersListViewModel: PromotersListViewModel by viewModels()

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var activity: Activity
    var catList: ArrayList<CategoryResponse.Categorys> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        activity = requireActivity()
        val progressDialog by lazy { CustomProgressDialog(activity) }

        val categoriesRecyclerView: RecyclerView = binding.rcvCategories
        categoriesRecyclerView.setHasFixedSize(true)

        val suggestions: RecyclerView = binding.rcvSuggestions
        suggestions.setHasFixedSize(true)

        val promoter: RecyclerView = binding.rcvPromoter
        promoter.setHasFixedSize(true)

        val searchView: RelativeLayout = binding.searchView
        searchView.setOnClickListener {
            val intent = Intent(activity, SearchActivity::class.java)
            intent.putExtra("actions", catList)
            activity.startActivity(intent)
        }

        val promoterId = PromotrApp.encryptedPrefs.promoterId
        val bearerToken = PromotrApp.encryptedPrefs.bearerToken
        Log.e("promoterId", "+Id:    " + promoterId + " " + bearerToken)
        /*if (promoterId.equals("")) {
            binding.promoterLayout.visibility = View.GONE
        } else {
            binding.promoterLayout.visibility = View.VISIBLE
            getPromoterList(progressDialog)
        }*/

        getCategorys(progressDialog)
        getPromoterList(progressDialog)

        categoryObserver()
        promoCodeObserver()
        PromoterLisObserver()

        return root
    }

    /**
     * Category list show
     */
    fun getCategorys(progressDialog: CustomProgressDialog) {
        categoryViewModel.getCategory(progressDialog, activity)
        // promoCodeViewModel.getpromoCode()
    }

    private fun categoryObserver() {
        categoryViewModel.progressIndicator.observe(viewLifecycleOwner, Observer {
        })
        categoryViewModel.mCategoryResponse.observe(
            viewLifecycleOwner
        ) {
            catList = it.peekContent().data?.categorys!!

            binding.rcvCategories.isHorizontalScrollBarEnabled = true
            binding.rcvCategories.isHorizontalFadingEdgeEnabled = true
            binding.rcvCategories.layoutManager =
                LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
            //binding.rcvCategories.addItemDecoration(DividerItemDecoration(requireContext(), RecyclerView.VERTICAL))
            binding.rcvCategories.adapter = CategoriesAdapter(requireContext(), catList)
        }

        categoryViewModel.errorResponse.observe(viewLifecycleOwner) {
            ErrorUtil.handlerGeneralError(requireContext(), it)
        }

       /* Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)}, 10000)*/
    }

    /**
     * PromoCode list show
     */
    private fun promoCodeObserver() {
        promoCodeViewModel.progressIndicator.observe(viewLifecycleOwner, Observer {
        })
        promoCodeViewModel.mPromocodeResponse.observe(
            viewLifecycleOwner
        ) {
            val list = it.peekContent().data?.promoCodes

            binding.rcvSuggestions.isHorizontalScrollBarEnabled = true
            binding.rcvSuggestions.isHorizontalFadingEdgeEnabled = true
            if (list!!.size == 1) {
                binding.rcvSuggestions.layoutManager =
                    GridLayoutManager(activity, 1, GridLayoutManager.HORIZONTAL, false)
                binding.rcvSuggestions.adapter = DashBoardPromoCodesAdapter(requireContext(), list)
            } else if (list.size == 2) {
                binding.rcvSuggestions.layoutManager =
                    GridLayoutManager(activity, 2, GridLayoutManager.HORIZONTAL, false)
                binding.rcvSuggestions.adapter = DashBoardPromoCodesAdapter(requireContext(), list)
            } else {
                binding.rcvSuggestions.layoutManager =
                    GridLayoutManager(activity, 3, GridLayoutManager.HORIZONTAL, false)
                binding.rcvSuggestions.adapter = DashBoardPromoCodesAdapter(requireContext(), list)
            }
        }

        promoCodeViewModel.errorResponse.observe(viewLifecycleOwner) {
            ErrorUtil.handlerGeneralError(requireContext(), it)
        }
    }


    /**
     * Promoter list show
     */
    fun getPromoterList(progressDialog: CustomProgressDialog) {
        promotersListViewModel.getPromotersList(progressDialog, activity)
    }

    private fun PromoterLisObserver() {
        promotersListViewModel.progressIndicator.observe(viewLifecycleOwner, Observer {
        })
        promotersListViewModel.mPromotersListResponse.observe(
            viewLifecycleOwner
        ) {
            val list = it.peekContent().data?.promoters

            binding.rcvPromoter.isHorizontalScrollBarEnabled = true
            binding.rcvPromoter.isHorizontalFadingEdgeEnabled = true
            //binding.rcvPromoter.layoutManager = GridLayoutManager(activity, 3, GridLayoutManager.HORIZONTAL, false)
            binding.rcvPromoter.layoutManager =
                LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
            binding.rcvPromoter.adapter = DashPromoterAdapter(requireContext(), list)
        }

        promotersListViewModel.errorResponse.observe(viewLifecycleOwner) {
            ErrorUtil.handlerGeneralError(requireContext(), it)
        }
    }

    override fun onResume() {
        super.onResume()
        promoCodeViewModel.getpromoCode()
    }
}