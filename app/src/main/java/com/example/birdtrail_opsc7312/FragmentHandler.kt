package com.example.birdtrail_opsc7312

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

class FragmentHandler {

    fun replaceFragment(fragment : Fragment, fragmentContainerID : Int, fragmentManager : FragmentManager) {

        //begin transition to desired fragment
        val fragmentTransaction = fragmentManager.beginTransaction()

        //set the desired fragment to be swapped
        fragmentTransaction.replace(fragmentContainerID, fragment)

        //commit fragment change
        fragmentTransaction.commit()
    }

}