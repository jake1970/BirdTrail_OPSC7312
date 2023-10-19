package com.example.birdtrail_opsc7312

import android.content.Context
import android.view.LayoutInflater
import android.widget.RelativeLayout
import com.example.birdtrail_opsc7312.databinding.CardSpeciesSelectorBinding

class Card_SpeciesSelector (
    context: Context? //FragmentActivity? was Context
) : RelativeLayout(context){

    var binding: CardSpeciesSelectorBinding

    init
    {
        binding = CardSpeciesSelectorBinding.inflate(LayoutInflater.from(context))
        addView(binding.root)
    }
}
