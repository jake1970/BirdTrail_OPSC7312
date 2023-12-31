package com.example.birdtrail_opsc7312

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate

//data class for user observations

data class UserObservationDataClass @RequiresApi(Build.VERSION_CODES.O) constructor(
    var observationID: String = "",
    var userID: String = "",
    var lat: Double = 0.0,
    var long: Double = 0.0,
    var birdName: String = "",
    var date: LocalDate = LocalDate.now(),
    var count: Int = 0,
    var time: String = ""
)


