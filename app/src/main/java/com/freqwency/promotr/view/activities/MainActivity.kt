package com.freqwency.promotr.view.activities

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.freqwency.promotr.R
import com.freqwency.promotr.application.PromotrApp
import com.freqwency.promotr.databinding.ActivityMainBinding
import com.freqwency.promotr.utils.GlobalFunctions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    private lateinit var navController: NavController
    private lateinit var activity: Activity
    private lateinit var mainBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        activity = this

        GlobalFunctions.addUIPadding(mainBinding.root)

        mainBinding.bottomNavigationView.background = null //Removing the background shadow
        mainBinding.bottomNavigationView.menu.getItem(2).isEnabled = true

        val promoterId = PromotrApp.encryptedPrefs.promoterId

        if (promoterId.isEmpty()) {
            mainBinding.bottomNavigationView.menu.getItem(2).isVisible = false
            mainBinding.fab.visibility = View.GONE
        } else {
            mainBinding.bottomNavigationView.menu.getItem(2).isVisible = true
            mainBinding.fab.visibility = View.VISIBLE
        }

        val navView: BottomNavigationView = mainBinding.bottomNavigationView

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment?
        navController = navHostFragment!!.navController
        mainBinding.fab.setImageResource(R.drawable.add_promo_unselected)
        navView.setupWithNavController(navController)

        val floatingActionButton: FloatingActionButton = mainBinding.fab
        floatingActionButton.setOnClickListener {
            mainBinding.fab.setImageResource(R.drawable.add_promo_selected);
            mainBinding.bottomNavigationView.selectedItemId = R.id.promoterProfileFragment
            navController.navigate(R.id.promoterProfileFragment)
        }

        navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    mainBinding.fab.setImageResource(R.drawable.add_promo_unselected)
                    navController.navigate(R.id.navigation_home)
                }

                R.id.navigation_favorite -> {
                    mainBinding.fab.setImageResource(R.drawable.add_promo_unselected)
                    navController.navigate(R.id.navigation_favorite)
                }

                R.id.navigation_notifications -> {
                    mainBinding.fab.setImageResource(R.drawable.add_promo_unselected)
                    navController.navigate(R.id.navigation_notifications)
                }

                R.id.navigation_account -> {
                    mainBinding.fab.setImageResource(R.drawable.add_promo_unselected)
                    navController.navigate(R.id.navigation_account)
                }
            }
           true
        }

        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (navController.popBackStack().not()) {
                    //Last fragment: Do your operation here
                    finish()
                }
            }
        })
    }

}


