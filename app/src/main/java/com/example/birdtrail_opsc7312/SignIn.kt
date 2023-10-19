package com.example.birdtrail_opsc7312

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.Debug.getLocation
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.birdtrail_opsc7312.databinding.SignInBinding
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import okhttp3.internal.wait
import java.lang.ref.WeakReference


class SignIn : AppCompatActivity() {

   private val myPrefsFile = "MyPrefsFile";
   private val myUserID = "";

    lateinit var binding: SignInBinding

    lateinit var locationPermissionHelper: LocationPermissionHelper


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sign_in)

        //---------------------------------------------------------------------------------------//
        //initial view config
        //---------------------------------------------------------------------------------------//

        //binding
        binding = SignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Hide the action bar
        supportActionBar?.hide()

        //set status bar color
        window.statusBarColor = ContextCompat.getColor(this, R.color.dark_blue)

        //---------------------------------------------------------------------------------------//


        //when the sign in button is clicked
        binding.btnSignIn.setOnClickListener()
        {

            //attempt to sign the user in
            val attemptSignIn = UserDataClass().validateUser(binding.etEmail.text.toString(), binding.etPassword.text.toString())

            //if the sign in was valid
            if (attemptSignIn == true)
            {

                //instantiate location permission helper
                locationPermissionHelper = LocationPermissionHelper(WeakReference(this))

                //call check permission and pass the sign in method
                locationPermissionHelper.checkPermissions {
                    signIn()
                }

            }
            else
            {
                //if the sign in was invalid

                //inform the user of the failure to sign in
                GlobalClass.InformUser(getString(R.string.failedSignIn), getString(R.string.incorrectEmailPassCombo), this)
            }

        }


        //when the back button is clicked
        binding.tvBack.setOnClickListener()
        {
            //take the user back to the landing page
            var intent = Intent(this, LandingPage::class.java)
            startActivity(intent)
        }


        //when forgot password is pressed
        binding.tvForgotPassword.setOnClickListener(){
            //inform the user of the pending feature
            Toast.makeText(this, getString(R.string.comingSoonText), Toast.LENGTH_SHORT).show()
        }

        //when sign up text is clicked
        binding.tvSignUp.setOnClickListener()
        {
            //take the user to the sign up page
            var intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }

    }

    suspend fun getUserLocation() : Location? {

        var userLocation: Location? = null

        var mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient.lastLocation
            .addOnSuccessListener (this) { task ->
                userLocation = task
            }.await()

        return userLocation

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun signIn()
    {
        //---------------------------------------------------------------------------------------//
        //Remember Me
        //---------------------------------------------------------------------------------------//
        if (binding.chkRememberMe.isChecked == true) {
            getSharedPreferences(myPrefsFile, MODE_PRIVATE)
                .edit()
                .putString(myUserID, GlobalClass.currentUser.userID.toString())
                .commit();
        }
        //---------------------------------------------------------------------------------------//


        var userLocation: Location? = null
        lifecycleScope.launch {

            userLocation = getUserLocation()

            withContext(Dispatchers.Main) {

                var intent = Intent(this@SignIn, Homepage::class.java)
                userLocation?.let { intent.putExtra("lat", it.latitude) }
                userLocation?.let { intent.putExtra("long", it.longitude) }
                startActivity(intent)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        locationPermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onBackPressed() {
        // Do nothing
    }

}