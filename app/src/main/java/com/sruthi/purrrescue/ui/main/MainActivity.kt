package com.sruthi.purrrescue.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.sruthi.purrrescue.R
import com.sruthi.purrrescue.databinding.ActivityMainBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    private val screensWithoutBottomNav = setOf(
        R.id.welcomeFragment,
        R.id.loginFragment,
        R.id.signupFragment
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            binding.statusBarBg.layoutParams.height = statusBarHeight
            binding.statusBarBg.requestLayout()
            insets
        }

        var isReady = false
        splashScreen.setKeepOnScreenCondition { !isReady }

        lifecycleScope.launch {
            delay(800)
            isReady = true
        }

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController

        binding.bottomNavMenu.setOnItemSelectedListener { item ->
            if (navController.currentDestination?.id == item.itemId) {
                return@setOnItemSelectedListener true
            }

            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.home, inclusive = false)
                .setLaunchSingleTop(true)
                .build()

            when (item.itemId) {
                R.id.home -> {
                    navController.navigate(R.id.home, null, navOptions)
                    true
                }
                R.id.report_cat -> {
                    navController.navigate(R.id.report_cat, null, navOptions)
                    true
                }
                R.id.profile -> {
                    navController.navigate(R.id.profile, null, navOptions)
                    true
                }
                else -> false
            }
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.bottomNavMenu.visibility =
                if (destination.id in screensWithoutBottomNav) View.GONE else View.VISIBLE
        }

    }

    fun showLoading() {
        binding.includeProgressOverlay.flProgress.visibility = View.VISIBLE
    }

    fun hideLoading() {
        binding.includeProgressOverlay.flProgress.visibility = View.GONE
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleNotificationIntent(intent)
    }

    private fun handleNotificationIntent(intent: Intent) {
        val reportId = intent.getStringExtra("reportId") ?: return
        navController = findNavController(R.id.fragmentContainerView)

        val bundle = bundleOf("reportId" to reportId)
        navController.navigate(R.id.catDetailsFragment, bundle)
    }
}