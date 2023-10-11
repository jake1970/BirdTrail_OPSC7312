package com.example.birdtrail_opsc7312

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.RelativeLayout
import com.example.birdtrail_opsc7312.databinding.CardAchievementBinding
import com.example.birdtrail_opsc7312.databinding.CardLeaderboardBinding

class Card_Achievement (
    context: Context? //FragmentActivity? was Context
) : RelativeLayout(context){

    var binding: CardAchievementBinding

    init
    {

        binding = CardAchievementBinding.inflate(LayoutInflater.from(context))
        addView(binding.root)

    }
}