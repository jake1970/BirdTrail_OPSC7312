package com.example.birdtrail_opsc7312

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.example.birdtrail_opsc7312.databinding.LandingPageBinding

class LandingPage : AppCompatActivity() {
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

        //Add Temp Data
        GlobalClass.main()

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
}