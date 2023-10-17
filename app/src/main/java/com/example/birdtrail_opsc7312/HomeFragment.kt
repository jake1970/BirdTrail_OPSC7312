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
import android.widget.RelativeLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
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

        var fullMapView = FullMapFragment()
        fullMapView.openInFullView = true
        fragmentControl.replaceFragment(fullMapView, R.id.cvMapFragmentContainer, requireActivity().supportFragmentManager)

        //88888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888

        val activityLayout = binding.llBirdSummaryInfo

        //======================================================================================================

        //clostest sighting to user
        val closestGeneralSighting = Card_Observations_Species(activity)

        var pastDistance = 0.0
        var pastHotspot  = eBirdJson2KtKotlin()

        var userLocation: Location? = null
        var mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        // Define the permission request
        val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // Permission has been granted, proceed with your operation
                mFusedLocationClient.lastLocation.addOnCompleteListener(requireActivity()) { task ->
                    userLocation = task.result
                    if (userLocation != null) {
                        //GlobalScope.launch(Dispatchers.IO) {
                            // Your code here...


                            GlobalScope.launch (Dispatchers.IO) {
                                // Iterate over each hotspot in the global list
                                for (hotspot in GlobalClass.hotspots) {
                                    // Calculate the distance between the user's location and the hotspot.
                                    val distanceInKm = FullMapFragment().calculateDistance(userLocation!!.latitude, userLocation!!.longitude, hotspot.lat!!, hotspot.lng!!)
                                    // If the distance is less than or equal to 50km, add an annotation for this hotspot.
                                    if (distanceInKm <= 60 ) {
                                        if (distanceInKm <= pastDistance || pastDistance == 0.0)
                                        {
                                            pastDistance = distanceInKm
                                            pastHotspot = hotspot
                                        }
                                    }
                                }
                                withContext (Dispatchers.Main) {
                                    closestGeneralSighting.binding.tvSpecies.text = pastHotspot.comName
                                    closestGeneralSighting.binding.tvSighted.text = pastHotspot.obsDt
                                    activityLayout.addView(closestGeneralSighting)

                                }
                            }
                        //}
                    }
                }
            } else {
                // Permission has been denied, handle accordingly
            }
        }

        // Check if we have the permission
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // If not, request the permission
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        //======================================================================================================

        //lastest user sighting
        val latestUserSighting = Card_Observations_Species(activity)

        var observationlist = arrayListOf<UserObservationDataClass>()
        //get latest sighting
        for (i in 1..GlobalClass.userObservations.size)
        {
            if (GlobalClass.userObservations[i-1].userID == GlobalClass.currentUser.userID)
            {
                observationlist.add(GlobalClass.userObservations[i-1])
            }
        }
        var userSighting = observationlist.last()

        latestUserSighting.binding.tvSpecies.text = userSighting.birdName
        latestUserSighting.binding.tvSighted.text = userSighting.date.toString()

        //======================================================================================================
        //add componets
        //activityLayout.addView(closestGeneralSighting)



        //call method to generate a space under the dynamic component
        scrollViewTools.generateSpacer(activityLayout, requireActivity(), spacerSize)


        activityLayout.addView(latestUserSighting)
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




}