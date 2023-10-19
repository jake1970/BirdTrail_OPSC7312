package com.example.birdtrail_opsc7312

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.birdtrail_opsc7312.databinding.FragmentMapHotspotBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DecimalFormat
import java.text.SimpleDateFormat

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


        try
        {
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
                binding.tvDistance.text = "${getString(R.string.distanceText)}: ${formattedDistance}KM"
            }
            else
            {
                if (distance != null) {
                    distance *= 0.62137119
                }
                val decimalFormat = DecimalFormat("#.##")
                val formattedDistance = decimalFormat.format(distance)
                binding.tvDistance.text = "${getString(R.string.distanceText)}: ${formattedDistance}mi"
            }

            //get bird observations at hotspot
            lifecycleScope.launch {
                try
                {
                    var ebirdHandler = eBirdAPIHandler()
                    hotspot.locId?.let { ebirdHandler.getHotspotBirds("ZA", it) }

                    val scrollViewTools = ScrollViewHandler()
                    withContext(Dispatchers.Main) {
                        val activityLayout = binding.llBirdList;
                        for (bird in GlobalClass.currentHotspotBirds)
                        {
                            var birdDisplay = Card_Observations_All(requireContext())
                            birdDisplay.binding.tvSpecies.text = bird.comName

                            // Define the date format pattern for your input string
                            val inputPattern = "yyyy-MM-dd HH:mm"

                            // Define the desired date format pattern for the output
                            val outputPattern = "yyyy-MM-dd"

                            val inputFormat = SimpleDateFormat(inputPattern)
                            val outputFormat = SimpleDateFormat(outputPattern)

                            var date = inputFormat.parse(bird.obsDt)
                            var localDate = outputFormat.format(date)

                            birdDisplay.binding.tvDate.text = localDate

                            birdDisplay.binding.tvSighted.text = "${getString(R.string.foundText)}: ${bird.howMany}"
                            activityLayout.addView(birdDisplay)
                            scrollViewTools.generateSpacer(activityLayout, requireActivity(), 14)
                        }
                    }
                }
                catch (e : Exception)
                {
                    GlobalClass.InformUser(getString(R.string.errorText),"${e.toString()}", requireContext())
                }
            }

            binding.btnBack.setOnClickListener(){
                val transaction = parentFragmentManager.beginTransaction()
                transaction.replace(R.id.flContent, UserFullMapView())
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


        }catch (e: Exception)
        {
            GlobalClass.InformUser(getString(R.string.errorText),"${e.toString()}", requireContext())
        }

        // Inflate the layout for this fragment
        return view
    }
}