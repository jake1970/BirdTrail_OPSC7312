package com.example.birdtrail_opsc7312

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.children
import com.example.birdtrail_opsc7312.databinding.ActivityHomepageBinding
import com.example.birdtrail_opsc7312.databinding.FragmentHomeBinding
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.internal.wait


class HomeFragment : Fragment(R.layout.fragment_home) {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val spacerSize = 20
    private lateinit var fullMapView: FullMapFragment



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        //show user info
        binding.tvUsername.text = GlobalClass.currentUser.username
        binding.imgMyProfileImage.setImageBitmap(GlobalClass.currentUser.profilepicture)
        binding.imgBadge.setImageBitmap(GlobalClass.badgeImages[GlobalClass.currentUser.badgeID])


        //create local fragment controller
        val fragmentControl = FragmentHandler()


        //new scroll view handler object
        val scrollViewTools = ScrollViewHandler()


        //88888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888

        //var fullMapView = FullMapFragment()
        fullMapView = FullMapFragment()
        fullMapView.openInFullView = true
        fragmentControl.replaceFragment(
            fullMapView,
            R.id.cvMapFragmentContainer,
            requireActivity().supportFragmentManager
        )

        //88888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888

        val activityLayout = binding.llBirdSummaryInfo

        //======================================================================================================

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
            else
            {


                //if the user doesnt have this achievement
                nextAchievement.binding.imgBadge.setImageBitmap(GlobalClass.badgeImages[achievement.badgeIndex])
                nextAchievement.binding.tvAchievementName.text = achievement.name
                nextAchievement.binding.tvDate.visibility = EditText.GONE
                nextAchievement.binding.tvRequirements.text = achievement.requirements
                nextAchievement.binding.tvSelectorText.text = "${GlobalClass.totalObservations} / ${achievement.observationsRequired}"
                nextAchievement.binding.tvSelectorText.setCompoundDrawables(null,null,null,null);
                nextAchievement.binding.tvSelectorText.setPadding(0,0,0,0)
                nextAchievement.binding.rlSelector.background.setColorFilter(ContextCompat.getColor(requireContext(), R.color.medium_blue), android.graphics.PorterDuff.Mode.SRC_IN)
                break
            }
        }

        // Create a TextView
        val nextAchievementText = TextView(requireContext()).apply {
            text = "Next Achievement" // replace with actual username
            textSize = 24f // this is in SP (scale-independent pixels), not DP
            setTextColor(ContextCompat.getColor(context, R.color.white)) // replace with actual color resource
            setPadding(8, 0, 0, 0) // assuming you want the left margin to be padding here
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        activityLayout.addView(nextAchievementText)
        activityLayout.addView(nextAchievement)


        //======================================================================================================

        //lastest user sighting
        val latestUserSighting = Card_Observations_Species(activity)

        var observationlist = arrayListOf<UserObservationDataClass>()
        //get latest sighting


        for (i in 1..GlobalClass.userObservations.size) {
            if (GlobalClass.userObservations[i - 1].userID == GlobalClass.currentUser.userID) {
                observationlist.add(GlobalClass.userObservations[i - 1])
            }
        }

        scrollViewTools.generateSpacer(activityLayout, requireActivity(), spacerSize)

        if (observationlist.isEmpty())
        {
            //add default add card
/*
            var addDataCard = Card_Observations_Species(activity)

            addDataCard.binding.imgBirdImage.setImageBitmap(AppCompatResources.getDrawable(requireContext(), R.drawable.imgplus)
                ?.toBitmap())
            addDataCard.binding.tvSpecies.text = ""
            addDataCard.binding.tvSpecies.visibility = View.GONE
            addDataCard.binding.tvSighted.text = getString(R.string.noObservations)


            //addDataCard.binding.tvSighted.textSize = 60f

            activityLayout.addView(addDataCard)

 */
            GlobalClass.generateObservationPrompt(activityLayout, requireContext(), parentFragmentManager)
        }
        else
        {

            var userSighting = observationlist.last()

            latestUserSighting.binding.tvSpecies.text = userSighting.birdName
            latestUserSighting.binding.tvSighted.text = userSighting.date.toString()


            // Create a TextView
            val latestObservationText = TextView(requireContext()).apply {
                text = "Your Latest Observation" // replace with actual username
                textSize = 24f // this is in SP (scale-independent pixels), not DP
                setTextColor(ContextCompat.getColor(context, R.color.white)) // replace with actual color resource
                setPadding(8, 0, 0, 0) // assuming you want the left margin to be padding here
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }

            activityLayout.addView(latestObservationText)
            activityLayout.addView(latestUserSighting)

        }





        //======================================================================================================
        //add componets
        //activityLayout.addView(closestGeneralSighting)

        //call method to generate a space under the dynamic component

        //call method to generate a space under the dynamic component
        scrollViewTools.generateSpacer(activityLayout, requireActivity(), spacerSize)
        //======================================================================================================


        //MostRecentGeneralSighting
        //NearestToUserSighting/Hotspot

//        binding.btnNav.setOnClickListener(){
//            val transaction = parentFragmentManager.beginTransaction()
//            transaction.replace(R.id.flContent, MapDirections())
//            transaction.addToBackStack(null)
//            transaction.commit()
//        }


        return view
    }

//    suspend public fun getUserLocation() : Location?
//    {
//        if (this::fullMapView.isInitialized == false)
//        {
//            fullMapView = FullMapFragment()
//        }
//
//        return fullMapView.getUserLocation()
//    }




//    fun getLocationAndFindClosestHotspot() {
//        mFusedLocationClient.lastLocation.addOnCompleteListener(requireActivity()) { task ->
//            userLocation = task.result
//            if (userLocation != null) {
//                GlobalScope.launch(Dispatchers.IO) {
//                    // Your code here...
//
//                    for (hotspot in GlobalClass.hotspots) {
//                        // Calculate the distance between the user's location and the hotspot.
//                        val distanceInKm = FullMapFragment().calculateDistance(
//                            userLocation!!.latitude,
//                            userLocation!!.longitude,
//                            hotspot.lat!!,
//                            hotspot.lng!!
//                        )
//                        // If the distance is less than or equal to 50km, add an annotation for this hotspot.
//                        if (distanceInKm <= 60) {
//                            if (distanceInKm <= pastDistance || pastDistance == 0.0) {
//                                pastDistance = distanceInKm
//                                pastHotspot = hotspot
//                            }
//                        }
//                    }
//                    withContext(Dispatchers.Main) {
//                        closestGeneralSighting.binding.tvSpecies.text = pastHotspot.comName
//                        closestGeneralSighting.binding.tvSighted.text = pastHotspot.obsDt
//                        activityLayout.addView(closestGeneralSighting)
//                    }
//                }
//            }
//        }
//    }
}
