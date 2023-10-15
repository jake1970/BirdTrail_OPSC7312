package com.example.birdtrail_opsc7312

import android.app.Application

class GlobalClass: Application()
{
    companion object
    {
        var hotspots = arrayListOf<eBirdJson2KtKotlin>()
        var userObservations = arrayListOf<UserObservationDataClass>()
        var userData = arrayListOf<UserDataClass>()
        var questions = arrayListOf<QuestionsDataClass>()
        var userAchievements = arrayListOf<UserAchievementsDataClass>()
        var acheivements = arrayListOf<AchievementsDataClass>()
        var badgeImages = arrayListOf<BadgeImagesDataClass>()

        fun main()
        {
            for (i in 1..3) {
                val securityAnswer = when (i) {
                    1 -> "Cape Town"
                    2 -> "Johannesburg"
                    3 -> "Pretoria"
                    else -> ""
                }

                val user = UserDataClass(
                    userID = i,
                    username = "User$i",
                    password = "Password$i",
                    questionID = i,
                    securityanswer = securityAnswer,
                    achID = i,
                    isMetric = true,
                    defaultdistance = 50,
                    score = 0
                )
                userData.add(user)
            }
    }
}



}