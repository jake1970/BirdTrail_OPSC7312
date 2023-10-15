package com.example.birdtrail_opsc7312

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.birdtrail_opsc7312.databinding.FragmentHomeBinding
import com.example.birdtrail_opsc7312.databinding.FragmentRankingBinding


/**
 * A simple [Fragment] subclass.
 * Use the [Ranking.newInstance] factory method to
 * create an instance of this fragment.
 */
class Ranking : Fragment() {

    private var _binding: FragmentRankingBinding? = null
    private val binding get() = _binding!!

    private val spacerSize = 14
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentRankingBinding.inflate(inflater, container, false)
        val view = binding.root

        //New Animation Handler Object
        val animationManager = AnimationHandler()

        //Load initial leaderboard View
        populateViewContent(true)

        binding.tvLeaderboardSwitch.setOnClickListener()
        {

            //Animate the bar to the left side of the screen
            animationManager.animateX(binding.vwSelectedView, 0f)

            //Load leaderboardView
            populateViewContent(true)
        }

        binding.tvLeaderboardSwitch.setOnClickListener()
        {
            //Generate the position of the tab indicator bar at the end (right) of the screen
           val moveToPosition = animationManager.getPositionInRelationToScreen(binding.vwSelectedView)

            //Animate the bar to the right side of the screen
            animationManager.animateX(binding.vwSelectedView, -moveToPosition.x.toFloat())

            //Load Achievements view
            populateViewContent(false)
        }

        // Inflate the layout for this fragment
        return view
    }

    //---------------------------------------------------------------------------------------------
    //Swap Ranking Views
    //---------------------------------------------------------------------------------------------
    private fun populateViewContent(leaderboardView: Boolean){

        //loop iterations
        val loopCount = 8

        //new scroll view handler object
        val scrollViewTools = ScrollViewHandler()

        //set the location for the dynamic components to be created in
        val activityLayout = binding.llRankingList;

        //check if there are existing dynamic components
        if (activityLayout.childCount != 0) {

            //clear the dynamic components
            activityLayout.removeAllViews()
        }

        //if all sightings must be loaded
        if (leaderboardView)
        {
            //loop through the sightings
            for (i in 1..loopCount) {

                //new dynamic component
                var newLeaderboardCard = Card_Leaderboard(activity)

                //add the dynamic component to the container view
                activityLayout.addView(newLeaderboardCard)

                //call method to generate a space under the dynamic component
                scrollViewTools.generateSpacer(activityLayout, requireActivity(), spacerSize)


            }
        }
        else
        {
            //if species summary must be loaded

            //loop through the species
            for (i in 1..loopCount) {


                //new dynamic component
                var newAchievementCard = Card_Achievement(activity)

                //add the dynamic component to the container view
                activityLayout.addView(newAchievementCard)

                //call method to generate a space under the dynamic component
                scrollViewTools.generateSpacer(activityLayout, requireActivity(), spacerSize)


            }
        }

    }
    //---------------------------------------------------------------------------------------------

}