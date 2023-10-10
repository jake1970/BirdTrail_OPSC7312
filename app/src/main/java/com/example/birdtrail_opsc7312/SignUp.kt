package com.example.birdtrail_opsc7312

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.birdtrail_opsc7312.databinding.ActivitySignUpBinding


class SignUp : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)


        //---------------------------------------------------------------------------------------//
        //initial view config
        //---------------------------------------------------------------------------------------//
        var binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Hide the action bar
        supportActionBar?.hide()

        //set status bar color
        window.statusBarColor = ContextCompat.getColor(this, R.color.dark_blue)

        //---------------------------------------------------------------------------------------//


        binding.btnSignUp.setOnClickListener()
        {
            var intent = Intent(this, Homepage::class.java)
            startActivity(intent)
        }

        binding.tvBack.setOnClickListener()
        {
            finish()
        }

        binding.spnSecurityQuestion.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                (view as TextView).setTextColor(Color.BLACK) //Change selected text color
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })


    }

}