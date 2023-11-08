package com.example.birdtrail_opsc7312

import android.app.AlertDialog
import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.example.HotspotJson2KtKotlin
import com.google.android.material.bottomnavigation.BottomNavigationView
import okhttp3.internal.notifyAll
import java.time.LocalDate

class GlobalClass: Application()
{
    companion object
    {
        @RequiresApi(Build.VERSION_CODES.O)

        //the currently signed in user
        var currentUser = UserDataClass()

        //the amount of observations made by the current user
        var totalObservations = 0

        //list of nearby hotspots
        var nearbyHotspots = arrayListOf<HotspotJson2KtKotlin>()

        //list of birds found at currently selected hotspot
        var currentHotspotBirds = arrayListOf<eBirdJson2KtKotlin>()

        //list of hostpots
        var hotspots = arrayListOf<eBirdJson2KtKotlin>()

        //list of all user observations
        var userObservations = arrayListOf<UserObservationDataClass>()

        //list of all user data
        var userData = arrayListOf<UserDataClass>()

        //list of security question options
        var questions = arrayListOf<QuestionsDataClass>()

        //list of user achievements
        var userAchievements = arrayListOf<UserAchievementsDataClass>()

        //list of achievement options
        var acheivements = arrayListOf<AchievementsDataClass>()

        //list of badge image options
        var badgeImages = arrayListOf<Bitmap>()


        //---------------------------------------------------------------------------------------------
        //method to show a feedback dialog to the user
        //---------------------------------------------------------------------------------------------
        fun InformUser(messageTitle: String, messageText: String, context: Context) {
            val alert = AlertDialog.Builder(context)
            alert.setTitle(messageTitle)
            alert.setMessage(messageText)
            alert.setPositiveButton(context.getString(R.string.okText), null)

            alert.show()
        }
        //---------------------------------------------------------------------------------------------



        //---------------------------------------------------------------------------------------------
        //method to show a custom card informing the user that they have no recorded options
        //---------------------------------------------------------------------------------------------
        fun generateObservationPrompt(activityLayout: LinearLayout, context: Context, fragmentManager: FragmentManager)
        {
            var addDataCard = Card_Observations_Species(context)

            //set the cards image to a bitmap of the plus symbol drawable
            addDataCard.binding.imgBirdImage.setImageBitmap(
                AppCompatResources.getDrawable(context, R.drawable.imgplus)
                ?.toBitmap())

            //set the card styling
            addDataCard.binding.tvSpecies.text = ""
            addDataCard.binding.tvSpecies.visibility = View.GONE
            addDataCard.binding.tvSighted.text = context.getString(R.string.noObservations)


            //when the card is clicked
            addDataCard.setOnClickListener()
            {


                //create local fragment controller
                val fragmentControl = FragmentHandler()

                //set the hompages bottom navigation selected page to none
                (context as Homepage).binding.bottomNavigationView.selectedItemId = R.id.placeholder

                //show the add observation screen
                fragmentControl.replaceFragment(Add_Observation(), R.id.flContent, fragmentManager)



            }

            //add the custom card to the view
            activityLayout.addView(addDataCard)
        }
        //---------------------------------------------------------------------------------------------



        //---------------------------------------------------------------------------------------------
        //method to give the user the default sign up achievement
        //---------------------------------------------------------------------------------------------
        @RequiresApi(Build.VERSION_CODES.O)
        fun initStarterAchievement(context: Context)
        {
            //if the user already has the starter achievement
            var containsInitial = false

            //loop through user achievements
            for (achievement in userAchievements)
            {
                //if the current user already has the starter achievement
                if (achievement.userID == currentUser.userID && achievement.achID == 0)
                {
                    containsInitial = true

                    //exit the loop
                    break
                }
            }

            //if the current user does not have the initial starter achievement
            if (containsInitial == false)
            {
                //add the starter achievement
                userAchievements.add(
                    UserAchievementsDataClass(
                        userID = currentUser.userID,
                        achID = 0,
                        date = currentUser.registrationDate,
                    )
                )
            }

            //call method to get the total observation amount
            evaluateObservations(context)
        }
        //---------------------------------------------------------------------------------------------



        //---------------------------------------------------------------------------------------------
        //method to calculate the total amount of observations that the user has made and calculate their score
        //---------------------------------------------------------------------------------------------
        @RequiresApi(Build.VERSION_CODES.O)
        fun evaluateObservations(context: Context)
        {
            //reset the current users score
            currentUser.score = 0

            //reset the current users observation count
            totalObservations = 0

            //loop through all user observations
            for (observation in userObservations)
            {
                //if the observation was made by the current user
                if (observation.userID == currentUser.userID)
                {
                    //increment the total observations made by he user
                    totalObservations += 1

                    //add 5 points to the users score
                    currentUser.score += 5
                }
            }

            //call method to evaluate the achievements that the user currently has
            evaluateAchievements(context)
        }
        //---------------------------------------------------------------------------------------------



        //---------------------------------------------------------------------------------------------
        //method to calculate the achievements that the current user has unlocked
        //---------------------------------------------------------------------------------------------
        @RequiresApi(Build.VERSION_CODES.O)
        fun evaluateAchievements(context: Context)
        {
            //the value that each achievement unlocked adds
            var scoreMultiplier = 20

            //list of achievements IDs that the has unlocked
            var currentUserAchievementListAchID = arrayListOf<Int>()


            //loop through all user achievements
            for (achievement in userAchievements)
            {
                //if the achievement was unlocked by the current user
                if (achievement.userID == currentUser.userID)
                {
                    //add the unlocked achievement id to the list
                    currentUserAchievementListAchID.add(achievement.achID)
                }
            }

            //sort the list of achievement ids
            currentUserAchievementListAchID.sort()


            //check if the user hasn't unlocked all achievements
            if (currentUserAchievementListAchID.count() != acheivements.count())
            {
                //if the user needs to unlock a new achievement
                var unlockNew = false

                //loop through the achievements
                for (achievement in acheivements)
                {
                    //check if the user already has the current achievement
                    if (currentUserAchievementListAchID.contains(achievement.achID))
                    {
                        //if the user has this achievement
                        unlockNew = false
                    }
                    else
                    {
                        //if the user doesn't have this achievement and the observations required is fulfilled
                        if (achievement.observationsRequired <= totalObservations) {

                            //the user must unlock a new achievement
                            unlockNew = true

                            //exit the loop
                            break
                        }

                    }
                }


                //calculate the amount of points that are added due to unlocked achievements
                val scoreBooster = scoreMultiplier*currentUserAchievementListAchID.count()

                //set the current users score
                currentUser.score = currentUser.score + (scoreBooster)


                //if must unlock a new achievement
                if (unlockNew == true)
                {
                    //add the users new achievement
                    userAchievements.add(
                        UserAchievementsDataClass(
                            userID = currentUser.userID,
                            achID = acheivements[currentUserAchievementListAchID.last().toInt()+1].achID,
                            date = LocalDate.now(),
                        )
                    )

                    //add the score multiplier boost to the users score
                    currentUser.score = currentUser.score + (scoreMultiplier)

                    //inform the user of their new achievement
                    InformUser(acheivements[currentUserAchievementListAchID.last().toInt()+1].name, context.getString(R.string.newBadgeText),  context )
                }
            }
        }
        //---------------------------------------------------------------------------------------------

//
//        @RequiresApi(Build.VERSION_CODES.O)
//        fun User()
//        {
//            userData.add(
//                UserDataClass(
//                    userID = 0,
//                    username = "user1",
//                    email = "user1@gmail.com",
//                    password = "Password1",
//                    questionID = 1,
//                    securityanswer = "Gordan",
//                    badgeID = 1,
//                    isMetric = true,
//                    defaultdistance = 50,
//                    score = 0,
//                    registrationDate = LocalDate.now(),
//                    hasProfile = true
//                ))
//
//            userData.add(
//                UserDataClass(
//                    userID = 1,
//                    username = "user2",
//                    email = "user2@gmail.com",
//                    password = "Password2",
//                    questionID = 1,
//                    securityanswer = "Jordan",
//                    badgeID = 4,
//                    isMetric = true,
//                    defaultdistance = 50,
//                    score = 0,
//                    registrationDate = LocalDate.now()
//                ))
//
//            userData.add(
//                UserDataClass(
//                    userID = 2,
//                    username = "user3",
//                    email = "user3@gmail.com",
//                    password = "Password3",
//                    questionID = 1,
//                    securityanswer = "Heaven",
//                    badgeID = 1,
//                    isMetric = true,
//                    defaultdistance = 50,
//                    score = 0,
//                    registrationDate = LocalDate.now()
//                ))
//
//        }



//        @RequiresApi(Build.VERSION_CODES.O)
//        fun Userachievements()
//        {
//            userAchievements.add(
//                UserAchievementsDataClass(
//                    userID = 0,
//                    achID = 1,
//                    date = LocalDate.now()
//                )
//            )
//
//            userAchievements.add(
//                UserAchievementsDataClass(
//                    userID = 1,
//                    achID = 2,
//                    date = LocalDate.now()
//                )
//            )
//
//            userAchievements.add(
//                UserAchievementsDataClass(
//                    userID = 2,
//                    achID = 3,
//                    date = LocalDate.now()
//                )
//            )
//        }

//        fun securityquestions()
//        {
//
//            questions.add(
//                QuestionsDataClass(
//                    questionID = 1,
//                    question = "In what city or town did your parents meet?"
//                ))
//
//            questions.add(
//                QuestionsDataClass(
//                    questionID = 2,
//                    question = "What city were you born in?"
//                ))
//
//            questions.add(
//                QuestionsDataClass(
//                    questionID = 3,
//                    question = "What was the first concert you attended?"
//                ))
//
//            questions.add(
//                QuestionsDataClass(
//                    questionID = 4,
//                    question = "What is your mothers maiden name?"
//                ))
//
//        }

//        @RequiresApi(Build.VERSION_CODES.O)
//        fun observations()
//        {
//            userObservations.add(
//                UserObservationDataClass(
//                    observationID = 1,
//                    userID = 0,
//                    lat = 18.432339,
//                    long = -33.989640,
//                    birdName = "Afrikaans",
//                    date = LocalDate.now(),
//                    count = 1,
//                ))
//
//            userObservations.add(
//                UserObservationDataClass(
//                    observationID = 2,
//                    userID = 0,
//                    lat = 18.469177,
//                    long = -33.942540,
//                    birdName = "Egyptian Goose",
//                    date = LocalDate.now(),
//                    count = 1,
//                ))
//
//            userObservations.add(
//                UserObservationDataClass(
//                    observationID = 3,
//                    userID = 0,
//                    lat = 18.941699,
//                    long = -33.762354,
//                    birdName = "Red-eye Dove",
//                    date = LocalDate.now(),
//                    count = 1,
//                ))
//
//            userObservations.add(
//                UserObservationDataClass(
//                    observationID = 4,
//                    userID = 1,
//                    lat = 25.663371,
//                    long = -33.762279,
//                    birdName = "Karoo Prinia",
//                    date = LocalDate.now(),
//                    count = 1,
//                ))
//
//            userObservations.add(
//                UserObservationDataClass(
//                    observationID = 5,
//                    userID = 1,
//                    lat = 28.125747,
//                    long = -25.951466,
//                    birdName = "Olive Thrush",
//                    date = LocalDate.now(),
//                    count = 1,
//                ))
//
//            userObservations.add(
//                UserObservationDataClass(
//                    observationID = 6,
//                    userID = 1,
//                    lat = 31.015152,
//                    long = -29.567094,
//                    birdName = "Cape-Robin-Chat",
//                    date = LocalDate.now(),
//                    count = 1,
//                ))
//
//            userObservations.add(
//                UserObservationDataClass(
//                    observationID = 7,
//                    userID = 2,
//                    lat = 26.280044,
//                    long = -29.126579,
//                    birdName = "Laughing Dove",
//                    date = LocalDate.now(),
//                    count = 1,
//                ))
//
//            userObservations.add(
//                UserObservationDataClass(
//                    observationID = 8,
//                    userID = 2,
//                    lat = 19.478346,
//                    long = -33.654693,
//                    birdName = "African Palm Swift",
//                    date = LocalDate.now(),
//                    count = 1,
//                ))
//
//            userObservations.add(
//                UserObservationDataClass(
//                    observationID = 9,
//                    userID = 2,
//                    lat = 29.468184,
//                    long = -23.893709,
//                    birdName = "Rose-ringed Parakeet",
//                    date = LocalDate.now(),
//                    count = 1,
//                ))
//
//            userObservations.add(
//                UserObservationDataClass(
//                    observationID = 10,
//                    userID = 2,
//                    lat = 28.259314,
//                    long = -25.754180,
//                    birdName = "Cape Sparrow",
//                    date = LocalDate.now(),
//                    count = 1,
//                ))
//
//        }

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


//        User()
//        Userachievements()
//        securityquestions()
//        observations()
        AddAcheivements()


        //set user images
        var profileImage = BitmapFactory.decodeResource(applicationContext.resources,R.drawable.imgdefaultprofile)
        for (user in userData)
        {
            user.profilepicture = profileImage
        }

        //add badges
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