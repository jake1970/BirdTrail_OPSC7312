package com.example.birdtrail_opsc7312

import android.location.Location
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.example.birdtrail_opsc7312.databinding.FragmentUserObservationsBinding
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class UserObservations : Fragment() {

    //set view binding
    private var _binding: FragmentUserObservationsBinding? = null
    private val binding get() = _binding!!

    //set spacer size, size between custom cards
    private val spacerSize = 14

    //boolean to control the list of cards being shown to the user
    private var onAllSightings = true

    private lateinit var loadingProgressBar : ViewGroup

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //view binding
        _binding = FragmentUserObservationsBinding.inflate(inflater, container, false)
        val view = binding.root


        MainScope().launch {

            loadingProgressBar = layoutInflater.inflate(R.layout.loading_cover, null) as ViewGroup
            view.addView(loadingProgressBar)

            if (GlobalClass.UpdateDataBase == true) {



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




    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateUI()
    {

        //New Animation Handler Object
        val animationManager = AnimationHandler()

        //on clicking the filter button next to the search bar
        binding.imgFilter.setOnClickListener()
        {

            //changes the order of the cards displayed


            //if the image is right side up
            if (binding.imgFilter.rotation.toDouble() == 0.0)
            {
                //rotate the image upside down
                binding.imgFilter.rotation = (180).toFloat()

                //reverse the order of the user observations list
                GlobalClass.userObservations.reverse()

                //check which custom card view to generate/load based on the filter image direction
                if (onAllSightings == true)
                {
                    //latest sighting first
                    populateViewContent(true, GlobalClass.userObservations)
                }
                else
                {
                    //reverse alphabetical order (Z-A)
                    populateViewContent(false, GlobalClass.userObservations)
                }

            }
            else
            {
                //if the image is upside down

                //rotate the image right side up
                binding.imgFilter.rotation = (0.0).toFloat()

                //reverse the order of the user observations list
                GlobalClass.userObservations.reverse()

                //check which custom card view to generate/load based on the filter image direction
                if (onAllSightings == true)
                {
                    //oldest sighting first
                    populateViewContent(true, GlobalClass.userObservations)
                }
                else
                {
                    //alphabetical order (A-Z)
                    populateViewContent(false, GlobalClass.userObservations)
                }


            }
        }

        //---------------------------------------------------------------------------------------------
        //Swap User Observation Views
        //---------------------------------------------------------------------------------------------

        //Load initial view of all sightings
        populateViewContent(true, GlobalClass.userObservations)


        //on all sightings button click
        binding.tvAllSightings.setOnClickListener()
        {

            //Animate the bar to the left side of the screen
            animationManager.animateX(binding.vwSelectedView, 0f)

            //Load view of all sightings
            populateViewContent(true, GlobalClass.userObservations)
        }


        //on species button click
        binding.tvSpeciesSightings.setOnClickListener()
        {

            //Generate the position of the tab indicator bar at the end (right) of the screen
            val moveToPosition = animationManager.getPositionInRelationToScreen(binding.vwSelectedView)

            //Animate the bar to the right side of the screen
            animationManager.animateX(binding.vwSelectedView, -moveToPosition.x.toFloat())

            //Load grouped view of species sightings
            populateViewContent(false, GlobalClass.userObservations)

        }

        //---------------------------------------------------------------------------------------------

        loadingProgressBar.visibility = View.GONE

    }



    //---------------------------------------------------------------------------------------------
    //Method to load the custom cards and their content based on the users selection
    //---------------------------------------------------------------------------------------------
    @RequiresApi(Build.VERSION_CODES.O)
    private fun populateViewContent(allSightings: Boolean, searchList: ArrayList<UserObservationDataClass>) {

        try {

            //new scroll view handler object
            val scrollViewTools = ScrollViewHandler()

            //set the location for the dynamic components to be created in
            val activityLayout = binding.llBirdList;

            //check if there are existing dynamic components
            if (activityLayout.childCount != 0) {

                //clear the dynamic components
                activityLayout.removeAllViews()
            }


            //if all sightings must be loaded
            if (allSightings) {
                //loop through the sightings
                for (sighting in searchList) {

                    if (sighting.userID == GlobalClass.currentUser.userID) {
                        //new dynamic component
                        var birdOption = Card_Observations_All(activity)


                        birdOption.binding.tvSpecies.text = sighting.birdName
                        birdOption.binding.tvDate.text = sighting.date.toString()
                        birdOption.binding.tvSighted.text =
                            getString(R.string.sightingsAmountText) + " " + sighting.count.toString()

                        //add the dynamic component to the container view
                        activityLayout.addView(birdOption)

                        //call method to generate a space under the dynamic component
                        scrollViewTools.generateSpacer(
                            activityLayout,
                            requireActivity(),
                            spacerSize
                        )


                        var userLocation: Location? = null
                        var mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
                        mFusedLocationClient.lastLocation.addOnCompleteListener(requireActivity()) { task ->
                            userLocation = task.result
                            if (userLocation != null) {

                                birdOption.setOnClickListener(){

                                    val index = GlobalClass.userObservations.indexOfFirst { it.observationID == sighting.observationID }

                                    val mapHotspotView = MapHotspot()
                                    val args = Bundle()

                                    val distanceInKm = calculateDistance(
                                        userLocation!!.latitude,
                                        userLocation!!.longitude,
                                        sighting.lat,
                                        sighting.long
                                    )
                                    args.putInt("observationIndex", index)
                                    args.putDouble("distance", distanceInKm)
                                    args.putBoolean("isHotspot", false)

                                    mapHotspotView.arguments = args


                                    val transaction = parentFragmentManager.beginTransaction()
                                    transaction.replace(R.id.flContent, mapHotspotView)
                                    transaction.addToBackStack(null)
                                    transaction.commit()

                                }
                            }
                        }
                    }
                }

                onAllSightings = true
            } else {

                var speciesTotalList = arrayListOf<BirdSpeciesSightingDataClass>()
                //if species summary must be loaded

                //loop through the species
                for (sighting in searchList) {

                    if (sighting.userID == GlobalClass.currentUser.userID) {

                        var matchFound = false
                        for (species in speciesTotalList) {

                            if (sighting.birdName == species.birdSpeciesName) {
                                species.birdSpeciesTotalCount =
                                    species.birdSpeciesTotalCount + sighting.count
                                matchFound = true
                            }
                        }

                        if (matchFound == false) {
                            var newSpeciesEntry = BirdSpeciesSightingDataClass()
                            newSpeciesEntry.birdSpeciesName = sighting.birdName
                            newSpeciesEntry.birdSpeciesTotalCount = sighting.count
                            speciesTotalList.add(newSpeciesEntry)
                        }
                    }
                }



                for (species in speciesTotalList) {

                    //new dynamic component
                    var birdOption = Card_Observations_Species(activity)

                    birdOption.binding.tvSpecies.text = species.birdSpeciesName
                    birdOption.binding.tvSighted.text =
                        getString(R.string.totalAmountText) + " " + species.birdSpeciesTotalCount.toString()

                    //add the dynamic component to the container view
                    activityLayout.addView(birdOption)

                    //call method to generate a space under the dynamic component
                    scrollViewTools.generateSpacer(activityLayout, requireActivity(), spacerSize)

                }

                onAllSightings = false
            }


            binding.etSearch.addTextChangedListener { charSequence ->

                //check if there are existing dynamic components
                if (binding.llBirdList.childCount != 0) {

                    //clear the dynamic components
                    binding.llBirdList.removeAllViews()
                }


                if (charSequence.isNullOrEmpty()) {

                    if (onAllSightings == true) {
                        populateViewContent(true, GlobalClass.userObservations)
                    } else {
                        populateViewContent(false, GlobalClass.userObservations)
                    }

                    binding.imgFilter.isEnabled = true
                    binding.imgFilter.setColorFilter(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.white
                        ), android.graphics.PorterDuff.Mode.SRC_IN
                    )


                } else {

                    binding.imgFilter.isEnabled = false
                    binding.imgFilter.setColorFilter(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.sub_grey
                        ), android.graphics.PorterDuff.Mode.SRC_IN
                    )

                    var searchBirdList = arrayListOf<UserObservationDataClass>()

                    for (birds in GlobalClass.userObservations) {
                        if (birds.birdName.lowercase()
                                .contains(charSequence.toString().lowercase())
                        ) {
                            searchBirdList.add(birds)
                        }
                    }

                    if (onAllSightings == true) {
                        populateViewContent(true, searchBirdList)
                    } else {
                        populateViewContent(false, searchBirdList)
                    }
                }
            }

            if (activityLayout.childCount == 0) {
                GlobalClass.generateObservationPrompt(
                    activityLayout,
                    requireContext(),
                    parentFragmentManager
                )

            }

        }
        catch (e :Exception)
        {
            GlobalClass.InformUser(getString(R.string.errorText),"$e", requireContext())
        }
    }

    //---------------------------------------------------------------------------------------------




    @RequiresApi(Build.VERSION_CODES.O)
    fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val earthRadiusKm = 6371.0

        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))

        var measurement = earthRadiusKm * c
        return measurement
    }

}
