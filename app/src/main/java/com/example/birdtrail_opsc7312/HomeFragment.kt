package com.example.birdtrail_opsc7312

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.birdtrail_opsc7312.databinding.FragmentHomeBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment(R.layout.fragment_home) {

    //binding
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val spacerSize = 20
    private lateinit var fullMapView: FullMapFragment

    private lateinit var loadingProgressBar : ViewGroup

    //---------------------------------------------------------------------------------------------

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        MainScope().launch {
            loadingProgressBar = layoutInflater.inflate(R.layout.loading_cover, null) as ViewGroup
            view.addView(loadingProgressBar)

            if (GlobalClass.UpdateDataBase == true) {

                try
                {
                    withContext(Dispatchers.Default) {
                        val databaseManager = DatabaseHandler()
                        databaseManager.updateLocalData()
                    }
                }
                catch (e: Exception)
                {
                    GlobalClass.InformUser(getString(R.string.errorText),"$e", requireContext())
                }
            }

            updateUI()
        }
        return view
    }




    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateUI()
    {
        try
        {



            //show user info
            binding.tvUsername.text = GlobalClass.currentUser.username
            binding.imgMyProfileImage.setImageBitmap(GlobalClass.currentUser.profilepicture)
            binding.imgBadge.setImageBitmap(GlobalClass.badgeImages[GlobalClass.currentUser.badgeID])

            //create local fragment controller
            val fragmentControl = FragmentHandler()

            //new scroll view handler object
            val scrollViewTools = ScrollViewHandler()

            //var fullMapView = FullMapFragment()
            fullMapView = FullMapFragment()
            fullMapView.openInFullView = true

            fragmentControl.replaceFragment(
                fullMapView,
                R.id.cvMapFragmentContainer,
                requireActivity().supportFragmentManager
            )

            //---------------------------------------------------------------------------------------------

            val activityLayout = binding.llBirdSummaryInfo

            //---------------------------------------------------------------------------------------------
            //show user's next achievement to get

            val nextAchievement = Card_Achievement(activity)

            var currentUserAchievementListAchID = arrayListOf<Int>()
            for (achievement in GlobalClass.userAchievements)
            {
                if (achievement.userID == GlobalClass.currentUser.userID)
                {
                    currentUserAchievementListAchID.add(achievement.achID)
                }
            }

            for (achievement in GlobalClass.acheivements) //ach 1
            {
                if (currentUserAchievementListAchID.contains(achievement.achID))
                {
                    //if the user has this achievement
                }
                else {

                    //check that the achievement is not the initial starter achievement
                    if (achievement.achID != 0) {

                        //if the user doesnt have this achievement
                        nextAchievement.binding.imgBadge.setImageBitmap(GlobalClass.badgeImages[achievement.badgeIndex])
                        nextAchievement.binding.tvAchievementName.text = achievement.name
                        nextAchievement.binding.tvDate.visibility = EditText.GONE
                        nextAchievement.binding.tvRequirements.text = achievement.requirements
                        nextAchievement.binding.tvSelectorText.text =
                            "${GlobalClass.totalObservations} / ${achievement.observationsRequired}"
                        nextAchievement.binding.tvSelectorText.setCompoundDrawables(
                            null,
                            null,
                            null,
                            null
                        );
                        nextAchievement.binding.tvSelectorText.setPadding(0, 0, 0, 0)
                        nextAchievement.binding.rlSelector.background.setColorFilter(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.medium_blue
                            ), android.graphics.PorterDuff.Mode.SRC_IN
                        )
                        break
                    }
                }
            }

            // Create a TextView
            val nextAchievementText = TextView(requireContext()).apply {
                text = getString(R.string.nextAchievement) // replace with actual username
                textSize = 18f // this is in SP (scale-independent pixels), not DP
                setTextColor(ContextCompat.getColor(context, R.color.white)) // replace with actual color resource
                setPadding(8, 0, 0, 6) // assuming you want the left margin to be padding here
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }

            activityLayout.addView(nextAchievementText)
            activityLayout.addView(nextAchievement)

            //---------------------------------------------------------------------------------------------
            //show latest user observation

            //latest user sighting
            val latestUserSighting = Card_Observations_Species(activity)

            var observationlist = arrayListOf<UserObservationDataClass>()
            //get latest sighting

            for (userObservation in GlobalClass.userObservations)
            {
                if (userObservation.userID == GlobalClass.currentUser.userID)
                {
                    observationlist.add(userObservation)
                }
            }

            //call method to generate a space under the dynamic component
            scrollViewTools.generateSpacer(activityLayout, requireActivity(), spacerSize)

            if (observationlist.isEmpty())
            {
                GlobalClass.generateObservationPrompt(activityLayout, requireContext(), parentFragmentManager)
            }
            else
            {
                var userSighting = GlobalClass.getLastestObservation()

                if (userSighting != null)
                {
                    latestUserSighting.binding.tvSpecies.text = userSighting.birdName
                    latestUserSighting.binding.tvSighted.text = userSighting.date.toString()

                    // Create a TextView
                    val latestObservationText = TextView(requireContext()).apply {
                        text = "Your Latest Observation" // replace with actual username
                        textSize = 18f // this is in SP (scale-independent pixels), not DP
                        setTextColor(ContextCompat.getColor(context, R.color.white)) // replace with actual color resource
                        setPadding(8, 0, 0, 12) // assuming you want the left margin to be padding here
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                    }
                    activityLayout.addView(latestObservationText)
                    activityLayout.addView(latestUserSighting)
                }
            }

            //call method to generate a space under the dynamic component
            scrollViewTools.generateSpacer(activityLayout, requireActivity(), spacerSize)

        }catch (e: Exception)
        {
            GlobalClass.InformUser(getString(R.string.errorText),"${e.toString()}", requireContext())
        }

        loadingProgressBar.visibility = View.GONE
    }
}
