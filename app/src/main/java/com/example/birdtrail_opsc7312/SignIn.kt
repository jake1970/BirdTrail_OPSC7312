package com.example.birdtrail_opsc7312

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.example.birdtrail_opsc7312.databinding.LandingPageBinding
import com.example.birdtrail_opsc7312.databinding.SignInBinding

class SignIn : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sign_in)

        //---------------------------------------------------------------------------------------//
        //initial view config
        //---------------------------------------------------------------------------------------//
        var binding = SignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Hide the action bar
        supportActionBar?.hide()

        //set status bar color
        window.statusBarColor = ContextCompat.getColor(this, R.color.dark_blue)

        //---------------------------------------------------------------------------------------//

        binding.btnSignIn.setOnClickListener()
        {




            val attemptSignIn = UserDataClass().validateUser(binding.etEmail.text.toString(), binding.etPassword.text.toString())

            if (attemptSignIn == true)
            {
                var intent = Intent(this, Homepage::class.java)
                startActivity(intent)
            }
            else
            {
                GlobalClass.InformUser(getString(R.string.failedSignIn), getString(R.string.incorrectEmailPassCombo), this)
            }


        }

        binding.tvBack.setOnClickListener()
        {
            finish()
        }


        binding.tvSignUp.setOnClickListener()
        {
            var intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }

    }
}