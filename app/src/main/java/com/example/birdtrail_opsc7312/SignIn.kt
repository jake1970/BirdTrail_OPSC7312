package com.example.birdtrail_opsc7312

import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.birdtrail_opsc7312.databinding.SignInBinding
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
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

            try {

                //firebase auth
                val firebaseAuth = FirebaseAuth.getInstance()

                //try signing in with the given information
                firebaseAuth.signInWithEmailAndPassword(
                    binding.etEmail.text.toString(),
                    binding.etPassword.text.toString()
                ).addOnCompleteListener {

                    //if the sign in data is valid and successful
                    if (it.isSuccessful) {

                        //set the users id
                        GlobalClass.currentUser.userID = firebaseAuth.currentUser?.uid.toString()

                        //instantiate location permission helper
                        locationPermissionHelper = LocationPermissionHelper(WeakReference(this))


                        //call check permission and pass the sign in method
                        locationPermissionHelper.checkPermissions {
                            signIn()
                        }


                    } else {

                        //if sign in fails

                        //show the user why the sign in failed
                        Toast.makeText(
                            this,
                            it.exception?.localizedMessage.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }


                }
            }
            catch (e :Exception)
            {
                GlobalClass.InformUser(getString(R.string.errorText),"$e", this@SignIn)
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

            try {

                val alert = AlertDialog.Builder(this)

                alert.setTitle("Enter your email")
                alert.setMessage("Please enter your accounts email address below, an email containing a link to reset your password will be sent shortly after.")

                // Set an EditText view to get user input
                val input = EditText(this)
                input.hint = "email"
                alert.setView(input)

                alert.setPositiveButton("Send") { dialog, whichButton ->
                    val value: String = input.text.toString()
                    // Do something with value!

                    if (!value.isNullOrEmpty()) {
                        val firebaseAuth = FirebaseAuth.getInstance()
                        firebaseAuth.sendPasswordResetEmail(value)
                        Toast.makeText(this, "Password reset sent to: $value", Toast.LENGTH_SHORT)
                            .show()

                    }
                }

                alert.setNegativeButton(
                    "Cancel"
                ) { dialog, whichButton ->
                    // Canceled.
                }

                alert.show()
            }
            catch (e :Exception)
            {
                GlobalClass.InformUser(getString(R.string.errorText),"$e", this@SignIn)
            }
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