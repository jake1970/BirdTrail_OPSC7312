package com.example.birdtrail_opsc7312

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate

data class UserObservationDataClass (
    var observationID: Int = 0,
    var userID: Int = 0,
    var lat: Double = 0.0,
    var long: Double = 0.0,
    var birdName: String = "",
    var eBirdCode: String = "",
    var date: LocalDate = LocalDate.now(),
    var count: Int = 0
)

