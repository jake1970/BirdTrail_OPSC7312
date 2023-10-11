package com.example.birdtrail_opsc7312

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.RelativeLayout
import com.example.birdtrail_opsc7312.databinding.CardAchievementBinding
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
