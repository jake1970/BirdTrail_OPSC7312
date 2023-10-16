package com.example.birdtrail_opsc7312

import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Space
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.example.birdtrail_opsc7312.databinding.FragmentUserObservationsBinding
import java.time.LocalDate


/**
 * A simple [Fragment] subclass.
 * Use the [UserObservations.newInstance] factory method to
 * create an instance of this fragment.
 */
class UserObservations : Fragment() {

    private var _binding: FragmentUserObservationsBinding? = null
    private val binding get() = _binding!!
    private val spacerSize = 14
    private var onAllSightings = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentUserObservationsBinding.inflate(inflater, container, false)
        val view = binding.root

        //New Animation Handler Object
        val animationManager = AnimationHandler()

        binding.imgFilter.setOnClickListener()
        {


            Toast.makeText(requireContext(), binding.imgFilter.rotation.toString(), Toast.LENGTH_SHORT)


            if (binding.imgFilter.rotation.toDouble() == 0.0)
            {
                binding.imgFilter.rotation = (180).toFloat()

                GlobalClass.userObservations.reverse()

                //reverse the order
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
                binding.imgFilter.rotation = (0.0).toFloat()

                GlobalClass.userObservations.reverse()

                //reverse the order
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


        binding.tvAllSightings.setOnClickListener()
        {

            //Animate the bar to the left side of the screen
            animationManager.animateX(binding.vwSelectedView, 0f)

            //Load view of all sightings
            populateViewContent(true, GlobalClass.userObservations)
        }

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



        // Inflate the layout for this fragment
        return view
    }


    //---------------------------------------------------------------------------------------------
    //Swap User Observation Views
    //---------------------------------------------------------------------------------------------
    @RequiresApi(Build.VERSION_CODES.O)
    private fun populateViewContent(allSightings: Boolean, searchList: ArrayList<UserObservationDataClass>){


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
        if (allSightings)
        {
            //loop through the sightings
            for (sighting in searchList) {

                if (sighting.userID == GlobalClass.currentUser.userID)
                {
                    //new dynamic component
                    var birdOption = Card_Observations_All(activity)


                    birdOption.binding.tvSpecies.text = sighting.birdName
                    birdOption.binding.tvDate.text = sighting.date.toString()
                    birdOption.binding.tvSighted.text = getString(R.string.sightingsAmountText) + " " + sighting.count.toString()

                    //add the dynamic component to the container view
                    activityLayout.addView(birdOption)

                    //call method to generate a space under the dynamic component
                    scrollViewTools.generateSpacer(activityLayout, requireActivity(), spacerSize)
                }

            }

            onAllSightings = true
        }
        else {

            var speciesTotalList = arrayListOf<BirdSpeciesSightingDataClass>()
            //if species summary must be loaded

            //loop through the species
            for (sighting in searchList)
            {

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
                birdOption.binding.tvSighted.text = getString(R.string.totalAmountText) + " " + species.birdSpeciesTotalCount.toString()

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

                if (onAllSightings == true)
                {
                    populateViewContent(true, GlobalClass.userObservations)
                }
                else
                {
                    populateViewContent(false, GlobalClass.userObservations)
                }

                binding.imgFilter.isEnabled = true
                binding.imgFilter.setColorFilter(ContextCompat.getColor(requireContext(), R.color.white), android.graphics.PorterDuff.Mode.SRC_IN)


            }
            else
            {

                binding.imgFilter.isEnabled = false
                binding.imgFilter.setColorFilter(ContextCompat.getColor(requireContext(), R.color.sub_grey), android.graphics.PorterDuff.Mode.SRC_IN)

                var searchBirdList = arrayListOf<UserObservationDataClass>()

                for (birds in GlobalClass.userObservations) {
                    if (birds.birdName.lowercase().contains(charSequence.toString().lowercase())) {
                        searchBirdList.add(birds)
                    }
                }

                if (onAllSightings == true)
                {
                    populateViewContent(true, searchBirdList)
                }
                else
                {
                    populateViewContent(false, searchBirdList)
                }
            }
        }



    }
    //---------------------------------------------------------------------------------------------



}
