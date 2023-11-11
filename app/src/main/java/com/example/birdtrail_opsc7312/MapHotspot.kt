package com.example.birdtrail_opsc7312

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.birdtrail_opsc7312.databinding.FragmentMapHotspotBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DecimalFormat
import java.text.SimpleDateFormat

class MapHotspot : Fragment() {

    private var _binding: FragmentMapHotspotBinding? = null
    private val binding get() = _binding!!
    private var isHotspot: Boolean? = true

    private lateinit var loadingProgressBar : ViewGroup

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMapHotspotBinding.inflate(inflater, container, false)
        val view = binding.root

        //get the content type to load to determine screen config
        isHotspot = arguments?.getBoolean("isHotspot")

        MainScope().launch {

            //create and show the loading screen overlay
            loadingProgressBar = layoutInflater.inflate(R.layout.loading_cover, null) as ViewGroup
            view.addView(loadingProgressBar)

            if (GlobalClass.UpdateDataBase == true) {

                try
                {
                    withContext(Dispatchers.Default) {
                        val databaseManager = DatabaseHandler()
                        databaseManager.updateLocalData()
                    }
                }
                catch (e :Exception)
                {
                    GlobalClass.InformUser(getString(R.string.errorText),"$e", requireContext())
                }

            }

            //check which screen config to load
            if (isHotspot == true)
            {
                //load the hotspot screen config
                updateUIForHotspot()
            }
            else
            {
                //load the expanded observation screen config
                updateUIForObservation()
            }

            //when the back button is clicked
            binding.btnBack.setOnClickListener(){

                //navigate two fragment backwards in the stack
                fragmentManager?.popBackStackImmediate()
                fragmentManager?.popBackStackImmediate()

            }
        }

        // Inflate the layout for this fragment
        return view
    }


    //---------------------------------------------------------------------------------------------
    //Method to configure the UI for the hotspot expanded view
    //---------------------------------------------------------------------------------------------
    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateUIForHotspot()
    {

        try
        {
            //hide the observation components
            binding.llObservationDetails.visibility = View.GONE

            //get the hotspot index
            val hotspotIndex = arguments?.getInt("hotspotIndex")

            //get the distance to the hotspot
            var distance = arguments?.getDouble("distance")

            //get the hotspot using the hotspot index
            var hotspot = GlobalClass.nearbyHotspots[hotspotIndex!!]

            //create local fragment controller
            val fragmentControl = FragmentHandler()

            //instance of full map fragment
            var fullMapView = FullMapFragment()

            //configure full map fragment
            fullMapView.openInFullView = false
            fullMapView.centerOnHotspot = true

            val args = Bundle()

            //add the arguments
            hotspot.lng?.let { args.putDouble("hotspotLong", it) }
            hotspot.lat?.let { args.putDouble("hotspotLat", it) }
            args.putBoolean("hotspotCamera", true)

            //set the instance arguments
            fullMapView.arguments = args

            //show the fragment instance
            fragmentControl.replaceFragment(fullMapView, R.id.cvHotspotMapFragmentContainer, requireActivity().supportFragmentManager)

            //set configure the screen data
            binding.tvHotspotDate.text = hotspot.latestObsDt.toString()
            binding.tvHotspotLocation.text = hotspot.locName

            //check the measurement system settings
            if (GlobalClass.currentUser.isMetric)
            {
                //define decimal format
                val decimalFormat = DecimalFormat("#.##")

                //format the hotspot distance
                val formattedDistance = decimalFormat.format(distance)

                //show the hotspot distance according to the measurement system setting
                binding.tvDistance.text = "${getString(R.string.distanceText)}: ${formattedDistance}KM"
            }
            else
            {
                //check the distance value is not null
                if (distance != null) {

                    //convert the distance to miles
                    distance *= 0.62137119
                }

                //define decimal format
                val decimalFormat = DecimalFormat("#.##")

                //format the hotspot distance
                val formattedDistance = decimalFormat.format(distance)

                //show the hotspot distance according to the measurement system setting
                binding.tvDistance.text = "${getString(R.string.distanceText)}: ${formattedDistance}mi"
            }

            //get bird observations at hotspot
            lifecycleScope.launch {
                try
                {
                    //new ebird communicator instance
                    var ebirdHandler = eBirdAPIHandler()

                    //get birds at a current hotspot
                    hotspot.locId?.let { ebirdHandler.getHotspotBirds("ZA", it) }

                    //new scroll view tools
                    val scrollViewTools = ScrollViewHandler()


                    withContext(Dispatchers.Main) {

                        //where the custom components are created
                        val activityLayout = binding.llBirdList;


                        for (bird in GlobalClass.currentHotspotBirds)
                        {
                            //the custom component "card"
                            var birdDisplay = Card_Observations_All(requireContext())

                            //set the bird name on the card
                            birdDisplay.binding.tvSpecies.text = bird.comName

                            // Define the date format pattern for your input string
                            val inputPattern = "yyyy-MM-dd HH:mm"

                            // Define the desired date format pattern for the output
                            val outputPattern = "yyyy-MM-dd"

                            val inputFormat = SimpleDateFormat(inputPattern)
                            val outputFormat = SimpleDateFormat(outputPattern)

                            //format the sighting date and siplay it on the card
                            var date = inputFormat.parse(bird.obsDt)
                            var localDate = outputFormat.format(date)

                            birdDisplay.binding.tvDate.text = localDate

                            //set the sighting count text
                            birdDisplay.binding.tvSighted.text = "${getString(R.string.foundText)}: ${bird.howMany}"

                            //add the card to the parent view
                            activityLayout.addView(birdDisplay)

                            //call method to add spacer between cards items
                            scrollViewTools.generateSpacer(activityLayout, requireActivity(), 14)
                        }
                    }
                }
                catch (e : Exception)
                {
                    GlobalClass.InformUser(getString(R.string.errorText),"${e.toString()}", requireContext())
                }
            }


            binding.btnDirections.setOnClickListener(){


                //new intent to direct the user to the directions to the hotspot
                val intent = Intent(requireContext(), MapDirectionsActivity::class.java).apply {

                    //pass the hotspot coordinates
                    putExtra("long", hotspot.lng)
                    putExtra("lat", hotspot.lat)
                }

                //start the intent
                startActivity(intent)
            }


        }catch (e: Exception)
        {
            GlobalClass.InformUser(getString(R.string.errorText),"${e.toString()}", requireContext())
        }

        //hide the loading cover
        loadingProgressBar.visibility = View.GONE

    }
    //---------------------------------------------------------------------------------------------



    //---------------------------------------------------------------------------------------------
    //Method to configure the UI for the observation expanded view
    //---------------------------------------------------------------------------------------------
    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateUIForObservation()
    {

        //hide the hotspot view components
        binding.svBirdList.visibility = View.GONE

        //get the index of the selected observation
        val observationIndex = arguments?.getInt("observationIndex")

        //get the distance of the selected observation
        var distance = arguments?.getDouble("distance")

        //get the observation from the list of user observations
        var observation = GlobalClass.userObservations[observationIndex!!]

        //create local fragment controller
        val fragmentControl = FragmentHandler()

        //instance of full map fragment
        var fullMapView = FullMapFragment()

        //configure fragment instance
        fullMapView.openInFullView = false
        fullMapView.centerOnHotspot = true

        val args = Bundle()

        //set instance argument data
        observation.long?.let { args.putDouble("hotspotLong", it) }
        observation.lat?.let { args.putDouble("hotspotLat", it) }
        args.putBoolean("hotspotCamera", true)

        //set instance arguments
        fullMapView.arguments = args

        //load fragment instance
        fragmentControl.replaceFragment(fullMapView, R.id.cvHotspotMapFragmentContainer, requireActivity().supportFragmentManager)

        //configure UI text
        binding.tvHotspot.text = "Observation"
        binding.tvHotspotDate.text = " " + observation.date.toString()
        binding.tvHotspotLocation.text = "${observation.lat},  ${observation.long}"



        //check the measurement system settings
        if (GlobalClass.currentUser.isMetric)
        {
            //define decimal format
            val decimalFormat = DecimalFormat("#.##")

            //format the hotspot distance
            val formattedDistance = decimalFormat.format(distance)

            //show the hotspot distance according to the measurement system setting
            binding.tvDistance.text = "${getString(R.string.distanceText)}: ${formattedDistance}KM"
        }
        else
        {
            //check the distance value is not null
            if (distance != null) {

                //convert the distance to miles
                distance *= 0.62137119
            }

            //define decimal format
            val decimalFormat = DecimalFormat("#.##")

            //format the hotspot distance
            val formattedDistance = decimalFormat.format(distance)

            //show the hotspot distance according to the measurement system setting
            binding.tvDistance.text = "${getString(R.string.distanceText)}: ${formattedDistance}mi"
        }


        //set the expanded observation text
        binding.tvBirdname.text = observation.birdName
        binding.tvCount.text = observation.count.toString()

        //load the image
        lifecycleScope.launch {

                var imageHandler = ImageHandler()
                var image = imageHandler.GetImage(
                    observation.birdName
                )
                binding.imgBirdImageExpanded.setImageBitmap(image)
        }

        //when the directions button is clicked
        binding.btnDirections.setOnClickListener(){

            //intent to directions screen
            val intent = Intent(requireContext(), MapDirectionsActivity::class.java).apply {

                //pass the coordinates of the observation sighting
                putExtra("long", observation.long)
                putExtra("lat", observation.lat)
            }
            //start intent
            startActivity(intent)
        }

        //hide loading cover
        loadingProgressBar.visibility = View.GONE
    }
    //---------------------------------------------------------------------------------------------

}