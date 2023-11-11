package com.example.birdtrail_opsc7312

import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
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

            //define preference file
            val pref = getSharedPreferences(myPrefsFile, MODE_PRIVATE)

            //get the stored user ID
            val userID = pref.getString(myUserID, null)

            //if the stored user ID is not null aka exists
            if (userID != null) {

                //new loading cover screen
                val loadingProgressBar = layoutInflater.inflate(R.layout.loading_cover, null) as ViewGroup
                binding.root.addView(loadingProgressBar)

                //users current location
                var userLocation: Location? = null
                lifecycleScope.launch {

                    val databaseManager = DatabaseHandler()

                    //load all users from database
                    databaseManager.getAllUsers()

                    //set users location
                    userLocation = getUserLocation()

                    withContext(Dispatchers.Main) {

                        //loop through users
                        for (user in GlobalClass.userData) {

                            //if the loops user id matches the stored user id
                            if (user.userID == userID) {

                                //set the active users user id to the stored user id
                                GlobalClass.currentUser.userID = user.userID

                                //hide the loading screen
                                loadingProgressBar.visibility = View.GONE


                                //navigate the user into the app and pass the location coordinates
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
                //open sign in screen
                var intent = Intent(this, SignIn::class.java)
                startActivity(intent)
            }

            binding.btnSignUp.setOnClickListener()
            {
                //open sign up screen
                var intent = Intent(this, SignUp::class.java)
                startActivity(intent)
            }

        } catch (e: Exception) {
            GlobalClass.InformUser(getString(R.string.errorText), "${e.toString()}", this)
        }
    }

    //method to get user current location
    suspend fun getUserLocation(): Location? {

        //store users location
        var userLocation: Location? = null

        //location services client
        var mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        //wait to get the last location
        mFusedLocationClient.lastLocation
            .addOnSuccessListener(this) { task ->
                userLocation = task
            }.await()

        //return the location
        return userLocation
    }

    override fun onBackPressed() {
        //disable back button
    }
}