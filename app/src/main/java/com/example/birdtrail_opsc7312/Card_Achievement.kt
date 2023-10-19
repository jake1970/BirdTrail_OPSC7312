package com.example.birdtrail_opsc7312

import android.content.Context
import android.view.LayoutInflater
import android.widget.RelativeLayout
import com.example.birdtrail_opsc7312.databinding.CardAchievementBinding

class Card_Achievement (
    context: Context? //FragmentActivity? was Context
) : RelativeLayout(context){

    var binding: CardAchievementBinding
    var badgeID = 0

    init
    {

        binding = CardAchievementBinding.inflate(LayoutInflater.from(context))
        addView(binding.root)

    }
}