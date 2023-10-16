package com.example.birdtrail_opsc7312


import android.annotation.SuppressLint
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Space
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.children
import androidx.core.view.forEach
import androidx.core.view.iterator
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
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

    private var selectedOption = ""

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingPermission")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        _binding = FragmentAddObservationBinding.inflate(inflater, container, false)
        val view = binding.root


        val loadingProgressBar = layoutInflater.inflate(R.layout.loading_cover, null) as ViewGroup
        view.addView(loadingProgressBar)

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

            val activityLayout = binding.llBirdList;

            for (birdName in searchList)
            {



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


                    if (selectedOption != birdOption.binding.tvSpecies.text.toString()) {

                        loadingProgressBar.visibility = View.VISIBLE


                        //load the image
                        lifecycleScope.launch {

                            try {
                                var imageHandler = ImageHandler()
                                var image = imageHandler.GetImage(
                                    birdOption.binding.tvSpecies.text.toString()
                                )
                                binding.imgBirdImageExpanded.setImageBitmap(image)
                            }
                            catch (e : Exception)
                            {
                                Toast.makeText(requireContext(), getString(R.string.failedToLoadImage), Toast.LENGTH_SHORT).show()
                            }

                            loadingProgressBar.visibility = View.GONE
                        }
                    }

                    selectedOption = birdOption.binding.tvSpecies.text.toString()

                }


                val scale = requireActivity().resources.displayMetrics.density
                val pixels = (14 * scale + 0.5f)

                val spacer = Space(activity)
                spacer.minimumHeight = pixels.toInt()
                activityLayout.addView(spacer)

            }


            for (option in activityLayout)
            {
                if (option is Card_SpeciesSelector && option.binding.tvSpecies.text == selectedOption)
                {
                    option.callOnClick()
                }
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
//                    if (charSequence?.let { birds.contains(it) } == true) {
                    if (birds.lowercase().contains(charSequence.toString().lowercase())) {
                        searchBirdList.add(birds)
                    }
                }

                populateBirdOptions(searchBirdList)
            }
        }




        //88888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888



        //if distance to closest hotspot is more than the max distance settable (60 km) then set the location as unknown

        var userLocation: Location? = null
        var mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        mFusedLocationClient.lastLocation.addOnCompleteListener(requireActivity()) { task ->
            userLocation = task.result
            if (userLocation != null) {

                populateBirdOptions(uniqueBirdList)

                binding.llBirdList.children.first().callOnClick()

                binding.tvCurrentLocation.text = " Lon: " + userLocation!!.longitude.toString() + " Lat: " + userLocation!!.latitude.toString()

                loadingProgressBar.visibility = View.GONE

            }
        }
        //88888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888

        //---------------------------------------------------------------------------------------------



/*



        var pastDistance = 0.0
        var pastHotspot  = eBirdJson2KtKotlin()


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
                                pastHotspot = hotspot
                            }
                        }
                    }

                    withContext (Dispatchers.Main) {
                        populateBirdOptions(uniqueBirdList)

                        //888888888888888888888888888888888888888888888888888888888888888888888888888888888888888//
                            binding.llBirdList.children.first().callOnClick()
                        //888888888888888888888888888888888888888888888888888888888888888888888888888888888888888//

                        if (pastHotspot.locName.isNullOrEmpty())
                        {
                            pastHotspot.locName = "Unknown"
                        }
                        binding.tvCurrentLocation.text = pastHotspot.locName.toString()


                        loadingProgressBar.visibility = View.GONE


                    }
                }

            }
        }

 */

        binding.btnEnter.setOnClickListener()
        {

            try {
                var newSighting = UserObservationDataClass()

                if (GlobalClass.userObservations.count() > 0) {
                    newSighting.observationID = (GlobalClass.userObservations.last().observationID + 1)
                }


                newSighting.userID = GlobalClass.currentUser.userID

                //88888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888
                newSighting.lat = userLocation!!.latitude
                newSighting.long = userLocation!!.longitude
                //88888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888
                /*
                newSighting.lat = pastHotspot.lat!!
                newSighting.long = pastHotspot.lng!!

                 */
                newSighting.birdName = binding.tvSpeciesName.text.toString()

                //date is set by default
                newSighting.count = binding.tvNumberOfSightings.text.toString().toInt()


                GlobalClass.userObservations.add(newSighting)




                requireActivity().findViewById<View>(R.id.home).callOnClick()

                /*
                //create local fragment controller
                val fragmentControl = FragmentHandler()

                fragmentControl.replaceFragment(HomeFragment(), R.id.flContent, requireActivity().supportFragmentManager)
                 */




            }
            catch (e: Exception)
            {
                GlobalClass.InformUser(getString(R.string.errorText),"${e.toString()}", requireContext())
            }



        }

        // Inflate the layout for this fragment
        return view
    }



}