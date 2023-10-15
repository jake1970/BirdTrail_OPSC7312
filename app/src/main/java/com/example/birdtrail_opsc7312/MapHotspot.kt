package com.example.birdtrail_opsc7312

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.birdtrail_opsc7312.databinding.FragmentAddObservationBinding
import com.example.birdtrail_opsc7312.databinding.FragmentMapHotspotBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MapHotspot.newInstance] factory method to
 * create an instance of this fragment.
 */
class MapHotspot : Fragment() {

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        arguments?.let {
//        }
//    }

    private var _binding: FragmentMapHotspotBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _binding = FragmentMapHotspotBinding.inflate(inflater, container, false)
        val view = binding.root

        val hotspotIndex = arguments?.getInt("hotspotIndex")

        var hotpost = GlobalClass.hotspots[hotspotIndex!!]

        binding.tvHotspotDate.text = hotpost.obsDt.toString()
        binding.tvBirdName.text = hotpost.comName
        binding.tvBirdCount.text = hotpost.howMany.toString()
        binding.tvBirdLocation.text = hotpost.locName

        binding.btnDirections.setOnClickListener(){

            val mapDirectionsView = MapDirections()
            val args = Bundle()

            hotpost.lat?.let { it1 -> args.putDouble("lat", it1) }
            hotpost.lng?.let { it1 -> args.putDouble("long", it1) }

            mapDirectionsView.arguments = args


            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.flContent, mapDirectionsView)
            transaction.addToBackStack(null)
        }

        // Inflate the layout for this fragment
        return view
    }
}