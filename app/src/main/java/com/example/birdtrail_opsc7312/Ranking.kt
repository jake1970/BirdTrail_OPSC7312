package com.example.birdtrail_opsc7312

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Insets
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsetsAnimation.Bounds
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import com.example.birdtrail_opsc7312.databinding.FragmentHomeBinding
import com.example.birdtrail_opsc7312.databinding.FragmentRankingBinding


/**
 * A simple [Fragment] subclass.
 * Use the [Ranking.newInstance] factory method to
 * create an instance of this fragment.
 */
@Suppress("DEPRECATION")
class Ranking : Fragment(R.layout.fragment_ranking) {

    private var _binding: FragmentRankingBinding? = null
    private val binding get() = _binding!!

    private val spacerSize = 14
    @RequiresApi(Build.VERSION_CODES.O)
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

        binding.tvLeaderboard.setOnClickListener()
        {

            //Animate the bar to the left side of the screen
            animationManager.animateX(binding.vwSelectedView, 0f)

            //Load leaderboardView
            populateViewContent(true)
        }

        binding.tvAchievements.setOnClickListener()
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
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ResourceAsColor")
    private fun populateViewContent(leaderboardView: Boolean){

        //loop iterations
        var loopCount = 8

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
            GlobalClass.currentUser.userID = 1
            //if species summary must be loaded
            loopCount = GlobalClass.acheivements.size
            //loop through the species
            for (i in 1..loopCount) {

                var achievmentUnlocked: Boolean = false

                var currentAchievemnt = GlobalClass.acheivements[i-1]
                //new dynamic component
                var newAchievementCard = Card_Achievement(activity)
                newAchievementCard.binding.tvAchievementName.text = currentAchievemnt.name
                newAchievementCard.binding.imgBadge.setImageBitmap(GlobalClass.badgeImages[i-1])
                newAchievementCard.binding.tvRequirements.text = currentAchievemnt.requirements

                for (j in 1..GlobalClass.userAchievements.size)
                {
                    var userAchievment = GlobalClass.userAchievements[j-1]
                    if (userAchievment.userID == GlobalClass.currentUser.userID &&  userAchievment.achID == currentAchievemnt.achID)
                    {
                        //unlocked achievement achievement``
                        newAchievementCard.binding.tvDate.text = userAchievment.date.toString()
                        newAchievementCard.binding.tvSelectorText.text = ""
                        newAchievementCard.binding.tvSelectorText.setPadding(0,72,0,0)
                        newAchievementCard.binding.rlSelector.background.setColorFilter(ContextCompat.getColor(requireContext(), R.color.dark_blue), android.graphics.PorterDuff.Mode.SRC_IN)
                        achievmentUnlocked = true
                        break
                    }
                }
                if (achievmentUnlocked == false)
                {
                    //locked achievement
                    newAchievementCard.binding.tvSelectorText.text = "99/100"
                    newAchievementCard.binding.tvSelectorText.setCompoundDrawables(null,null,null,null);
                    newAchievementCard.binding.tvSelectorText.setPadding(0,0,0,0)
                    newAchievementCard.binding.rlSelector.background.setColorFilter(ContextCompat.getColor(requireContext(), R.color.dark_blue), android.graphics.PorterDuff.Mode.SRC_IN)
                }

                //default is the select bird

                //add the dynamic component to the container view
                activityLayout.addView(newAchievementCard)

                //call method to generate a space under the dynamic component
                scrollViewTools.generateSpacer(activityLayout, requireActivity(), spacerSize)

            }
        }
    }
    //---------------------------------------------------------------------------------------------
}