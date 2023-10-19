package com.example.birdtrail_opsc7312

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate

//data class for user achievement data
data class UserAchievementsDataClass @RequiresApi(Build.VERSION_CODES.O) constructor(

    var achID: Int = 0,
    var userID: Int = 0,
    var date: LocalDate = LocalDate.now(),
)


