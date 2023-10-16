package com.example.birdtrail_opsc7312

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate

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
        var badgeImages = arrayListOf<Bitmap>()

        fun User()
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
        }

        fun securityquestions()
        {
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
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun observations()
        {
            val observations1 = UserObservationDataClass(
                observationID = 1,
                userID = 1,
                lat = 18.432339,
                long = -33.989640,
                birdName = "Afrikaans",
                date = LocalDate.now(),
                count = 1,
            )

            val observations2 = UserObservationDataClass(
                observationID = 2,
                userID = 1,
                lat = 18.469177,
                long = -33.942540,
                birdName = "Egyption Goose",
                date = LocalDate.now(),
                count = 1,
            )

            val observations3 = UserObservationDataClass(
                observationID = 3,
                userID = 1,
                lat = 18.941699,
                long = -33.762354,
                birdName = "Red-eye Dove",
                date = LocalDate.now(),
                count = 1,
            )

            val observations4 = UserObservationDataClass(
                observationID = 4,
                userID = 2,
                lat = 25.663371,
                long = -33.762279,
                birdName = "Karoo Prinia",
                date = LocalDate.now(),
                count = 1,
            )

            val observations5 = UserObservationDataClass(
                observationID = 5,
                userID = 2,
                lat = 28.125747,
                long = -25.951466,
                birdName = "Olive Thrush",
                date = LocalDate.now(),
                count = 1,
            )

            val observations6 = UserObservationDataClass(
                observationID = 6,
                userID = 2,
                lat = 31.015152,
                long = -29.567094,
                birdName = "Cape-Robin-Chat",
                date = LocalDate.now(),
                count = 1,
            )

            val observations7 = UserObservationDataClass(
                observationID = 7,
                userID = 3,
                lat = 26.280044,
                long = -29.126579,
                birdName = "Laughing Dove",
                date = LocalDate.now(),
                count = 1,
            )

            val observations8 = UserObservationDataClass(
                observationID = 8,
                userID = 3,
                lat = 19.478346,
                long = -33.654693,
                birdName = "African Palm Swift",
                date = LocalDate.now(),
                count = 1,
            )

            val observations9 = UserObservationDataClass(
                observationID = 9,
                userID = 3,
                lat = 29.468184,
                long = -23.893709,
                birdName = "Rose-ringed Parakeet",
                date = LocalDate.now(),
                count = 1,
            )

            val observations10 = UserObservationDataClass(
                observationID = 10,
                userID = 3,
                lat = 28.259314,
                long = -25.754180,
                birdName = "Cape Sparrow",
                date = LocalDate.now(),
                count = 1,
            )

            userObservations.add(observations1)
            userObservations.add(observations2)
            userObservations.add(observations3)
            userObservations.add(observations4)
            userObservations.add(observations5)
            userObservations.add(observations6)
            userObservations.add(observations7)
            userObservations.add(observations8)
            userObservations.add(observations9)
            userObservations.add(observations10)
        }

        fun AddAcheivements()
        {
            acheivements.add(
                AchievementsDataClass(
                    achID = 1,
                    name = "First Flight",
                    requirements = "Record your first observation",
                    badgeIndex = 0,
                    observationsRequired = 1,
            ))
            acheivements.add(
                AchievementsDataClass(
                    achID = 2,
                    name = "Novice",
                    requirements = "Record 5 observations",
                    badgeIndex = 1,
                    observationsRequired = 5,
                ))
            acheivements.add(
                AchievementsDataClass(
                    achID = 3,
                    name = "Rookie",
                    requirements = "Record 10 observations",
                    badgeIndex = 2,
                    observationsRequired = 10,
                ))
            acheivements.add(
                AchievementsDataClass(
                    achID = 4,
                    name = "Beginner",
                    requirements = "Record 15 observations",
                    badgeIndex = 3,
                    observationsRequired = 15,
                ))
            acheivements.add(
                AchievementsDataClass(
                    achID = 5,
                    name = "Intermediate",
                    requirements = "Record 20 observations",
                    badgeIndex = 4,
                    observationsRequired = 20,
                ))
            acheivements.add(
                AchievementsDataClass(
                    achID = 6,
                    name = "Skilled",
                    requirements = "Record 25 observations",
                    badgeIndex = 5,
                    observationsRequired = 25,
                ))
            acheivements.add(
                AchievementsDataClass(
                    achID = 7,
                    name = "Advanced",
                    requirements = "Record 30 observations",
                    badgeIndex = 6,
                    observationsRequired = 30,
                ))
            acheivements.add(
                AchievementsDataClass(
                    achID = 8,
                    name = "Senior",
                    requirements = "Record 35 observations",
                    badgeIndex = 7,
                    observationsRequired = 35,
                ))
            acheivements.add(
                AchievementsDataClass(
                    achID = 9,
                    name = "Expert",
                    requirements = "Record 40 observations",
                    badgeIndex = 3,
                    observationsRequired = 40,
                ))
            acheivements.add(
                AchievementsDataClass(
                    achID = 10,
                    name = "Master",
                    requirements = "Record 45 observations",
                    badgeIndex = 0,
                    observationsRequired = 45,
                ))

        }

        fun userAchievement()
        {
            val userachievement1 = UserAchievementsDataClass(
                userID = 1,
                achID = 1,
                date = LocalDate.now()
            )
            override fun onCreate()
            {
                super.onCreate()

                val userachievement2 = UserAchievementsDataClass(
                    userID = 2,
                    achID = 2,
                    date = LocalDate.now()
                )
                val userachievement3 = UserAchievementsDataClass(
                    userID = 3,
                    achID = 3,
                    date = LocalDate.now()
                )

                userAchievements.add(userachievement1)
                userAchievements.add(userachievement2)
                userAchievements.add(userachievement3)

            }
        }
    }
    override fun onCreate()
    {
        super.onCreate()

        //add badges
        badgeImages.add(BitmapFactory.decodeResource(applicationContext.resources,R.drawable.imgbrozebadge1))
        badgeImages.add(BitmapFactory.decodeResource(applicationContext.resources,R.drawable.imgbrozebadge2))
        badgeImages.add(BitmapFactory.decodeResource(applicationContext.resources,R.drawable.imgbrozebadge3))

        badgeImages.add(BitmapFactory.decodeResource(applicationContext.resources,R.drawable.imgsilverbadge1))
        badgeImages.add(BitmapFactory.decodeResource(applicationContext.resources,R.drawable.imgsilverbadge2))
        badgeImages.add(BitmapFactory.decodeResource(applicationContext.resources,R.drawable.imgsilverbadge3))

        badgeImages.add(BitmapFactory.decodeResource(applicationContext.resources,R.drawable.imggoldbadge1))
        badgeImages.add(BitmapFactory.decodeResource(applicationContext.resources,R.drawable.imggoldbadge2))
        badgeImages.add(BitmapFactory.decodeResource(applicationContext.resources,R.drawable.imggoldbadge3))

        badgeImages.add(BitmapFactory.decodeResource(applicationContext.resources,R.drawable.imgbluebadge))
    }
}