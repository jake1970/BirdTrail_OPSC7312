package com.example.birdtrail_opsc7312

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.birdtrail_opsc7312.databinding.ActivityHomepageBinding
import com.example.birdtrail_opsc7312.databinding.LandingPageBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.mapbox.geojson.Point
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.navigation.core.trip.session.LocationMatcherResult
import com.mapbox.navigation.core.trip.session.LocationObserver
import com.mapbox.navigation.ui.maps.camera.transition.NavigationCameraTransitionOptions
import com.mapbox.navigation.ui.maps.location.NavigationLocationProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.internal.wait
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period

class Homepage : AppCompatActivity() {



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_homepage)

        var binding = ActivityHomepageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);


        //locationPermissionHelper = LocationPermissionHelper(WeakReference(this))
        val loadingProgressBar = layoutInflater.inflate(R.layout.loading_cover, null) as ViewGroup
        binding.root.addView(loadingProgressBar)



/*
        var userLocation: Location? = null
        var mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        var a = mFusedLocationClient.lastLocation.result

        while (a == null)
        {
      //  var mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        //mFusedLocationClient.lastLocation.addOnCompleteListener(requireActivity()) { task ->
            //userLocation = task.result
           // if (userLocation != null) {
        }

 */


        var loadHome = HomeFragment()



        GlobalScope.launch {
            //get bird observations
            var eBirdHandler = eBirdAPIHandler()
            eBirdHandler.getRecentObservations("ZA")

            //GetUserLocation()
            //eBirdHandler.getNearbyHotspots(long, lat)

            withContext(Dispatchers.Main) {
                loadingProgressBar.visibility = View.GONE
            }
        }





        //Hide the action bar
        supportActionBar?.hide()

        //set status bar color
        window.statusBarColor = ContextCompat.getColor(this, R.color.dark_blue)

        //add the default start achievement
        GlobalClass.initStarterAchievement(this)

        //create local fragment controller
        val fragmentControl = FragmentHandler()

       // fragmentControl.replaceFragment(HomeFragment(), R.id.flContent, supportFragmentManager)

        fragmentControl.replaceFragment(loadHome, R.id.flContent, supportFragmentManager)

        // replaceFragment(home())
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

        /*
        binding.bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.leaderboard -> {
                    // Start Leaderboard Activity
                    true
                }
                R.id.home -> {
                    // Start Home Activity
                    val intent = Intent(this, Homepage::class.java)
                    startActivity(intent)
                    true
                }
                R.id.observations -> {
                    // Start Observations Activity
                    true
                }
                R.id.settings -> {
                    // Start Settings Activity
                    val intent = Intent(this, SettingsPage::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
         */

    }

//    fun GetUserLocation()
//    {
//        if (checkPermissions()) {
//            var userLocation: Location? = null
//            var mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
//            mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
//                userLocation = task.result
//                if (userLocation != null) {
//                    lat = userLocation!!.latitude
//                    long = userLocation!!.longitude
//                }
//            }
//        }
//    }




    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }


//    override fun onBackPressed() {
//        // Do nothing
//    }

}