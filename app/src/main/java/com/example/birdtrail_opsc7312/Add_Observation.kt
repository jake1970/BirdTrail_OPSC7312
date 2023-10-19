package com.example.birdtrail_opsc7312

import android.annotation.SuppressLint
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Space
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.children
import androidx.core.view.forEach
import androidx.core.view.iterator
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.birdtrail_opsc7312.databinding.FragmentAddObservationBinding
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.*

//fargement for adding user observations
class Add_Observation : Fragment() {

    //binding
    private var _binding: FragmentAddObservationBinding? = null
    private val binding get() = _binding!!

    private var selectedOption = ""

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingPermission")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        _binding = FragmentAddObservationBinding.inflate(inflater, container, false)
        val view = binding.root

        //---------------------------------------------------------------------------------------------------------

        val loadingProgressBar = layoutInflater.inflate(R.layout.loading_cover, null) as ViewGroup
        view.addView(loadingProgressBar)

        //---------------------------------------------------------------------------------------------------------

        binding.tvSpeciesName.setOnClickListener()
        {
            Toast.makeText(requireContext(), binding.tvSpeciesName.text, Toast.LENGTH_SHORT).show()
        }

        binding.btnBack.setOnClickListener()
        {
            val fragmentControl = FragmentHandler()
            //just for now, will return back to whichever screen you were previously on
            fragmentControl.replaceFragment(HomeFragment(), R.id.flContent, parentFragmentManager)
        }

        binding.tvNumberOfSightings.text = "1"

        binding.imgMinusSighting.setOnClickListener()
        {

            var currentSightingValue = (binding.tvNumberOfSightings.text as String).toInt()

            if (currentSightingValue > 1 )
            {
                binding.tvNumberOfSightings.text = (--currentSightingValue).toString()

            }

        }

        binding.imgPlusSighting.setOnClickListener()
        {
            var currentSightingValue = (binding.tvNumberOfSightings.text as String).toInt()

            if (currentSightingValue < 60 )
            {
                binding.tvNumberOfSightings.text = (++currentSightingValue).toString()
            }
        }

        //---------------------------------------------------------------------------------------------
        //Populate list of birds to choose from
        //---------------------------------------------------------------------------------------------
        fun populateBirdOptions(searchList: ArrayList<String>) {

            try
            {
                //bird option container
                val activityLayout = binding.llBirdList;

                for (birdName in searchList)
                {
                    var birdOption = Card_SpeciesSelector(activity)

                    birdOption.binding.rlSelector.visibility = View.INVISIBLE

                    birdOption.binding.tvSpecies.text = birdName

                    //add the new view
                    activityLayout.addView(birdOption)

                    birdOption.setOnClickListener()
                    {
                        activityLayout.forEach { childView ->

                            if (childView is Card_SpeciesSelector) {
                                childView.binding.rlSelector.visibility = View.INVISIBLE
                            }
                        }

                        birdOption.binding.rlSelector.visibility = View.VISIBLE
                        binding.tvSpeciesName.text = birdOption.binding.tvSpecies.text

                        //bird selected
                        if (selectedOption != birdOption.binding.tvSpecies.text.toString()) {

                            loadingProgressBar.visibility = View.VISIBLE

                            //load the image
                            lifecycleScope.launch {

                                try {
                                    var imageHandler = ImageHandler()
                                    var image = imageHandler.GetImage(
                                        birdOption.binding.tvSpecies.text.toString()
                                    )
                                    binding.imgBirdImageExpanded.setImageBitmap(image)
                                }
                                catch (e : Exception)
                                {
                                    Toast.makeText(activity, getString(R.string.failedToLoadImage), Toast.LENGTH_SHORT).show()
                                }
                            }

                            loadingProgressBar.visibility = View.GONE

                        }

                        selectedOption = birdOption.binding.tvSpecies.text.toString()

                    }

                    val scale = requireActivity().resources.displayMetrics.density
                    val pixels = (14 * scale + 0.5f)

                    val spacer = Space(activity)
                    spacer.minimumHeight = pixels.toInt()
                    activityLayout.addView(spacer)
                }

                for (option in activityLayout)
                {
                    if (option is Card_SpeciesSelector && option.binding.tvSpecies.text == selectedOption)
                    {
                        option.callOnClick()
                    }
                }
            }
            catch (e: Exception)
            {
                GlobalClass.InformUser(getString(R.string.errorText),"${e.toString()}", requireContext())
            }
        }

        var uniqueBirdList =  ArrayList<String>()

        for (birds in GlobalClass.hotspots)
        {
            if (!uniqueBirdList.contains(birds.comName))
            {
                uniqueBirdList.add(birds.comName.toString())
            }
        }

        uniqueBirdList.sort()

        //check if there are existing dynamic components
        if (binding.llBirdList.childCount != 0) {

            //clear the dynamic components
            binding.llBirdList.removeAllViews()
        }

        //---------------------------------------------------------------------------------------------
        binding.etSearch.addTextChangedListener { charSequence ->
            try
            {
                //check if there are existing dynamic components
                if (binding.llBirdList.childCount != 0) {

                    //clear the dynamic components
                    binding.llBirdList.removeAllViews()
                }

                if (charSequence.isNullOrEmpty()) {
                    populateBirdOptions(uniqueBirdList)
                } else {
                    var searchBirdList = ArrayList<String>()

                    for (birds in uniqueBirdList) {
                        // if (charSequence?.let { birds.contains(it) } == true) {
                        if (birds.lowercase().contains(charSequence.toString().lowercase())) {
                            searchBirdList.add(birds)
                        }
                    }
                    populateBirdOptions(searchBirdList)
                }
            }
            catch (e: Exception)
            {
                GlobalClass.InformUser(getString(R.string.errorText),"${e.toString()}", requireContext())
            }
        }

        //---------------------------------------------------------------------------------------------
        //get user location
        var userLocation: Location? = null
        try
        {
            var mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
            mFusedLocationClient.lastLocation.addOnCompleteListener(requireActivity()) { task ->
                userLocation = task.result
                if (userLocation != null) {

                    populateBirdOptions(uniqueBirdList)

                    binding.llBirdList.children.first().callOnClick()

                    binding.tvCurrentLocation.text = " Lon: " + userLocation!!.longitude.toString() + " Lat: " + userLocation!!.latitude.toString()

                    loadingProgressBar.visibility = View.GONE

                }
            }



        }catch (e: Exception)
        {
            GlobalClass.InformUser(getString(R.string.errorText),"${e.toString()}", requireContext())
        }

        //---------------------------------------------------------------------------------------------

        binding.btnEnter.setOnClickListener()
        {

            try {
                var newSighting = UserObservationDataClass()

                if (GlobalClass.userObservations.count() > 0) {
                    newSighting.observationID = (GlobalClass.userObservations.last().observationID + 1)
                }

                newSighting.userID = GlobalClass.currentUser.userID

                newSighting.lat = userLocation!!.latitude
                newSighting.long = userLocation!!.longitude

                newSighting.birdName = binding.tvSpeciesName.text.toString()

                //date is set by default
                newSighting.count = binding.tvNumberOfSightings.text.toString().toInt()

                GlobalClass.userObservations.add(newSighting)

                requireActivity().findViewById<View>(R.id.home).callOnClick()

                GlobalClass.evaluateObservations(requireActivity())

            }
            catch (e: Exception)
            {
                GlobalClass.InformUser(getString(R.string.errorText),"${e.toString()}", requireContext())
            }
        }

        // Inflate the layout for this fragment
        return view
    }
}