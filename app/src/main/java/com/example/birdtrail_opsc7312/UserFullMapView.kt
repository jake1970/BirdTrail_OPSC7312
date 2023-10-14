package com.example.birdtrail_opsc7312


import android.R.attr.button
import android.animation.ValueAnimator
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.animation.doOnEnd
import androidx.fragment.app.Fragment
import com.example.birdtrail_opsc7312.databinding.FragmentUserFullMapViewBinding
import kotlin.math.roundToInt


/**
 * A simple [Fragment] subclass.
 * Use the [UserFullMapView.newInstance] factory method to
 * create an instance of this fragment.
 */
class UserFullMapView : Fragment() {

    private var _binding: FragmentUserFullMapViewBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentUserFullMapViewBinding.inflate(inflater, container, false)
        val view = binding.root


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
            /*
            binding.rlTopBar.animate()
                .scaleY(-10f)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .setDuration(1000)

             */

            /*
            ObjectAnimator.ofFloat(binding.rlTopBar, "translationY", 200f).apply {
                duration = 500
                start()
            }

             */

           /*
            val params = binding.rlTopBar.getLayoutParams()
            params.height = 1000
            binding.rlTopBar.setLayoutParams(params)
             */

            if (initialHeight == 0)
            {
                initialHeight = binding.rlTopBar.height
            }

            if (binding.rlTopBar.height == initialHeight)
            {
                val va = ValueAnimator.ofInt(100, 740)
                va.duration = 300
                va.addUpdateListener { animation ->
                    val value = animation.animatedValue as Int
                    binding.rlTopBar.getLayoutParams().height = value
                    binding.rlTopBar.requestLayout()
                }
                va.start()


               // va.doOnEnd {
                    //binding.llFilterOptions.isBaselineAligned

                    /*
                    val params = binding.llFilterOptions.layoutParams as RelativeLayout.LayoutParams
                    params.addRule(RelativeLayout.ALIGN_PARENT_TOP)
                    binding.llFilterOptions.layoutParams = params //causes layout update

                     */
                    binding.llFilterOptions.visibility = View.VISIBLE//}


                binding.imgDarkenOverlay.visibility = View.VISIBLE



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



            }

            binding.tvDistanceValue.text = "50KM"
            binding.slDistance.addOnChangeListener { rangeSlider, value, fromUser ->
                // Responds to when slider's value is changed

                binding.tvDistanceValue.text = value.roundToInt().toString() + "KM"
            }



            /*
            binding.rlTopBar.setPivotY(0f);
            val scaleY = ObjectAnimator.ofFloat(binding.rlTopBar, "scaleY", 10f)
            scaleY.interpolator = DecelerateInterpolator()
            scaleY.start()

             */

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

        }


        // Inflate the layout for this fragment
        return view
    }


}