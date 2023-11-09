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
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.drawToBitmap
import androidx.core.view.forEach
import androidx.core.view.isVisible
import androidx.core.view.iterator
import com.example.birdtrail_opsc7312.databinding.FragmentHomeBinding
import com.example.birdtrail_opsc7312.databinding.FragmentRankingBinding
import com.mapbox.common.location.GetLocationCallback
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date


@Suppress("DEPRECATION")
class Ranking : Fragment(R.layout.fragment_ranking) {

    //set the binding
    private var _binding: FragmentRankingBinding? = null
    private val binding get() = _binding!!

    //set the spacer size
    private val spacerSize = 14

    private lateinit var loadingProgressBar : ViewGroup
    private var initialBadgeID : Int = 0

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //binding
        _binding = FragmentRankingBinding.inflate(inflater, container, false)
        val view = binding.root

        loadingProgressBar = layoutInflater.inflate(R.layout.loading_cover, null) as ViewGroup
        loadingProgressBar.visibility = View.GONE
        view.addView(loadingProgressBar)

        MainScope().launch {

            if (GlobalClass.UpdateDataBase == true) {

                loadingProgressBar.visibility = View.VISIBLE

                withContext(Dispatchers.Default) {
                    val databaseManager = DatabaseHandler()
                    databaseManager.updateLocalData()
                }

            }
            updateUI()
        }

        // Inflate the layout for this fragment
        return view
    }


    override fun onDestroyView() {
        if ((initialBadgeID != GlobalClass.currentUser.badgeID))
        {

            val databaseManager = DatabaseHandler()

            GlobalScope.launch {

                databaseManager.updateUser(GlobalClass.currentUser)

                withContext(Dispatchers.Main) {

                }
            }
        }
        super.onDestroyView()

    }


    private fun updateUI()
    {

        initialBadgeID = GlobalClass.currentUser.badgeID

        //New Animation Handler Object
        val animationManager = AnimationHandler()

        //Load initial leaderboard View
        populateViewContent(true)

        //when the leaderboard button is clicked
        binding.tvLeaderboard.setOnClickListener()
        {

            //Animate the bar to the left side of the screen
            animationManager.animateX(binding.vwSelectedView, 0f)

            //Load leaderboardView
            populateViewContent(true)
        }

        //when the achievement button is clicked
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

            //loop through the achievements in the achievements list view
            for (unlockedAchievement in activityLayout)
            {
                //if the achievement is unlocked and selected by the user (currently active)
                if (unlockedAchievement is Card_Achievement && unlockedAchievement.binding.tvDate.visibility != TextView.GONE && unlockedAchievement.badgeID == GlobalClass.currentUser.badgeID) {

                    //call the on click method to select the achievement
                    unlockedAchievement.callOnClick()

                    //exit the loop
                    break
                }
            }
        }



        //the amount of achievements the user has unlocked
        var unlockedAchievements = 0

        //loop through the achievements unlocked by all users
        for (userAchievement in GlobalClass.userAchievements)
        {
            //if the achievement was unlocked by the current user
            if (userAchievement.userID == GlobalClass.currentUser.userID)
            {
                //increment the users unlocked achievement counter
                unlockedAchievements++
            }
        }

        //display the users score
        binding.tvScoreValue.text = " ${GlobalClass.currentUser.score}"

        //display the amount of achievements the user has unlocked
        binding.tvAchievementValue.text = "  ${unlockedAchievements} / ${GlobalClass.acheivements.size}"

        //display the users profile picture
        binding.imgMyProfileImage.setImageBitmap(GlobalClass.currentUser.profilepicture)

        //display the users badge
        binding.imgBadge.setImageBitmap(GlobalClass.badgeImages[GlobalClass.currentUser.badgeID])

        loadingProgressBar.visibility = View.GONE

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

        //if leaderboard must be loaded
        if (leaderboardView)
        {

            //holds an instance of the user data list
            var sortedUsers = GlobalClass.userData

            //sort users by their score values
            sortedUsers.sortWith(compareBy { it.score })

            //reverse the list to sort in descending
            sortedUsers.reverse()

            //the users position in the ranking
            var userRankingPosition = 0

            //variable to hold the last added users score
            var pastuserScore = -1


            //loop through users
            for (user in sortedUsers) {

                //the amount of achievements the user has
                var userUnlockedAchievements = 0

                //loop through the user achievements
                for (userAchievement in GlobalClass.userAchievements)
                {
                    //if the achievement belongs to the user
                    if (userAchievement.userID == user.userID)
                    {
                        //increment the unlocked achievement counter
                        userUnlockedAchievements++
                    }
                }


                //new dynamic component to hold the users leaderboard data
                var newLeaderboardCard = Card_Leaderboard(activity)

                //set the users leaderboard cards profile image
               // newLeaderboardCard.binding.imgMyProfileImage.setImageBitmap(user.profilepicture)

                //set the users leaderboard cards badge image
                newLeaderboardCard.binding.imgBadge.setImageBitmap(GlobalClass.badgeImages[user.badgeID])

                //set the users leaderboard cards username
                newLeaderboardCard.binding.tvUsername.text = user.username

                //set the users leaderboard cards score
                newLeaderboardCard.binding.tvScore.text = user.score.toString()

                //set the users leaderboard cards achievement count information
                newLeaderboardCard.binding.tvBadges.text = "${userUnlockedAchievements} / ${GlobalClass.acheivements.size}"

                //check if the users score is not the same as the past users score
                if (pastuserScore != user.score) {
                    //increment the ranking position
                    userRankingPosition++
                }

                //set the prvious users score the the current user score
                pastuserScore = user.score

                //set the users leaderboard cards ranking
                newLeaderboardCard.binding.tvRankingPlace.text = userRankingPosition.toString()

                //check if the user is the currently signed in user
                if (user.userID == GlobalClass.currentUser.userID)
                {
                    //set the users leaderboard card to be unique from the other leaderboard cards
                    newLeaderboardCard.binding.rlIdentityBacking.background.setColorFilter(ContextCompat.getColor(requireContext(), R.color.dark_blue), android.graphics.PorterDuff.Mode.SRC_IN)
                    newLeaderboardCard.binding.imgMyProfileImage.setImageBitmap(GlobalClass.currentUser.profilepicture)

                }
                else
                {
                    newLeaderboardCard.binding.imgMyProfileImage.setImageBitmap(requireActivity().getDrawable(R.drawable.imgdefaultprofile)?.toBitmap())
                }


                //add the dynamic component to the container view
                activityLayout.addView(newLeaderboardCard)

                //call method to generate a space under the dynamic component
                scrollViewTools.generateSpacer(activityLayout, requireActivity(), spacerSize)

            }
        }
        else
        {
            //if the achievements must be loaded

            //the current badge counter
            var currentBadge = 0

            //loop through the achievements
            for (achievement in GlobalClass.acheivements) {


                //if the achievement is unlocked by the user or not
                var achievementUnlocked = false

                //new dynamic component
                var newAchievementCard = Card_Achievement(activity)

                //hide the date component on the achievement card
                newAchievementCard.binding.tvDate.visibility = EditText.GONE

                //set the achievement card achievement name
                newAchievementCard.binding.tvAchievementName.text = achievement.name

                //set the achievement card badge image
                newAchievementCard.binding.imgBadge.setImageBitmap(GlobalClass.badgeImages[currentBadge++])

                //set the achievement card badge id
                newAchievementCard.badgeID = currentBadge-1

                //set the achievement card requirements value
                newAchievementCard.binding.tvRequirements.text = achievement.requirements


                //loop through the unlocked achievements
                for (unlockedAchievement in GlobalClass.userAchievements)
                {

                    //if the unlocked achievement was unlocked by the user
                    if ((unlockedAchievement.userID == GlobalClass.currentUser.userID && unlockedAchievement.achID == achievement.achID))
                    {

                        //call method to set the achievement cards style to be unlocked
                        NewAchievementUnlockedStyle(newAchievementCard, unlockedAchievement.date)

                        //set the achievements unlocked status to true
                        achievementUnlocked = true

                        //when the achievement card is clicked
                        newAchievementCard.setOnClickListener()
                        {

                            //loop through the unlocked achievements
                            for (achievement in activityLayout)
                            {
                                //if the achievement is unlocked
                                if (achievement is Card_Achievement && achievement.binding.tvDate.visibility != TextView.GONE) {

                                    //call method to reset the unlocked achievement card style
                                    ExistingAchievementUnlockedStyle(achievement)
                                }
                            }

                            //call method to reset the style of the achievement card to show its been selected
                            ResetAchievementCard(newAchievementCard)

                            //set the users badge id to the selected achievement badge id
                            GlobalClass.currentUser.badgeID = newAchievementCard.badgeID

                            //set the users badge to the selected achievement badge
                            binding.imgBadge.setImageBitmap(GlobalClass.badgeImages[GlobalClass.currentUser.badgeID])
                        }

                        //exit the loop
                        break
                    }

                }


                //if the achievement is not unlocked
                if (achievementUnlocked == false)
                {
                    //locked achievement
                    //set styling to locked achievement

                    newAchievementCard.binding.tvSelectorText.text = "${GlobalClass.totalObservations} / ${achievement.observationsRequired}"
                    newAchievementCard.binding.tvSelectorText.setCompoundDrawables(null,null,null,null);
                    newAchievementCard.binding.tvSelectorText.setPadding(0,0,0,0)
                    newAchievementCard.binding.rlSelector.background.setColorFilter(ContextCompat.getColor(requireContext(), R.color.dark_blue), android.graphics.PorterDuff.Mode.SRC_IN)
                }



                //add the dynamic component to the container view
                activityLayout.addView(newAchievementCard)

                //call method to generate a space under the dynamic component
                scrollViewTools.generateSpacer(activityLayout, requireActivity(), spacerSize)




            }
        }
    }
    //---------------------------------------------------------------------------------------------



    //---------------------------------------------------------------------------------------------
    //method to reset the achievement card to show an achievement has been selected
    //---------------------------------------------------------------------------------------------
    private fun ResetAchievementCard(newAchievementCard : Card_Achievement)
    {

        var refreshCard = Card_Achievement(activity)

        refreshCard.binding.tvDate.text = newAchievementCard.binding.tvDate.text

        newAchievementCard.binding.tvSelectorText.text = getString(R.string.selectedText)
        newAchievementCard.binding.tvSelectorText.setPadding(0,36,0,0)
        newAchievementCard.binding.rlSelector.background.setColorFilter(ContextCompat.getColor(requireContext(), R.color.confirmation_green), android.graphics.PorterDuff.Mode.SRC_IN)
    }
    //---------------------------------------------------------------------------------------------



    //---------------------------------------------------------------------------------------------
    //method to reset the achievement card to show an unlocked achievement that is not currently selected
    //---------------------------------------------------------------------------------------------
    private fun ExistingAchievementUnlockedStyle(newAchievementCard : Card_Achievement)
    {
        newAchievementCard.binding.tvSelectorText.text = ""
        newAchievementCard.binding.tvSelectorText.setPadding(0,76,0,0)
        newAchievementCard.binding.rlSelector.background.setColorFilter(ContextCompat.getColor(requireContext(), R.color.dark_blue), android.graphics.PorterDuff.Mode.SRC_IN)
    }
    //---------------------------------------------------------------------------------------------


    //---------------------------------------------------------------------------------------------
    //method to set the style of a newly unlocked achievement card
    //---------------------------------------------------------------------------------------------
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
    //---------------------------------------------------------------------------------------------

}