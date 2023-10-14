package com.example.birdtrail_opsc7312

import android.app.Application

class GlobalClass: Application()
{
    companion object
    {
        var hotspots = arrayListOf<eBirdJson2KtKotlin>()
        var userObservations = arrayListOf<UserObservationDataClass>()

    }
}