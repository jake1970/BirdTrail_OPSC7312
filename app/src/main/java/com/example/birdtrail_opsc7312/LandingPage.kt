package com.example.birdtrail_opsc7312

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.birdtrail_opsc7312.databinding.LandingPageBinding


class LandingPage : AppCompatActivity() {


    private val myPrefsFile = "MyPrefsFile";
    private val myUserID = "";

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.landing_page)

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

        //---------------------------------------------------------------------------------------//
        //Remember Me
        //---------------------------------------------------------------------------------------//
        val pref = getSharedPreferences(myPrefsFile, MODE_PRIVATE)
        val userID = pref.getString(myUserID, null)

        if (userID != null) {
            Toast.makeText(this, userID, Toast.LENGTH_SHORT)


            for (user in GlobalClass.userData)
            {
                if (user.userID == userID.toInt())
                {
                    GlobalClass.currentUser = user
                }
            }

            var intent = Intent(this, Homepage::class.java)
            startActivity(intent)

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


    }
    override fun onBackPressed() {
        // Do nothing
    }

}