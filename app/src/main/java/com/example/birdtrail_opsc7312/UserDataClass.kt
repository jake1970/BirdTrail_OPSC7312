package com.example.birdtrail_opsc7312

import android.graphics.Bitmap

data class UserDataClass(

    var userID: Int = 0,
    var username: String = "",
    var password: String = "",
    var questionID: Int = 0,
    var securityanswer: String = "",
    var achID : Int = 0,
    var isMetric: Boolean = true,
    var defaultdistance : Int = 0,
    var score : Int = 0,
    var profilepicture : Bitmap? = null
)
