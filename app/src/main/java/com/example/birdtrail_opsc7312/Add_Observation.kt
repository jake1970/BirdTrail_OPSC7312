package com.example.birdtrail_opsc7312

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Space
import com.example.birdtrail_opsc7312.databinding.FragmentAddObservationBinding
import com.example.birdtrail_opsc7312.databinding.FragmentHomeBinding

/**
 * A simple [Fragment] subclass.
 * Use the [Add_Observation.newInstance] factory method to
 * create an instance of this fragment.
 */
class Add_Observation : Fragment() {

    private var _binding: FragmentAddObservationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentAddObservationBinding.inflate(inflater, container, false)
        val view = binding.root


        binding.btnBack.setOnClickListener()
        {
            val fragmentControl = FragmentHandler()
            //just for now, will return back to whichever screen you were previously on
            fragmentControl.replaceFragment(HomeFragment(), R.id.flContent, parentFragmentManager)
        }


        //---------------------------------------------------------------------------------------------
        //test code for custom components
        //---------------------------------------------------------------------------------------------
        for (i in 1..2) {

            val activityLayout = binding.llBirdList;
            var birdOption = Card_SpeciesSelector(activity)

           // birdOption.binding.tvContactName.text = "Jake Young"
            //birdOption.binding.tvContactRole.text = "Senior Member"



            //add the new view
            activityLayout.addView(birdOption)


            val scale = requireActivity().resources.displayMetrics.density
            val pixels = (14 * scale + 0.5f)

            val spacer = Space(activity)
            spacer.minimumHeight = pixels.toInt()
            activityLayout.addView(spacer)

        }
        //---------------------------------------------------------------------------------------------



        // Inflate the layout for this fragment
        return view
    }



}