package com.marhabweb.minizendo_update

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.marhabweb.minizendo_update.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MZPrefs.setup(applicationContext)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController = findNavController(R.id.nav_host_fragment_content_main)

        binding.fab.setOnClickListener {
            navController.navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
        binding.headerBack.setOnClickListener {
            navController.navigateUp()
        }

        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (navController.currentDestination?.id == R.id.SecondFragment) {
                        if (!navController.popBackStack(R.id.FirstFragment, false)) {
                            if (!navController.navigateUp()) {
                                finish()
                            }
                        }
                    } else if (!navController.navigateUp()) {
                        moveTaskToBack(true)
                    }
                }
            }
        )

        navController.addOnDestinationChangedListener { _, dest, _ ->
            when (dest.id) {
                R.id.FirstFragment -> {
                    binding.headerBack.visibility = View.GONE
                }
                R.id.SecondFragment -> {
                    setTopBarVisible(false)
                }
            }
        }
    }

    fun plusButtonVisible(visible: Boolean) {
        binding.fab.visibility = if (visible) View.VISIBLE else View.GONE
    }

    fun plusButtonEnabled(enabled: Boolean) {
        binding.fab.isEnabled = enabled
    }

    /**
     * Top bar for the first screen: "Mini Zendo" when empty, "Session List" when the user has sessions.
     */
    fun applySessionListHeader(hasSessions: Boolean) {
        with(binding) {
            setTopBarVisible(hasSessions)
            headerBack.visibility = View.GONE
            headerTitle.setText(if (hasSessions) R.string.session_list else R.string.mini_zendo)
            headerTitle.gravity = Gravity.CENTER_VERTICAL or Gravity.START
            headerTitle.textAlignment = TextView.TEXT_ALIGNMENT_VIEW_START
            headerTitle.visibility = View.VISIBLE
        }
    }

    fun setTopBarVisible(visible: Boolean) {
        binding.topBar.visibility = if (visible) View.VISIBLE else View.GONE
    }

    fun refreshList() {
        @Suppress("DEPRECATION")
        overridePendingTransition(0, 0)
        startActivity(intent)
        @Suppress("DEPRECATION")
        overridePendingTransition(0, 0)
    }
}
