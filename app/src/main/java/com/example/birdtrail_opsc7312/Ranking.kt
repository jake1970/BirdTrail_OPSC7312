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
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.drawToBitmap
import androidx.core.view.forEach
import androidx.core.view.isVisible
import androidx.core.view.iterator
import com.example.birdtrail_opsc7312.databinding.FragmentHomeBinding
import com.example.birdtrail_opsc7312.databinding.FragmentRankingBinding
import com.mapbox.common.location.GetLocationCallback
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date


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

            //set the location for the dynamic components to be searched
            val activityLayout = binding.llRankingList;

            for (unlockedAchievement in activityLayout)
            {
                if (unlockedAchievement is Card_Achievement && unlockedAchievement.binding.tvDate.visibility != TextView.GONE && unlockedAchievement.badgeID == GlobalClass.currentUser.badgeID) {

                    unlockedAchievement.callOnClick()
                    break
                }
            }


        }


        //-----------------------------------------------------------------------------------------
        //populate ranking header
        //-----------------------------------------------------------------------------------------

        var unlockedAchievements = 0

        for (userAchievement in GlobalClass.userAchievements)
        {
            if (userAchievement.userID == GlobalClass.currentUser.userID)
            {
                unlockedAchievements++
            }
        }

        binding.tvScoreValue.text = " ${GlobalClass.currentUser.score}"
        binding.tvAchievementValue.text = "  ${unlockedAchievements} / ${GlobalClass.acheivements.size}"
        binding.imgMyProfileImage.setImageBitmap(GlobalClass.currentUser.profilepicture)
        binding.imgBadge.setImageBitmap(GlobalClass.badgeImages[GlobalClass.currentUser.badgeID])

        //-----------------------------------------------------------------------------------------


        // Inflate the layout for this fragment
        return view
    }

    //---------------------------------------------------------------------------------------------
    //Swap Ranking Views
    //---------------------------------------------------------------------------------------------
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ResourceAsColor")
    private fun populateViewContent(leaderboardView: Boolean){


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

            //GlobalClass.userData[1].score = 20

            var sortedUsers = GlobalClass.userData
            sortedUsers.sortWith(compareBy { it.score })


            var userRankingPosition = 0
            var pastuserScore = -1

            //loop through the sightings
            for (user in sortedUsers) {


                    //new dynamic component
                    var newLeaderboardCard = Card_Leaderboard(activity)
                    newLeaderboardCard.binding.imgMyProfileImage.setImageBitmap(user.profilepicture)
                    newLeaderboardCard.binding.imgBadge.setImageBitmap(GlobalClass.badgeImages[user.badgeID])
                    newLeaderboardCard.binding.tvUsername.text = user.username
                    newLeaderboardCard.binding.tvScore.text = user.score.toString()

                    if (pastuserScore != user.score) {
                        userRankingPosition++
                    }

                    pastuserScore = user.score

                    newLeaderboardCard.binding.tvRankingPlace.text = userRankingPosition.toString()//i.toString()


                    if (user.userID == GlobalClass.currentUser.userID)
                    {
                        newLeaderboardCard.binding.rlIdentityBacking.background.setColorFilter(ContextCompat.getColor(requireContext(), R.color.dark_blue), android.graphics.PorterDuff.Mode.SRC_IN)
                    }


                    //add the dynamic component to the container view
                    activityLayout.addView(newLeaderboardCard)

                    //call method to generate a space under the dynamic component
                    scrollViewTools.generateSpacer(activityLayout, requireActivity(), spacerSize)

            }

            /*
            var sortedUsers = GlobalClass.userData
            sortedUsers.sortWith(compareBy { it.score })

            //loop through the sightings
            for (i in 1..sortedUsers.size) {

                var user = sortedUsers[i-1]

                if (user.userID != GlobalClass.currentUser.userID) //other users
                {
                    //new dynamic component
                    var newLeaderboardCard = Card_Leaderboard(activity)
                    newLeaderboardCard.binding.imgMyProfileImage.setImageBitmap(user.profilepicture)
                    newLeaderboardCard.binding.imgBadge.setImageBitmap(GlobalClass.badgeImages[user.badgeID])
                    newLeaderboardCard.binding.tvUsername.text = user.username
                    newLeaderboardCard.binding.tvScore.text = user.score.toString()
                    newLeaderboardCard.binding.tvRankingPlace.text = i.toString()

                    //add the dynamic component to the container view
                    activityLayout.addView(newLeaderboardCard)

                    //call method to generate a space under the dynamic component
                    scrollViewTools.generateSpacer(activityLayout, requireActivity(), spacerSize)
                }
                else //current user
                {
                    binding.tvScoreValue.text = GlobalClass.currentUser.score.toString()
                    binding.tvAchievementValue.text = i.toString()
                    binding.imgMyProfileImage.setImageBitmap(GlobalClass.currentUser.profilepicture)
                    binding.imgBadge.setImageBitmap(GlobalClass.badgeImages[GlobalClass.currentUser.badgeID])
                }
            }

             */
        }
        else //get Achievements
        {
            //GlobalClass.currentUser.userID = 1
            //if achievements view must be loaded

            //2023-10-17


            GlobalClass.userAchievements.add(
            UserAchievementsDataClass(
                userID = GlobalClass.currentUser.userID,
                achID = 0,
                date = GlobalClass.currentUser.registrationDate,
            )
            )


            var currentBadge = 0

            for (achievement in GlobalClass.acheivements) {



                var achievementUnlocked = false

                //new dynamic component
                var newAchievementCard = Card_Achievement(activity)

                newAchievementCard.binding.tvDate.visibility = EditText.GONE

                newAchievementCard.binding.tvAchievementName.text = achievement.name
                newAchievementCard.binding.imgBadge.setImageBitmap(GlobalClass.badgeImages[currentBadge++])
                newAchievementCard.badgeID = currentBadge-1 //-1
                newAchievementCard.binding.tvRequirements.text = achievement.requirements

                for (unlockedAchievement in GlobalClass.userAchievements)
                {
                    Toast.makeText(activity, "for", Toast.LENGTH_SHORT).show()

                    if ((unlockedAchievement.userID == GlobalClass.currentUser.userID && unlockedAchievement.achID == achievement.achID))
                    {

                        Toast.makeText(activity, "if", Toast.LENGTH_SHORT).show()

                        NewAchievementUnlockedStyle(newAchievementCard, unlockedAchievement.date)
                        achievementUnlocked = true


                        newAchievementCard.setOnClickListener()
                        {
                            //Toast.makeText(activity, "Clicked", Toast.LENGTH_SHORT).show()

                            for (achievement in activityLayout)
                            {
                                if (achievement is Card_Achievement && achievement.binding.tvDate.visibility != TextView.GONE) {
                                    //Toast.makeText(activity, "Clicked", Toast.LENGTH_SHORT).show()
                                    ExistingAchievementUnlockedStyle(achievement)
                                }
                            }

                            ResetAchievementCard(newAchievementCard)
                            GlobalClass.currentUser.badgeID = newAchievementCard.badgeID
                            binding.imgBadge.setImageBitmap(GlobalClass.badgeImages[GlobalClass.currentUser.badgeID])
                        }

                        break
                    }

                }


                if (achievementUnlocked == false)
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

    private fun ResetAchievementCard(newAchievementCard : Card_Achievement)
    {

        var refreshCard = Card_Achievement(activity)

        refreshCard.binding.tvDate.text = newAchievementCard.binding.tvDate.text

        newAchievementCard.binding.tvSelectorText.text = getString(R.string.selectedText)
        newAchievementCard.binding.tvSelectorText.setPadding(0,32,0,0)
        newAchievementCard.binding.rlSelector.background.setColorFilter(ContextCompat.getColor(requireContext(), R.color.confirmation_green), android.graphics.PorterDuff.Mode.SRC_IN)
    }


    private fun ExistingAchievementUnlockedStyle(newAchievementCard : Card_Achievement)
    {
        newAchievementCard.binding.tvSelectorText.text = ""
        newAchievementCard.binding.tvSelectorText.setPadding(0,76,0,0)
        newAchievementCard.binding.rlSelector.background.setColorFilter(ContextCompat.getColor(requireContext(), R.color.dark_blue), android.graphics.PorterDuff.Mode.SRC_IN)
    }


    private fun NewAchievementUnlockedStyle(newAchievementCard : Card_Achievement, unlockedAchievementDate: LocalDate,)
    {
        // Define the date format pattern for your input string
        val inputPattern = "yyyy-MM-dd"
        // Define the desired date format pattern for the output
        val outputPattern = "yy-MM-dd"

        val inputFormat = SimpleDateFormat(inputPattern)
        val outputFormat = SimpleDateFormat(outputPattern)

        //unlocked achievement achievement
        newAchievementCard.binding.tvDate.visibility = EditText.VISIBLE

        var date = inputFormat.parse(unlockedAchievementDate.toString())
        var localDate = outputFormat.format(date)

        newAchievementCard.binding.tvDate.text = localDate
        newAchievementCard.binding.tvSelectorText.text = ""
        newAchievementCard.binding.tvSelectorText.setPadding(0,76,0,0)
        newAchievementCard.binding.rlSelector.background.setColorFilter(ContextCompat.getColor(requireContext(), R.color.dark_blue), android.graphics.PorterDuff.Mode.SRC_IN)

    }

}