package com.example.birdtrail_opsc7312


import android.R.attr.button
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.animation.doOnEnd
import androidx.fragment.app.Fragment
import com.example.birdtrail_opsc7312.databinding.FragmentUserFullMapViewBinding
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.maps.extension.style.expressions.dsl.generated.switchCase
import com.mapbox.maps.plugin.gestures.OnMoveListener
import com.mapbox.maps.plugin.gestures.gestures
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt


/**
 * A simple [Fragment] subclass.
 * Use the [UserFullMapView.newInstance] factory method to
 * create an instance of this fragment.
 */
class UserFullMapView : Fragment() {

    private var _binding: FragmentUserFullMapViewBinding? = null
    private val binding get() = _binding!!


    private var currentDistance = 50
    private lateinit var currentTimeFrame: String
    private var measurementSymbol = "KM"

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentUserFullMapViewBinding.inflate(inflater, container, false)
        val view = binding.root

        //------------------------------------------------------------------------------------------
        //handle measurements
        //------------------------------------------------------------------------------------------

        if (GlobalClass.currentUser.isMetric == false)
        {
            measurementSymbol = "mi"
            binding.slDistance.value = 30f
            currentDistance = 30
        }



        if (GlobalClass.currentUser.defaultdistance > binding.slDistance.value)
        {
            binding.slDistance.value = binding.slDistance.valueTo
        }

        binding.slDistance.valueTo = GlobalClass.currentUser.defaultdistance.toFloat()



        //------------------------------------------------------------------------------------------


        currentTimeFrame = binding.spnTimeFrame.selectedItem.toString()

        //create local fragment controller
        val fragmentControl = FragmentHandler()

        var fullMapView = FullMapFragment()
        fragmentControl.replaceFragment(fullMapView, R.id.cvFullMapFragmentContainer, requireActivity().supportFragmentManager)



        binding.tvBack.setOnClickListener()
        {
            fragmentControl.replaceFragment(HomeFragment(), R.id.flContent, parentFragmentManager)
        }

        var initialHeight = 0

        binding.tvFilter.setOnClickListener()
        {
            if (initialHeight == 0)
            {
                initialHeight = binding.rlTopBar.height
            }

            if (binding.rlTopBar.height == initialHeight)
            {
                val va = ValueAnimator.ofInt(100, 556)
                va.duration = 300
                va.addUpdateListener { animation ->
                    val value = animation.animatedValue as Int
                    binding.rlTopBar.getLayoutParams().height = value
                    binding.rlTopBar.requestLayout()
                }
                va.start()

                binding.llFilterOptions.visibility = View.VISIBLE//}

                binding.imgDarkenOverlay.visibility = View.VISIBLE

                currentTimeFrame = binding.spnTimeFrame.selectedItem.toString()
                currentDistance = binding.slDistance.value.toInt()
               // currentSearchTerm = binding.etSearch.text.toString()
            }
            else
            {
                val va = ValueAnimator.ofInt(binding.rlTopBar.height, initialHeight)
                va.duration = 300
                va.addUpdateListener { animation ->
                    val value = animation.animatedValue as Int
                    binding.rlTopBar.getLayoutParams().height = value
                    binding.rlTopBar.requestLayout()
                }
                va.start()

               // va.doOnEnd {
                binding.llFilterOptions.visibility = View.GONE//}
                binding.imgDarkenOverlay.visibility = View.INVISIBLE

                modifyMap()
            }
        }

            //distance
            //binding.tvDistanceValue.text = "${distance}KM"
            binding.slDistance.addOnChangeListener { rangeSlider, value, fromUser ->

               binding.tvDistanceValue.text = "${value.toInt()}$measurementSymbol"
//
            }

            binding.tvDistanceValue.text = binding.slDistance.value.toInt().toString()

            //time frame
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



        fullMapView.onMoveListener = object : OnMoveListener {
            override fun onMoveBegin(detector: MoveGestureDetector) {
                fullMapView.onCameraTrackingDismissed()
                //binding.imgCenterMap.visibility = View.VISIBLE
                ObjectAnimator.ofFloat( binding.imgCenterMap, View.ALPHA, 0.0f, 1.0f).setDuration(600).start();
            }

            override fun onMove(detector: MoveGestureDetector): Boolean {
                return false
            }

            override fun onMoveEnd(detector: MoveGestureDetector) {}

        }

        binding.imgCenterMap.setOnClickListener()
        {

                    fullMapView.onMapReady()
                    ObjectAnimator.ofFloat(binding.imgCenterMap, View.ALPHA, 1.0f, 0.0f).setDuration(600).start();

        }

        binding.tvDistanceValue.text = "${currentDistance}$measurementSymbol"

        // Inflate the layout for this fragment
        return view
    }





    private fun modifyMap()
    {
        //currentSearchTerm = binding.etSearch.text.toString()
        if ((currentDistance != binding.slDistance.value.toInt()) || (currentTimeFrame != binding.spnTimeFrame.selectedItem.toString()))
            {

                //create local fragment controller
                val fragmentControl = FragmentHandler()

                var fullMapView = FullMapFragment()

                var filterWeeks = 1
                when(binding.spnTimeFrame.selectedItemPosition) {
                    0 -> filterWeeks = 1
                    1 -> filterWeeks = 2
                    2 -> filterWeeks = 3
                }
                fullMapView.filterTimeFrame = filterWeeks

                fullMapView.filterDistance = binding.slDistance.value.toInt().toDouble()

                //ullMapView.filterSearchBirdName = binding.etSearch.text.toString()

                fragmentControl.replaceFragment(
                    fullMapView,
                    R.id.cvFullMapFragmentContainer,
                    requireActivity().supportFragmentManager
                )


            }

        //Toast.makeText(requireContext(), distance.toString(), Toast.LENGTH_SHORT).show()
        //fullMapView.loadMap(distance)
    }

}