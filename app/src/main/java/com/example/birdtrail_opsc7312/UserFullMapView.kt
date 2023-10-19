package com.example.birdtrail_opsc7312


import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.birdtrail_opsc7312.databinding.FragmentUserFullMapViewBinding
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.maps.plugin.gestures.OnMoveListener



class UserFullMapView : Fragment() {

    //set view binding
    private var _binding: FragmentUserFullMapViewBinding? = null
    private val binding get() = _binding!!

    //set the current map filter distance
    private var currentDistance = 50

    //set the current map filter time frame
    private lateinit var currentTimeFrame: String

    //set the current map measurement symbol
    private var measurementSymbol = "KM"

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //view binding
        _binding = FragmentUserFullMapViewBinding.inflate(inflater, container, false)
        val view = binding.root


        //check the current users measurement settings
        if (GlobalClass.currentUser.isMetric == false)
        {
            //set the current map measurement symbol to miles
            measurementSymbol = "mi"

            //set the maximum value for miles distance setting
            binding.slDistance.valueTo = 40f
        }
        else
        {
            //set the maximum value for kilometers distance setting
            binding.slDistance.valueTo = 60f
        }


        //set the current map filter distance to the users default distance setting
        currentDistance = GlobalClass.currentUser.defaultdistance
        binding.slDistance.value = currentDistance.toFloat()


        //set the current map filter timeframe
        currentTimeFrame = binding.spnTimeFrame.selectedItem.toString()


        //create local fragment controller
        val fragmentControl = FragmentHandler()

        //new variable from the full map fragment (contains the map view)
        var fullMapView = FullMapFragment()

        //call method to modify the movement listener of the map view
        overrideMapMovementListener(fullMapView)

        //call method to update the users map
        modifyMap(true)


        //on the top bar back button click
        binding.tvBack.setOnClickListener()
        {
            //send the user back to the home fragment
            fragmentControl.replaceFragment(HomeFragment(), R.id.flContent, parentFragmentManager)
        }

        //variable to hold the expanding menus initial height
        var initialHeight = 0


        //on the top bar filter button click
        binding.tvFilter.setOnClickListener()
        {

            //new animation handler object
            val animationManager = AnimationHandler()

            //check if the initial height has not been set
            if (initialHeight == 0)
            {
                //set the initial height to the top bars height
                initialHeight = binding.rlTopBar.height
            }


            //check if the menu is expanded or not
            if (binding.rlTopBar.height == initialHeight)
            {
               //if the menu is not expanded

                //call method to animate the expansion of the menu
                animationManager.animateMenu(binding.rlTopBar, 100, 556)

                //show the map filter options
                binding.llFilterOptions.visibility = View.VISIBLE

                //show the darkened overlay for the map
                binding.imgDarkenOverlay.visibility = View.VISIBLE

                //set the current map filter time frame to the ui set value
                currentTimeFrame = binding.spnTimeFrame.selectedItem.toString()

                //set the current map filter distance to the ui set value
                currentDistance = binding.slDistance.value.toInt()


            }
            else
            {
                //if the menu is expanded

                //call method to animate the shrinking of the menu
                animationManager.animateMenu(binding.rlTopBar, 556, initialHeight)

                //hide the map filter options
                binding.llFilterOptions.visibility = View.GONE

                //hide the darkened overlay for the map
                binding.imgDarkenOverlay.visibility = View.INVISIBLE

                //call method to update the map
                modifyMap(false)
            }
        }


        //on distance filter slider drag
        binding.slDistance.addOnChangeListener { rangeSlider, value, fromUser ->

            //set the distance label the the value of the slider
            binding.tvDistanceValue.text = "${value.toInt()}$measurementSymbol"
        }



        //fix for spinner not showing text in correct color
        binding.spnTimeFrame.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                (view as TextView).setTextColor(Color.BLACK) //Change selected text color
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })


        //on map center button click
        binding.imgCenterMap.setOnClickListener()
        {
            //call method to force update the map
            modifyMap(true)
        }


        //set default distance filter text display
        binding.tvDistanceValue.text = "${currentDistance}$measurementSymbol"

        // Inflate the layout for this fragment
        return view
    }


    //---------------------------------------------------------------------------------------------
    //method to modify the maps movement listener
    //---------------------------------------------------------------------------------------------
    private fun overrideMapMovementListener(fullMapView: FullMapFragment)
    {
        fullMapView.onMoveListener = object : OnMoveListener {
            override fun onMoveBegin(detector: MoveGestureDetector) {
                fullMapView.onCameraTrackingDismissed()

                //animate the map center button fade in
                ObjectAnimator.ofFloat( binding.imgCenterMap, View.ALPHA, 0.0f, 1.0f).setDuration(600).start();
            }

            override fun onMove(detector: MoveGestureDetector): Boolean {
                return false
            }

            override fun onMoveEnd(detector: MoveGestureDetector) {}

        }
    }
    //---------------------------------------------------------------------------------------------


    //---------------------------------------------------------------------------------------------
    //method to modify the map view and the information it displays
    //---------------------------------------------------------------------------------------------
    private fun modifyMap(forceUpdate : Boolean)
    {

        //if the filter data has been modified or an update is being forced
        if (((currentDistance != binding.slDistance.value.toInt()) || (currentTimeFrame != binding.spnTimeFrame.selectedItem.toString())) || forceUpdate == true)
            {

                //create local fragment controller
                val fragmentControl = FragmentHandler()

                //new variable from the full map fragment (contains the map view)
                var fullMapView = FullMapFragment()


                //set the time frame filter value
                fullMapView.filterTimeFrame = (binding.spnTimeFrame.selectedItemPosition) + 1

                //set the distance based filter value
                fullMapView.filterDistance = binding.slDistance.value.toInt().toDouble()


                //show the updated map view
                fragmentControl.replaceFragment(
                    fullMapView,
                    R.id.cvFullMapFragmentContainer,
                    requireActivity().supportFragmentManager
                )

                //set the center map button to transparent
                binding.imgCenterMap.alpha = 0f


                //call method to modify the movement listener of the map view
                overrideMapMovementListener(fullMapView)

            }

    }
    //---------------------------------------------------------------------------------------------

}