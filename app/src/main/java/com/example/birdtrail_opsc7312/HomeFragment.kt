package com.example.birdtrail_opsc7312

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.birdtrail_opsc7312.databinding.ActivityHomepageBinding
import com.example.birdtrail_opsc7312.databinding.FragmentHomeBinding
import kotlinx.coroutines.GlobalScope


class HomeFragment : Fragment(R.layout.fragment_home) {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        //create local fragment controller
        val fragmentControl = FragmentHandler()

        var fullMapView = FullMapFragment()
        fullMapView.openInFullView = true
        fragmentControl.replaceFragment(fullMapView, R.id.cvMapFragmentContainer, requireActivity().supportFragmentManager)
        //fragmentControl.replaceFragment(FullMapFragment(), R.id.cvMapFragmentContainer, requireActivity().supportFragmentManager)



        binding.btnNav.setOnClickListener(){
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.flContent, MapDirections())
            transaction.addToBackStack(null)
            transaction.commit()
        }







        return view
    }

}