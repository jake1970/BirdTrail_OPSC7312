package com.example.birdtrail_opsc7312

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.example.birdtrail_opsc7312.databinding.FragmentAddObservationBinding
import com.example.birdtrail_opsc7312.databinding.FragmentMapHotspotBinding
import kotlinx.coroutines.launch

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

        var hotspot = GlobalClass.hotspots[hotspotIndex!!]

        //create local fragment controller
        val fragmentControl = FragmentHandler()

        var fullMapView = FullMapFragment()
        fullMapView.openInFullView = false
        fullMapView.centerOnHotspot = true

        val args = Bundle()

        hotspot.lng?.let { args.putDouble("hotspotLong", it) }
        hotspot.lat?.let { args.putDouble("hotspotLat", it) }
        args.putBoolean("hotspotCamera", true)



        fullMapView.arguments = args


        fragmentControl.replaceFragment(fullMapView, R.id.cvHotspotMapFragmentContainer, requireActivity().supportFragmentManager)


        binding.tvHotspotDate.text = hotspot.obsDt.toString()
        binding.tvBirdName.text = hotspot.comName
        binding.tvBirdCount.text = hotspot.howMany.toString()
        binding.tvBirdLocation.text = hotspot.locName

        lifecycleScope.launch {
            var imageHandler = ImageHandler()
            var image = imageHandler.GetImage(hotspot.comName.toString())
            binding.imgBird.setImageBitmap(image)
        }

        binding.btnDirections.setOnClickListener(){

            val intent = Intent(requireContext(), MapDirectionsActivity::class.java).apply {
                putExtra("long", hotspot.lng)
                putExtra("lat", hotspot.lat)
            }
            startActivity(intent)
        }

        // Inflate the layout for this fragment
        return view
    }
}