package com.example.birdtrail_opsc7312

import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Space
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.example.birdtrail_opsc7312.databinding.FragmentUserObservationsBinding


/**
 * A simple [Fragment] subclass.
 * Use the [UserObservations.newInstance] factory method to
 * create an instance of this fragment.
 */
class UserObservations : Fragment() {

    private var _binding: FragmentUserObservationsBinding? = null
    private val binding get() = _binding!!
    private val spacerSize = 14

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



            }
            else
            {
                binding.imgFilter.rotation = (0.0).toFloat()


            }
        }

        //---------------------------------------------------------------------------------------------
        //Swap User Observation Views
        //---------------------------------------------------------------------------------------------

        //Load initial view of all sightings
        populateViewContent(true)


        binding.tvAllSightings.setOnClickListener()
        {

            //Animate the bar to the left side of the screen
            animationManager.animateX(binding.vwSelectedView, 0f)

            //Load view of all sightings
            populateViewContent(true)
        }

        binding.tvSpeciesSightings.setOnClickListener()
        {

            //Generate the position of the tab indicator bar at the end (right) of the screen
            val moveToPosition = animationManager.getPositionInRelationToScreen(binding.vwSelectedView)

            //Animate the bar to the right side of the screen
            animationManager.animateX(binding.vwSelectedView, -moveToPosition.x.toFloat())

            //Load grouped view of species sightings
            populateViewContent(false)

        }

        //---------------------------------------------------------------------------------------------



        // Inflate the layout for this fragment
        return view
    }


    //---------------------------------------------------------------------------------------------
    //Swap User Observation Views
    //---------------------------------------------------------------------------------------------
    private fun populateViewContent(allSightings: Boolean){

        //loop iterations
        val loopCount = 20

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
            for (i in 1..loopCount) {

                //new dynamic component
                var birdOption = Card_Observations_All(activity)

                //add the dynamic component to the container view
                activityLayout.addView(birdOption)

                //call method to generate a space under the dynamic component
                scrollViewTools.generateSpacer(activityLayout, requireActivity(), spacerSize)


            }
        }
        else
        {
            //if species summary must be loaded

            //loop through the species
            for (i in 1..loopCount) {


                //new dynamic component
                var birdOption = Card_Observations_Species(activity)

                //add the dynamic component to the container view
                activityLayout.addView(birdOption)

                //call method to generate a space under the dynamic component
                scrollViewTools.generateSpacer(activityLayout, requireActivity(), spacerSize)


            }
        }

    }
    //---------------------------------------------------------------------------------------------



}
