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
            val User1 = UserDataClass(
                userID = 1,
                username = "User1",
                password = "Password1",
                questionID = 1,
                securityanswer = "Gordan",
                achID = 1,
                isMetric = true,
                defaultdistance = 50,
                score = 0,
            )

            val User2 = UserDataClass(
                userID = 2,
                username = "User2",
                password = "Password2",
                questionID = 1,
                securityanswer = "Jordan",
                achID = 2,
                isMetric = true,
                defaultdistance = 50,
                score = 0,
            )
            val User3 = UserDataClass(
                userID = 3,
                username = "User3",
                password = "Password3",
                questionID = 1,
                securityanswer = "Heaven",
                achID = 3,
                isMetric = true,
                defaultdistance = 50,
                score = 0,
            )

            userData.add(User1)
            userData.add(User2)
            userData.add(User3)

            val SecurityQuestions1 = QuestionsDataClass(

                questionID = 1,
                question = "In what city or town did your parents meet?"
            )

            val SecurityQuestions2 = QuestionsDataClass(

                questionID = 2,
                question = "What city were you born in?"
            )

            val SecurityQuestions3 = QuestionsDataClass(

                questionID = 3,
                question = "What was the first concert you attended?"
            )

            val SecurityQuestions4 = QuestionsDataClass(

                questionID = 4,
                question = "What is your mothers maiden name?"
            )

            questions.add(SecurityQuestions1)
            questions.add(SecurityQuestions2)
            questions.add(SecurityQuestions3)
            questions.add(SecurityQuestions4)

            val achievements1 = AchievementsDataClass(
                achID = 1,
                name = "50 Birds Sighted",
                requirements = "First Achievement",
                badgeID = 1,
                points = 0,
            )
            val achievements2 = AchievementsDataClass(
                achID = 2,
                name = "30 Birds Sighted",
                requirements = "First Achievement",
                badgeID = 1,
                points = 0,
            )
            val achievements3 = AchievementsDataClass(
                achID = 3,
                name = "20 Birds Sighted",
                requirements = "First Achievement",
                badgeID = 1,
                points = 0,
            )

            acheivements.add(achievements1)
            acheivements.add(achievements2)
            acheivements.add(achievements3)
        }
}



}