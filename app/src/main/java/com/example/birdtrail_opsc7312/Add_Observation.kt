package com.example.birdtrail_opsc7312

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Space
import androidx.core.view.forEach
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.example.birdtrail_opsc7312.databinding.FragmentAddObservationBinding
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.*


/**
 * A simple [Fragment] subclass.
 * Use the [Add_Observation.newInstance] factory method to
 * create an instance of this fragment.
 */
class Add_Observation : Fragment() {

    private var _binding: FragmentAddObservationBinding? = null
    private val binding get() = _binding!!

    @SuppressLint("MissingPermission")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        _binding = FragmentAddObservationBinding.inflate(inflater, container, false)
        val view = binding.root

        //---------------------------------------------------------------------------------------------------------






        //---------------------------------------------------------------------------------------------------------

        binding.btnBack.setOnClickListener()
        {
            val fragmentControl = FragmentHandler()
            //just for now, will return back to whichever screen you were previously on
            fragmentControl.replaceFragment(HomeFragment(), R.id.flContent, parentFragmentManager)
        }



        binding.tvNumberOfSightings.text = "1"


        binding.imgMinusSighting.setOnClickListener()
        {

            var currentSightingValue = (binding.tvNumberOfSightings.text as String).toInt()

            if (currentSightingValue > 1 )
            {
                binding.tvNumberOfSightings.text = (--currentSightingValue).toString()

            }

        }

        binding.imgPlusSighting.setOnClickListener()
        {
            var currentSightingValue = (binding.tvNumberOfSightings.text as String).toInt()

            if (currentSightingValue < 60 )
            {
                binding.tvNumberOfSightings.text = (++currentSightingValue).toString()
            }
        }







        //---------------------------------------------------------------------------------------------
        //Populate list of birds to choose from
        //---------------------------------------------------------------------------------------------


       // GlobalScope.launch (Dispatchers.IO) {




        fun populateBirdOptions(searchList: ArrayList<String>) {


            for (birdName in searchList)
            {

                val activityLayout = binding.llBirdList;

                var birdOption = Card_SpeciesSelector(activity)


                birdOption.binding.rlSelector.visibility = View.INVISIBLE
                birdOption.binding.tvSpecies.text = birdName
                //birdOption.binding.tvContactRole.text = "Senior Member"

                //add the new view
                activityLayout.addView(birdOption)

                birdOption.setOnClickListener()
                {

                    activityLayout.forEach { childView ->
                        // do something with this childView

                        if (childView is Card_SpeciesSelector) {
                            childView.binding.rlSelector.visibility = View.INVISIBLE
                        }

                    }

                    birdOption.binding.rlSelector.visibility = View.VISIBLE
                    binding.tvSpeciesName.text = birdOption.binding.tvSpecies.text
                }


                val scale = requireActivity().resources.displayMetrics.density
                val pixels = (14 * scale + 0.5f)

                val spacer = Space(activity)
                spacer.minimumHeight = pixels.toInt()
                activityLayout.addView(spacer)

            }
        }


            //withContext (Dispatchers.Main) {
           // }
        //}

        var uniqueBirdList =  ArrayList<String>()

        for (birds in GlobalClass.hotspots)
        {
            if (!uniqueBirdList.contains(birds.comName))
            {
                uniqueBirdList.add(birds.comName.toString())
            }
        }

        uniqueBirdList.sort()

        //check if there are existing dynamic components
        if (binding.llBirdList.childCount != 0) {

            //clear the dynamic components
            binding.llBirdList.removeAllViews()
        }



        /*

        GlobalScope.launch (Dispatchers.IO) {




            withContext (Dispatchers.Main) {
                //Toast.makeText(requireContext(), (binding.llBirdList.getChildAt(1) as Card_SpeciesSelector).binding.tvSpecies.text, Toast.LENGTH_SHORT).show()
                //(binding.llBirdList.getChildAt(0) as Card_SpeciesSelector).callOnClick()
            }
        }

         */


        //------------------------------------------------------------



        binding.etSearch.addTextChangedListener { charSequence ->

            //check if there are existing dynamic components
            if (binding.llBirdList.childCount != 0) {

                //clear the dynamic components
                binding.llBirdList.removeAllViews()
            }

            if (charSequence.isNullOrEmpty()) {
                populateBirdOptions(uniqueBirdList)
            } else {
                var searchBirdList = ArrayList<String>()

                for (birds in uniqueBirdList) {
                    if (charSequence?.let { birds.contains(it) } == true) {
                        searchBirdList.add(birds)
                    }
                }

                populateBirdOptions(searchBirdList)
            }
        }



        /*
        for (i in 1..20) {

            val activityLayout = binding.llBirdList;

            var birdOption = Card_SpeciesSelector(activity)


            birdOption.binding.rlSelector.visibility = View.INVISIBLE
           // birdOption.binding.tvContactName.text = "Jake Young"
            //birdOption.binding.tvContactRole.text = "Senior Member"

            //add the new view
            activityLayout.addView(birdOption)

            birdOption.setOnClickListener()
            {

                activityLayout.forEach{ childView ->
                    // do something with this childView

                    if (childView is Card_SpeciesSelector)
                    {
                        childView.binding.rlSelector.visibility = View.INVISIBLE
                    }

                }

                birdOption.binding.rlSelector.visibility = View.VISIBLE
            }


            val scale = requireActivity().resources.displayMetrics.density
            val pixels = (14 * scale + 0.5f)

            val spacer = Space(activity)
            spacer.minimumHeight = pixels.toInt()
            activityLayout.addView(spacer)

        }

         */

        //---------------------------------------------------------------------------------------------

        var pastDistance = 0.0
        var pastHotspot = "Unknown"


        //if distance to closest hotspot is more than the max distance settable (60 km) then set the location as unknown

        var userLocation: Location? = null
        var mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        mFusedLocationClient.lastLocation.addOnCompleteListener(requireActivity()) { task ->
            userLocation = task.result
            if (userLocation != null) {

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
                                pastHotspot = hotspot.locName.toString()
                            }
                        }
                    }

                    withContext (Dispatchers.Main) {
                        populateBirdOptions(uniqueBirdList)
                        binding.tvCurrentLocation.text = pastHotspot
                    }
                }

            }
        }

        // Inflate the layout for this fragment
        return view
    }



}