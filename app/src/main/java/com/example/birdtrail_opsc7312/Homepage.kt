package com.example.birdtrail_opsc7312

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.birdtrail_opsc7312.databinding.ActivityHomepageBinding
import com.example.birdtrail_opsc7312.databinding.LandingPageBinding

class Homepage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage)

        var binding = ActivityHomepageBinding.inflate(layoutInflater)
        setContentView(binding.root)



        //navigation bar
        binding.bottomNavigationView.background = null
        binding.bottomNavigationView.menu.getItem(2).isEnabled = false


    }
}