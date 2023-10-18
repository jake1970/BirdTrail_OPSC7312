package com.example.birdtrail_opsc7312

import android.app.AlertDialog
import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.example.HotspotJson2KtKotlin
import okhttp3.internal.notifyAll
import java.time.LocalDate

class GlobalClass: Application()
{
    companion object
    {
        @RequiresApi(Build.VERSION_CODES.O)
        var currentUser = UserDataClass()
        var totalObservations = 0

        var nearbyHotspots = arrayListOf<HotspotJson2KtKotlin>()

        var hotspots = arrayListOf<eBirdJson2KtKotlin>()
        var userObservations = arrayListOf<UserObservationDataClass>()
        var userData = arrayListOf<UserDataClass>()
        var questions = arrayListOf<QuestionsDataClass>()
        var userAchievements = arrayListOf<UserAchievementsDataClass>()
        var acheivements = arrayListOf<AchievementsDataClass>()
        var badgeImages = arrayListOf<Bitmap>()

        fun InformUser(messageTitle: String, messageText: String, context: Context) {
            val alert = AlertDialog.Builder(context)
            alert.setTitle(messageTitle)
            alert.setMessage(messageText)
            alert.setPositiveButton(context.getString(R.string.okText), null)

            alert.show()
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun initStarterAchievement(context: Context)
        {
            var containsInitial = false

            for (achievement in userAchievements)
            {
                if (achievement.userID == currentUser.userID && achievement.achID == 0)
                {
                    containsInitial = true
                    break
                }
            }

            if (containsInitial == false)
            {
                userAchievements.add(
                    UserAchievementsDataClass(
                        userID = currentUser.userID,
                        achID = 0,
                        date = currentUser.registrationDate,
                    )
                )
            }

            evaluateObservations(context)
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun evaluateObservations(context: Context)
        {
            currentUser.score = 0
            totalObservations = 0

            for (observation in userObservations)
            {
                if (observation.userID == currentUser.userID)
                {
                    totalObservations += 1
                    currentUser.score += 5 //currentUser.score + 5
                }
            }

        evaluateAchievements(context)
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun evaluateAchievements(context: Context)
        {
            var scoreMultiplier = 20

            var currentUserAchievementListAchID = arrayListOf<Int>()


            for (achievement in userAchievements)
            {
                if (achievement.userID == currentUser.userID)
                {
                    currentUserAchievementListAchID.add(achievement.achID)
                }
            }

            currentUserAchievementListAchID.sort()

            //Toast.makeText(context, "current badge count:  " + currentUserAchievementListAchID.count().toString(), Toast.LENGTH_LONG).show()


            var unlockNew = false

            for (achievement in acheivements) //ach 1
            {
                if (currentUserAchievementListAchID.contains(achievement.achID))
                {
                    //if the user has this achievement
                    unlockNew = false
                }
                else
                {
                    //if the user doesnt have this achievement
                    if (achievement.observationsRequired <= totalObservations) {
                        unlockNew = true
                        break
                    }

                }
            }



            val scoreBooster = scoreMultiplier*currentUserAchievementListAchID.count()
            currentUser.score = currentUser.score + (scoreBooster)


            if (unlockNew == true)
            {
                userAchievements.add(
                    UserAchievementsDataClass(
                        userID = currentUser.userID,
                        achID = acheivements[currentUserAchievementListAchID.last().toInt()+1].achID,
                        date = LocalDate.now(),
                    )
                )

                currentUser.score = currentUser.score + (scoreMultiplier)

                InformUser(acheivements[currentUserAchievementListAchID.last().toInt()+1].name, context.getString(R.string.newBadgeText),  context )
            }


        }


        @RequiresApi(Build.VERSION_CODES.O)
        fun User()
        {
            userData.add(
                UserDataClass(
                    userID = 0,
                    username = "user1",
                    email = "user1@gmail.com",
                    password = "Password1",
                    questionID = 1,
                    securityanswer = "Gordan",
                    badgeID = 1,
                    isMetric = true,
                    defaultdistance = 50,
                    score = 0,
                    registrationDate = LocalDate.now()
                ))

            userData.add(
                UserDataClass(
                    userID = 1,
                    username = "user2",
                    email = "user2@gmail.com",
                    password = "Password2",
                    questionID = 1,
                    securityanswer = "Jordan",
                    badgeID = 4,
                    isMetric = true,
                    defaultdistance = 50,
                    score = 0,
                    registrationDate = LocalDate.now()
                ))

            userData.add(
                UserDataClass(
                    userID = 2,
                    username = "user3",
                    email = "user3@gmail.com",
                    password = "Password3",
                    questionID = 1,
                    securityanswer = "Heaven",
                    badgeID = 1,
                    isMetric = true,
                    defaultdistance = 50,
                    score = 0,
                    registrationDate = LocalDate.now()
                ))

        }



        @RequiresApi(Build.VERSION_CODES.O)
        fun Userachievements()
        {
            userAchievements.add(
                UserAchievementsDataClass(
                    userID = 0,
                    achID = 1,
                    date = LocalDate.now()
                )
            )

            //---------------------------------------------------------------------

            /*userAchievements.add(
                UserAchievementsDataClass(
                    userID = 0,
                    achID = 2,
                    date = LocalDate.now()
                )
            )

             */
            //---------------------------------------------------------------------

            userAchievements.add(
                UserAchievementsDataClass(
                    userID = 1,
                    achID = 2,
                    date = LocalDate.now()
                )
            )

            userAchievements.add(
                UserAchievementsDataClass(
                    userID = 2,
                    achID = 3,
                    date = LocalDate.now()
                )
            )
        }

        fun securityquestions()
        {

            questions.add(
                QuestionsDataClass(
                    questionID = 1,
                    question = "In what city or town did your parents meet?"
                ))

            questions.add(
                QuestionsDataClass(
                    questionID = 2,
                    question = "What city were you born in?"
                ))

            questions.add(
                QuestionsDataClass(
                    questionID = 3,
                    question = "What was the first concert you attended?"
                ))

            questions.add(
                QuestionsDataClass(
                    questionID = 4,
                    question = "What is your mothers maiden name?"
                ))

        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun observations()
        {
            userObservations.add(
                UserObservationDataClass(
                    observationID = 1,
                    userID = 0,
                    lat = 18.432339,
                    long = -33.989640,
                    birdName = "Afrikaans",
                    date = LocalDate.now(),
                    count = 1,
                ))

            userObservations.add(
                UserObservationDataClass(
                    observationID = 2,
                    userID = 0,
                    lat = 18.469177,
                    long = -33.942540,
                    birdName = "Egyptian Goose",
                    date = LocalDate.now(),
                    count = 1,
                ))

            userObservations.add(
                UserObservationDataClass(
                    observationID = 3,
                    userID = 0,
                    lat = 18.941699,
                    long = -33.762354,
                    birdName = "Red-eye Dove",
                    date = LocalDate.now(),
                    count = 1,
                ))

            userObservations.add(
                UserObservationDataClass(
                    observationID = 4,
                    userID = 1,
                    lat = 25.663371,
                    long = -33.762279,
                    birdName = "Karoo Prinia",
                    date = LocalDate.now(),
                    count = 1,
                ))

            userObservations.add(
                UserObservationDataClass(
                    observationID = 5,
                    userID = 1,
                    lat = 28.125747,
                    long = -25.951466,
                    birdName = "Olive Thrush",
                    date = LocalDate.now(),
                    count = 1,
                ))

            userObservations.add(
                UserObservationDataClass(
                    observationID = 6,
                    userID = 1,
                    lat = 31.015152,
                    long = -29.567094,
                    birdName = "Cape-Robin-Chat",
                    date = LocalDate.now(),
                    count = 1,
                ))

            userObservations.add(
                UserObservationDataClass(
                    observationID = 7,
                    userID = 2,
                    lat = 26.280044,
                    long = -29.126579,
                    birdName = "Laughing Dove",
                    date = LocalDate.now(),
                    count = 1,
                ))

            userObservations.add(
                UserObservationDataClass(
                    observationID = 8,
                    userID = 2,
                    lat = 19.478346,
                    long = -33.654693,
                    birdName = "African Palm Swift",
                    date = LocalDate.now(),
                    count = 1,
                ))

            userObservations.add(
                UserObservationDataClass(
                    observationID = 9,
                    userID = 2,
                    lat = 29.468184,
                    long = -23.893709,
                    birdName = "Rose-ringed Parakeet",
                    date = LocalDate.now(),
                    count = 1,
                ))

            userObservations.add(
                UserObservationDataClass(
                    observationID = 10,
                    userID = 2,
                    lat = 28.259314,
                    long = -25.754180,
                    birdName = "Cape Sparrow",
                    date = LocalDate.now(),
                    count = 1,
                ))

        }

        fun AddAcheivements()
        {
            acheivements.add(
                AchievementsDataClass(
                    achID = 0,
                    name = "New Comer",
                    requirements = "Sign up for an account",
                    badgeIndex = 0,
                    observationsRequired = 1,
                ))
            acheivements.add(
                AchievementsDataClass(
                    achID = 1,
                    name = "First Flight",
                    requirements = "Record your first observation",
                    badgeIndex = 1,
                    observationsRequired = 1,
            ))
            acheivements.add(
                AchievementsDataClass(
                    achID = 2,
                    name = "Novice",
                    requirements = "Record 5 observations",
                    badgeIndex = 2,
                    observationsRequired = 5,
                ))
            acheivements.add(
                AchievementsDataClass(
                    achID = 3,
                    name = "Rookie",
                    requirements = "Record 10 observations",
                    badgeIndex = 3,
                    observationsRequired = 10,
                ))
            acheivements.add(
                AchievementsDataClass(
                    achID = 4,
                    name = "Beginner",
                    requirements = "Record 15 observations",
                    badgeIndex = 4,
                    observationsRequired = 15,
                ))
            acheivements.add(
                AchievementsDataClass(
                    achID = 5,
                    name = "Intermediate",
                    requirements = "Record 20 observations",
                    badgeIndex = 5,
                    observationsRequired = 20,
                ))
            acheivements.add(
                AchievementsDataClass(
                    achID = 6,
                    name = "Skilled",
                    requirements = "Record 25 observations",
                    badgeIndex = 6,
                    observationsRequired = 25,
                ))
            acheivements.add(
                AchievementsDataClass(
                    achID = 7,
                    name = "Advanced",
                    requirements = "Record 30 observations",
                    badgeIndex = 7,
                    observationsRequired = 30,
                ))
            acheivements.add(
                AchievementsDataClass(
                    achID = 8,
                    name = "Senior",
                    requirements = "Record 35 observations",
                    badgeIndex = 8,
                    observationsRequired = 35,
                ))
            acheivements.add(
                AchievementsDataClass(
                    achID = 9,
                    name = "Expert",
                    requirements = "Record 40 observations",
                    badgeIndex = 9,
                    observationsRequired = 40,
                ))
            acheivements.add(
                AchievementsDataClass(
                    achID = 10,
                    name = "Master",
                    requirements = "Record 45 observations",
                    badgeIndex = 10,
                    observationsRequired = 45,
                ))

        }

    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate()
    {
        super.onCreate()

        User()
        Userachievements()
        securityquestions()
        observations()
        AddAcheivements()

        //set user images
        var profileImage = BitmapFactory.decodeResource(applicationContext.resources,R.drawable.imgdefaultprofile)
        for (user in userData)
        {
            user.profilepicture = profileImage
        }

        //add badges#
        badgeImages.add(BitmapFactory.decodeResource(applicationContext.resources,R.drawable.imgdefaultbadge))
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