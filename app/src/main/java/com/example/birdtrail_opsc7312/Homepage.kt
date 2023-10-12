package com.example.birdtrail_opsc7312

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.example.birdtrail_opsc7312.databinding.ActivityHomepageBinding
import com.example.birdtrail_opsc7312.databinding.LandingPageBinding

class Homepage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_homepage)

        var binding = ActivityHomepageBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //Hide the action bar
        supportActionBar?.hide()

        //set status bar color
        window.statusBarColor = ContextCompat.getColor(this, R.color.dark_blue)


        //create local fragment controller
        val fragmentControl = FragmentHandler()

        fragmentControl.replaceFragment(HomeFragment(), R.id.flContent, supportFragmentManager)

        // replaceFragment(home())
        binding.bottomNavigationView.selectedItemId = R.id.home

        binding.fab.setOnClickListener(){
            fragmentControl.replaceFragment(Add_Observation(), R.id.flContent, supportFragmentManager)
            binding.bottomNavigationView.selectedItemId = R.id.placeholder
        }

        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId)
            {

                R.id.ranking -> fragmentControl.replaceFragment(Ranking(), R.id.flContent, supportFragmentManager)
                R.id.home -> fragmentControl.replaceFragment(HomeFragment(), R.id.flContent, supportFragmentManager)
                R.id.observations -> {}//fragmentControl.replaceFragment(contacts(), R.id.clContent, supportFragmentManager)
                R.id.settings -> fragmentControl.replaceFragment(AppSettings(), R.id.flContent, supportFragmentManager)


                else -> {

                }
            }
            true
        }




        /*
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
         */




    }
}