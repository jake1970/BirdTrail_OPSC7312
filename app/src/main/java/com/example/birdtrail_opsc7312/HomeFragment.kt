package com.example.birdtrail_opsc7312

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.example.birdtrail_opsc7312.databinding.ActivityHomepageBinding
import com.example.birdtrail_opsc7312.databinding.FragmentHomeBinding
import kotlinx.coroutines.GlobalScope


class HomeFragment : Fragment(R.layout.fragment_home) {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val spacerSize = 20


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        //create local fragment controller
        val fragmentControl = FragmentHandler()


        //new scroll view handler object
        val scrollViewTools = ScrollViewHandler()



        var fullMapView = FullMapFragment()
        fullMapView.openInFullView = true
        fragmentControl.replaceFragment(fullMapView, R.id.cvMapFragmentContainer, requireActivity().supportFragmentManager)


        val activityLayout = binding.llBirdSummaryInfo

        val latestGeneralSighting = Card_Observations_Species(activity)
        val latestUserSighting = Card_Observations_Species(activity)

        activityLayout.addView(latestGeneralSighting)

        //call method to generate a space under the dynamic component
        scrollViewTools.generateSpacer(activityLayout, requireActivity(), spacerSize)

       activityLayout.addView(latestUserSighting)



        //MostRecentGeneralSighting
        //NearestToUserSighting/Hotspot




        return view
    }

}