package com.example.birdtrail_opsc7312

import android.graphics.*
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.birdtrail_opsc7312.databinding.ActivityHomepageBinding
import kotlinx.coroutines.*

class Homepage : AppCompatActivity() {

    lateinit var binding : ActivityHomepageBinding
    private lateinit var loadingProgressBar : ViewGroup

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = ActivityHomepageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Hide the action bar
        supportActionBar?.hide()

        //set status bar color
        window.statusBarColor = ContextCompat.getColor(this, R.color.dark_blue)

        loadingProgressBar = layoutInflater.inflate(R.layout.loading_cover, null) as ViewGroup
        binding.root.addView(loadingProgressBar)

        try
        {
            var lat = intent.getDoubleExtra("lat", 0.0)
            var long = intent.getDoubleExtra("long", 0.0)

            GlobalScope.launch {
                //get nearby hotspots
                var eBirdHandler = eBirdAPIHandler()
                eBirdHandler.getNearbyHotspots(long, lat)

                //get bird observations
                eBirdHandler.getRecentObservations("ZA")

                val databaseManager = DatabaseHandler()
                databaseManager.updateLocalData()

                GlobalClass.currentUser.profilepicture = databaseManager.getUserImage(this@Homepage, GlobalClass.currentUser.userID, GlobalClass.currentUser.hasProfile)

                withContext(Dispatchers.Main) {
                    initUI()
                }
            }
        }
        catch (e: Exception)
        {
            GlobalClass.InformUser(getString(R.string.errorText),"$e", this)
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun initUI()
    {

        //add the default start achievement
        GlobalClass.initStarterAchievement(this)

        loadingProgressBar.visibility = View.GONE

        //create local fragment controller
        val fragmentControl = FragmentHandler()

        //load home fragment
        var loadHome = HomeFragment()

        fragmentControl.replaceFragment(loadHome, R.id.flContent, supportFragmentManager)

        //bottom navigation
        binding.bottomNavigationView.selectedItemId = R.id.home

        binding.fab.setOnClickListener(){
            fragmentControl.replaceFragment(Add_Observation(), R.id.flContent, supportFragmentManager)
            binding.bottomNavigationView.selectedItemId = R.id.placeholder
        }

        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId)
            {
                R.id.ranking -> fragmentControl.replaceFragment(Ranking(), R.id.flContent, supportFragmentManager)
                R.id.home -> fragmentControl.replaceFragment(HomeFragment(), R.id.flContent, supportFragmentManager)
                R.id.observations -> fragmentControl.replaceFragment(UserObservations(), R.id.flContent, supportFragmentManager)
                R.id.settings -> fragmentControl.replaceFragment(AppSettings(), R.id.flContent, supportFragmentManager)
                else -> {

                }
            }
            true
        }

        findViewById<View>(R.id.placeholder).isClickable = false
    }

    override fun onBackPressed() {
        //disable back button
    }
}