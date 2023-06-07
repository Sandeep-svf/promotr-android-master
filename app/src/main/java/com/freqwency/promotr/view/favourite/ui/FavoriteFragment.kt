package com.freqwency.promotr.view.favourite.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.freqwency.promotr.R
import com.freqwency.promotr.application.PromotrApp
import com.freqwency.promotr.databinding.FragmentFavoriteBinding
import com.freqwency.promotr.utils.ErrorUtil
import com.freqwency.promotr.view.favourite.FavouriteAdapter
import com.freqwency.promotr.view.favourite.viewmodel.PromoCodeShowFavViewModel
import com.freqwency.promotr.view.favourite.viewmodel.PromocodeShowFavResponse
import com.freqwency.promotr.view.logins.ui.LoginActivity
import com.freqwency.promotr.view.promoterprofile.adapters.FavouritePromoterAdapter
import com.freqwency.promotr.view.promoterprofile.viewmodel.promoterShowFav.PromoterShowFavResponse
import com.freqwency.promotr.view.promoterprofile.viewmodel.promoterShowFav.PromoterShowFavViewModel
import com.johncodeos.customprogressdialogexample.CustomProgressDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoriteFragment : Fragment() {
    private lateinit var activity: Activity
    val promoCodeShowFavViewModel: PromoCodeShowFavViewModel by viewModels()
    val promoterShowFavViewModel: PromoterShowFavViewModel by viewModels()
    var favList: ArrayList<PromocodeShowFavResponse.FavoritePromoCodes> = ArrayList()
    var favPromoterList: ArrayList<PromoterShowFavResponse.FavoritePromoters> = ArrayList()
    private var _binding: FragmentFavoriteBinding? = null
    var favType = "1"

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        val root: View = binding.root

        activity = requireActivity()
        val progressDialog by lazy { CustomProgressDialog(activity) }

        val token = PromotrApp.encryptedPrefs.bearerToken
        if (token.equals("")) {
            binding.loginLayout.visibility = View.VISIBLE
            binding.rcvFavorites.visibility = View.GONE

            binding.promocodeLauout.visibility = View.GONE
            binding.promoterLauout.visibility = View.GONE
        } else {
            binding.loginLayout.visibility = View.GONE
            binding.rcvFavorites.visibility = View.VISIBLE
            favType = "1"
            binding.promocodeLauout.visibility = View.VISIBLE
            binding.promoterLauout.visibility = View.VISIBLE
            // getPromoCodeShowFav(progressDialog)
        }

        binding.promocodeBtn.setTextColor(activity.getColor(R.color.secondary))
        binding.promocodeView.visibility = View.VISIBLE
        binding.promoterView.visibility = View.GONE
        // binding.promocodeView.setBackgroundColor(activity.getColor(R.color.secondary))

        val promoterId = PromotrApp.encryptedPrefs.promoterId

        binding.promocodeBtn.setOnClickListener {
            binding.promocodeBtn.setTextColor(activity.getColor(R.color.secondary))
            binding.promoterBtn.setTextColor(activity.getColor(R.color.black))
            binding.promocodeView.visibility = View.VISIBLE
            binding.promoterView.visibility = View.GONE

            getPromoCodeShowFav(progressDialog)
            favType = "1"
        }

        binding.promoterBtn.setOnClickListener {
            binding.promocodeBtn.setTextColor(activity.getColor(R.color.black))
            binding.promoterBtn.setTextColor(activity.getColor(R.color.secondary))
            binding.promocodeView.visibility = View.GONE
            binding.promoterView.visibility = View.VISIBLE
            favType = "2"
            getPromoterShowFav(progressDialog)
        }

        /*if (promoterId.equals("")) {
            binding.promoterLauout.visibility = View.GONE
        } else {
            binding.promoterLauout.visibility = View.VISIBLE
        }*/

        binding.signInBtn.setOnClickListener {
          //  PromotrApp.encryptedPrefs.isFirstTime = true
            val intent = Intent(activity, LoginActivity::class.java)
            activity.startActivity(intent)
           // activity.finish()
        }

        val suggestions: RecyclerView = binding.rcvFavorites
        suggestions.setHasFixedSize(true)

        PromoCodeShowFavObserver()
        PromoterShowFavObserver()
        //getPromoterShowFav(progressDialog)
        return root
    }

    /**
     * Promocode Favourite list show
     */
    fun getPromoCodeShowFav(progressDialog: CustomProgressDialog) {
        promoCodeShowFavViewModel.getpromoCodeShowFav(progressDialog, activity)
    }

    private fun PromoCodeShowFavObserver() {
        promoCodeShowFavViewModel.progressIndicator.observe(viewLifecycleOwner, Observer {
        })
        promoCodeShowFavViewModel.mPromocodeShowFavResponse.observe(
            viewLifecycleOwner
        ) {
            favList = it.peekContent().data?.favoritePromoCodes!!

            binding.rcvFavorites.isVerticalFadingEdgeEnabled = true
            binding.rcvFavorites.isVerticalScrollBarEnabled = true
            binding.rcvFavorites.layoutManager = LinearLayoutManager(requireActivity())
            // binding.rcvCategories.addItemDecoration(DividerItemDecoration(requireContext(), RecyclerView.VERTICAL))
            binding.rcvFavorites.adapter = FavouriteAdapter(requireContext(), favList)
        }

        promoCodeShowFavViewModel.errorResponse.observe(viewLifecycleOwner) {
            ErrorUtil.handlerGeneralError(requireContext(), it)
        }
    }


    /**
     * Promoter Favourite list show
     */
    fun getPromoterShowFav(progressDialog: CustomProgressDialog) {
        promoterShowFavViewModel.getpromoterShowFav(progressDialog, activity)
    }

    private fun PromoterShowFavObserver() {
        promoterShowFavViewModel.progressIndicator.observe(viewLifecycleOwner, Observer {
        })
        promoterShowFavViewModel.mPromoterShowFavResponse.observe(
            viewLifecycleOwner
        ) {
            favPromoterList = it.peekContent().data?.favoritePromoters!!

            binding.rcvFavorites.isVerticalFadingEdgeEnabled = true
            binding.rcvFavorites.isVerticalScrollBarEnabled = true
            binding.rcvFavorites.layoutManager = LinearLayoutManager(requireActivity())
            binding.rcvFavorites.adapter =
                FavouritePromoterAdapter(requireContext(), favPromoterList)
        }

        promoterShowFavViewModel.errorResponse.observe(viewLifecycleOwner) {
            ErrorUtil.handlerGeneralError(requireContext(), it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        val progressDialog by lazy { CustomProgressDialog(activity) }
        val token = PromotrApp.encryptedPrefs.bearerToken

        Log.e("promoterId", "  Tokens   ")

        if (favType.equals("2")) {
            getPromoterShowFav(progressDialog)
            Log.e("promoterId", "  Promotr   "+favType)
        } else {
            if (!token.equals("")) {
                getPromoCodeShowFav(progressDialog)
                Log.e("promoterId", "  PromoCode   "+favType)
            }
        }
    }
}