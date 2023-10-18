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
        //var binding = SignInBinding.inflate(layoutInflater)
        binding = SignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Hide the action bar
        supportActionBar?.hide()

        //set status bar color
        window.statusBarColor = ContextCompat.getColor(this, R.color.dark_blue)

        //---------------------------------------------------------------------------------------//


        //888888888888888888888888888888888888888888888888888888888888888888888888888888888888888//

        /*
        binding.etEmail.setText("user1@gmail.com")
        binding.etPassword.setText("Password1")
         */

        binding.etEmail.setText("jake")
        binding.etPassword.setText("jake")

        //888888888888888888888888888888888888888888888888888888888888888888888888888888888888888//



        binding.btnSignIn.setOnClickListener()
        {


            val attemptSignIn = UserDataClass().validateUser(binding.etEmail.text.toString(), binding.etPassword.text.toString())

            if (attemptSignIn == true)
            {

                locationPermissionHelper = LocationPermissionHelper(WeakReference(this))
                locationPermissionHelper.checkPermissions {
                    signIn()
                }


                /*
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

                var intent = Intent(this, Homepage::class.java)
                startActivity(intent)
                 */

            }
            else
            {
                GlobalClass.InformUser(getString(R.string.failedSignIn), getString(R.string.incorrectEmailPassCombo), this)
            }


        }

        binding.tvBack.setOnClickListener()
        {
            var intent = Intent(this, LandingPage::class.java)
            startActivity(intent)
        }


        binding.tvSignUp.setOnClickListener()
        {
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

                Toast.makeText(this@SignIn, "Long: ${userLocation?.longitude} Lat: ${userLocation?.latitude}", Toast.LENGTH_SHORT).show()

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

}