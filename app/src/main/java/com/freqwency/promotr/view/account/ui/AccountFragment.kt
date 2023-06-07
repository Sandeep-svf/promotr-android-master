package com.freqwency.promotr.view.account.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.freqwency.promotr.R
import com.freqwency.promotr.application.PromotrApp
import com.freqwency.promotr.databinding.FragmentAccountBinding
import com.freqwency.promotr.view.becomePromoter.ui.PromoOwnerRegisterActivity
import com.freqwency.promotr.view.account.viewmodel.AccountViewModel
import com.freqwency.promotr.view.activities.SettingsActivity
import com.freqwency.promotr.view.logins.ui.LoginActivity
import com.freqwency.promotr.view.userprofile.ui.ProfileActivity

class AccountFragment : Fragment() {
    val userAccountViewModel: AccountViewModel by viewModels()
    private var _binding: FragmentAccountBinding? = null
    private lateinit var activity: Activity
    private val binding get() = _binding!!
    var promoterId = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity = requireActivity()

        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        promoterId = PromotrApp.encryptedPrefs.promoterId
        checkLoginStatus()

        binding.llSettings.setOnClickListener {
            val intent = Intent(activity, SettingsActivity::class.java)
            activity.startActivity(intent)
        }

        binding.signInBtn.setOnClickListener {
            val intent = Intent(activity, LoginActivity::class.java)
            activity.startActivity(intent)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun checkLoginStatus() {
        if (PromotrApp.encryptedPrefs.user != null) {
            // logged in
            if (promoterId.equals("")) {
                binding.btnPromoterIn.visibility = View.VISIBLE //to check if promoter
            } else {
                binding.btnPromoterIn.visibility = View.GONE //to check if promoter
            }

            binding.signInBtn.isVisible = false
            binding.arrowIcon.isVisible = true
        } else {
            // not logged in
            binding.btnPromoterIn.isVisible = false //to check if promoter
            binding.signInBtn.isVisible = true
            binding.arrowIcon.isVisible = false
        }
        binding.llProfile.setOnClickListener {
            if (PromotrApp.encryptedPrefs.user != null) {
                val intent = Intent(activity, ProfileActivity::class.java)
                activity.startActivity(intent)
            } else {
                // go to login
                val intent = Intent(activity, LoginActivity::class.java)
                activity.startActivity(intent)
            }
        }

        binding.btnPromoterIn.setOnClickListener {
            val intent = Intent(activity, PromoOwnerRegisterActivity::class.java)
            activity.startActivity(intent)
        }
    }
}