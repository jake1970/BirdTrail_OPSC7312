package com.example.birdtrail_opsc7312

import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.birdtrail_opsc7312.databinding.LandingPageBinding
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class LandingPage : AppCompatActivity() {


    private val myPrefsFile = "MyPrefsFile";
    private val myUserID = "";

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.landing_page)

        try {
            //---------------------------------------------------------------------------------------//
            //initial view config
            //---------------------------------------------------------------------------------------//
            var binding = LandingPageBinding.inflate(layoutInflater)
            setContentView(binding.root)

            //Hide the action bar
            supportActionBar?.hide()

            //set status bar color
            window.statusBarColor = ContextCompat.getColor(this, R.color.dark_blue)

            //---------------------------------------------------------------------------------------//
            //Remember Me
            //---------------------------------------------------------------------------------------//
            val pref = getSharedPreferences(myPrefsFile, MODE_PRIVATE)
            val userID = pref.getString(myUserID, null)


            if (userID != null) {

                val loadingProgressBar = layoutInflater.inflate(R.layout.loading_cover, null) as ViewGroup
                binding.root.addView(loadingProgressBar)

                var userLocation: Location? = null
                lifecycleScope.launch {

                    val databaseManager = DatabaseHandler()
                    databaseManager.getAllUsers()

                    userLocation = getUserLocation()

                    withContext(Dispatchers.Main) {

                        for (user in GlobalClass.userData) {

                            if (user.userID == userID) {


                                GlobalClass.currentUser.userID = user.userID

                                loadingProgressBar.visibility = View.GONE

                                var intent =
                                    Intent(this@LandingPage, Homepage::class.java)
                                userLocation?.let {
                                    intent.putExtra(
                                        "lat",
                                        it.latitude
                                    )
                                }
                                userLocation?.let {
                                    intent.putExtra(
                                        "long",
                                        it.longitude
                                    )
                                }
                                startActivity(intent)
                            }
                        }
                    }
                }
            }
            //---------------------------------------------------------------------------------------//

            binding.btnSignIn.setOnClickListener()
            {
                var intent = Intent(this, SignIn::class.java)
                startActivity(intent)
            }

            binding.btnSignUp.setOnClickListener()
            {
                var intent = Intent(this, SignUp::class.java)
                startActivity(intent)
            }


        } catch (e: Exception) {
            GlobalClass.InformUser(getString(R.string.errorText), "${e.toString()}", this)
        }
    }

    suspend fun getUserLocation(): Location? {

        var userLocation: Location? = null

        var mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient.lastLocation
            .addOnSuccessListener(this) { task ->
                userLocation = task
            }.await()

        return userLocation

    }

    override fun onBackPressed() {
        //disable back button
    }

}