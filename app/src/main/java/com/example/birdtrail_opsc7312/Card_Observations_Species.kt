package com.example.birdtrail_opsc7312

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.RelativeLayout
import com.example.birdtrail_opsc7312.databinding.CardObservationsAllBinding
import com.example.birdtrail_opsc7312.databinding.CardObservationsSpeciesBinding

class Card_Observations_Species (
    context: Context? //FragmentActivity? was Context
) : RelativeLayout(context){

    var binding: CardObservationsSpeciesBinding

    init
    {

        binding = CardObservationsSpeciesBinding.inflate(LayoutInflater.from(context))
        addView(binding.root)

    }
}