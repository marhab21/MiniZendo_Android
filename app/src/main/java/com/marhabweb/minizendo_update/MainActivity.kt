package com.marhabweb.minizendo_update


import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.marhabweb.minizendo_update.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set up the persistence of sessions
        MZPrefs.setup(applicationContext)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

       // val navController = findNavController(R.id.nav_host_fragment_content_main)
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.fab.setOnClickListener {
            navController.navigate(R.id.action_FirstFragment_to_SecondFragment)
            binding.fab.visibility = View.INVISIBLE
        }

    }

    // Toggle the plus button in and out of fragments
    fun plusButtonVisible(visible: Boolean) {
        val fabBtn = binding.fab
        if (visible) {
            fabBtn.visibility = View.VISIBLE
        } else {
            fabBtn.visibility = View.INVISIBLE
        }
    }

    // Disable if limit is reached.
    fun plusButtonEnabled(enabled: Boolean) {
        val fabBtn = binding.fab
        fabBtn.alpha = 1.0F
        fabBtn.isEnabled = enabled
    }

   // Refresh list after deleting a session
   fun refreshList() {
        overridePendingTransition(0, 0)
        startActivity(intent)
        overridePendingTransition(0, 0)
    }

    // Navigate
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}

