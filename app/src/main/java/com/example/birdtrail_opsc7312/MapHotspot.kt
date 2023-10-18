package com.example.birdtrail_opsc7312

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import com.example.birdtrail_opsc7312.databinding.FragmentAddObservationBinding
import com.example.birdtrail_opsc7312.databinding.FragmentMapHotspotBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DecimalFormat

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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _binding = FragmentMapHotspotBinding.inflate(inflater, container, false)
        val view = binding.root

        val hotspotIndex = arguments?.getInt("hotspotIndex")
        var distance = arguments?.getDouble("distance")


        var hotspot = GlobalClass.nearbyHotspots[hotspotIndex!!]

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


        binding.tvHotspotDate.text = hotspot.latestObsDt.toString()
        binding.tvHotspotLocation.text = hotspot.locName
        if (GlobalClass.currentUser.isMetric)
        {
            val decimalFormat = DecimalFormat("#.##")
            val formattedDistance = decimalFormat.format(distance)
            binding.tvDistance.text = "Distance: ${formattedDistance}km"
        }
        else
        {
            if (distance != null) {
                distance *= 0.62137119
            }
            val decimalFormat = DecimalFormat("#.##")
            val formattedDistance = decimalFormat.format(distance)
            binding.tvDistance.text = "Distance: ${formattedDistance}mi"
        }





        lifecycleScope.launch {
            try
            {
                var ebirdHandler = eBirdAPIHandler()
                hotspot.locId?.let { ebirdHandler.getHotspotBirds("ZA", it) }

            //var imageHandler = ImageHandler()
            //var image = imageHandler.GetImage(hotspot.comName.toString())
            //binding.imgBird.setImageBitmap(image)
                val scrollViewTools = ScrollViewHandler()
                withContext(Dispatchers.Main) {
                    val activityLayout = binding.llBirdList;
                    for (bird in GlobalClass.currentHotspotBirds)
                    {
                        var birdDisplay = Card_Observations_All(requireContext())
                        birdDisplay.binding.tvSpecies.text = bird.comName
                        birdDisplay.binding.tvDate.text = bird.obsDt
                        birdDisplay.binding.tvSighted.text = bird.howMany.toString()
                        activityLayout.addView(birdDisplay)
                        scrollViewTools.generateSpacer(activityLayout, requireActivity(), 20)

                    }
                }
            }
            catch (e : Exception)
            {
                Toast.makeText(requireContext(), getString(R.string.failedToLoadImage), Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnBack.setOnClickListener(){
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.flContent, FullMapFragment())
            transaction.addToBackStack(null)
            transaction.commit()
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