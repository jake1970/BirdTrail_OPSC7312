package com.example.birdtrail_opsc7312

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.RelativeLayout
import com.example.birdtrail_opsc7312.databinding.CardAchievementBinding
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