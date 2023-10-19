package com.example.birdtrail_opsc7312

import android.content.Context
import android.view.LayoutInflater
import android.widget.RelativeLayout
import com.example.birdtrail_opsc7312.databinding.CardObservationsAllBinding

class Card_Observations_All (
    context: Context? //FragmentActivity? was Context
) : RelativeLayout(context){

    var binding: CardObservationsAllBinding

    init
    {

        binding = CardObservationsAllBinding.inflate(LayoutInflater.from(context))
        addView(binding.root)

    }
}