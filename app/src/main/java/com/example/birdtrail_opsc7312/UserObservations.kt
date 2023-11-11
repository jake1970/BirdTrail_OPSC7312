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

                try
                {
                    withContext(Dispatchers.Default) {
                        val databaseManager = DatabaseHandler()
                        databaseManager.updateLocalData()
                    }
                }
                catch (e :Exception)
                {
                    GlobalClass.InformUser(getString(R.string.errorText),"$e", requireContext())
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


                        //set dynamic component text data
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


                        //the users current location
                        var userLocation: Location? = null

                        //new location client
                        var mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

                        //get the last known user location
                        mFusedLocationClient.lastLocation.addOnCompleteListener(requireActivity()) { task ->

                            //set the location
                            userLocation = task.result

                            //if the location is actually set
                            if (userLocation != null) {

                                //when the dynamic compoenent is clicked
                                birdOption.setOnClickListener(){

                                    //get the index of the associated observation
                                    val index = GlobalClass.userObservations.indexOfFirst { it.observationID == sighting.observationID }

                                    //new map hotspot fragment instance
                                    val mapHotspotView = MapHotspot()

                                    //new arguments
                                    val args = Bundle()

                                    //calculate the distance to the point
                                    val distanceInKm = calculateDistance(
                                        userLocation!!.latitude,
                                        userLocation!!.longitude,
                                        sighting.lat,
                                        sighting.long
                                    )

                                    //set the fragment arguments
                                    args.putInt("observationIndex", index)
                                    args.putDouble("distance", distanceInKm)
                                    args.putBoolean("isHotspot", false)

                                    //set the arguments to the fragment instance
                                    mapHotspotView.arguments = args


                                    //open the fragment
                                    val transaction = parentFragmentManager.beginTransaction()
                                    transaction.replace(R.id.flContent, mapHotspotView)
                                    transaction.addToBackStack(null)
                                    transaction.commit()

                                }
                            }
                        }
                    }
                }

                //set view tracker
                onAllSightings = true
            } else {

                //list of bird species
                var speciesTotalList = arrayListOf<BirdSpeciesSightingDataClass>()

                //if species summary must be loaded


                for (sighting in searchList) {

                    //if the sighting was made by the currently signed in user
                    if (sighting.userID == GlobalClass.currentUser.userID) {

                        //variable to hold if the species was added already
                        var matchFound = false

                        //loop through the species list
                        for (species in speciesTotalList) {

                            //if the species name matches the sightings name
                            if (sighting.birdName == species.birdSpeciesName) {

                                //increase the bird species total sighting count
                                species.birdSpeciesTotalCount =
                                    species.birdSpeciesTotalCount + sighting.count

                                //update species added variable
                                matchFound = true
                            }
                        }

                        //if the species summary is finished
                        if (matchFound == false) {

                            //add the species data
                            var newSpeciesEntry = BirdSpeciesSightingDataClass()

                            //set the species name
                            newSpeciesEntry.birdSpeciesName = sighting.birdName

                            //set the species total count
                            newSpeciesEntry.birdSpeciesTotalCount = sighting.count

                            //add the species data list
                            speciesTotalList.add(newSpeciesEntry)
                        }
                    }
                }


                //loop through the species
                for (species in speciesTotalList) {

                    //new dynamic component
                    var birdOption = Card_Observations_Species(activity)

                    //set the text data
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


            //when the search bar text changes
            binding.etSearch.addTextChangedListener { charSequence ->

                //check if there are existing dynamic components
                if (binding.llBirdList.childCount != 0) {

                    //clear the dynamic components
                    binding.llBirdList.removeAllViews()
                }


                //if the text is empty
                if (charSequence.isNullOrEmpty()) {

                    //load the full list of user observations
                    populateViewContent(onAllSightings, GlobalClass.userObservations)

                    //enable the direction filter
                    binding.imgFilter.isEnabled = true

                    //tint the direction filter
                    binding.imgFilter.setColorFilter(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.white
                        ), android.graphics.PorterDuff.Mode.SRC_IN
                    )


                } else {

                    //if the search bar is not empty

                    //disable the direction filter
                    binding.imgFilter.isEnabled = false

                    //tint the direction filter
                    binding.imgFilter.setColorFilter(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.sub_grey
                        ), android.graphics.PorterDuff.Mode.SRC_IN
                    )

                    //list of user observations matching the search term
                    var searchBirdList = arrayListOf<UserObservationDataClass>()

                    //loop through the user observations
                    for (birds in GlobalClass.userObservations) {

                        //if the search term is within the bird name
                        if (birds.birdName.lowercase()
                                .contains(charSequence.toString().lowercase())
                        ) {
                            //add the matching user observation to the search list
                            searchBirdList.add(birds)
                        }
                    }

                    //call method to load the user observations list from the list of matching birds according to the search term
                    populateViewContent(onAllSightings, searchBirdList)


                }
            }

            //if there are no observations made by the signed in user
            if (activityLayout.childCount == 0) {

                //call method to generate a custom component "card" to prompt the user to add an observation
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



    //---------------------------------------------------------------------------------------------
    //Method to get the distance between two points
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
    //---------------------------------------------------------------------------------------------

}
