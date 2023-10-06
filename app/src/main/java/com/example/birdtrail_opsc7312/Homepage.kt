package com.example.birdtrail_opsc7312

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.birdtrail_opsc7312.databinding.ActivityHomepageBinding
import com.example.birdtrail_opsc7312.databinding.LandingPageBinding

class Homepage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_homepage)

        var binding = ActivityHomepageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavigationView.background = null
        binding.bottomNavigationView.menu.getItem(2).isEnabled = false

        binding.bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.leaderboard -> {
                    // Start Leaderboard Activity
                    true
                }
                R.id.home -> {
                    // Start Home Activity
                    val intent = Intent(this, Homepage::class.java)
                    startActivity(intent)
                    true
                }
                R.id.observations -> {
                    // Start Observations Activity
                    true
                }
                R.id.settings -> {
                    // Start Settings Activity
                    val intent = Intent(this, SettingsPage::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }




    }
}