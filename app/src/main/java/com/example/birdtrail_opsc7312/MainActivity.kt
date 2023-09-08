package com.example.birdtrail_opsc7312

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.example.birdtrail_opsc7312.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //---------------------------------------------------------------------------------------//
        //initial view config
        //---------------------------------------------------------------------------------------//
        var binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Hide the action bar
        supportActionBar?.hide()

        //set status bar color
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)
        //---------------------------------------------------------------------------------------//
    }
}