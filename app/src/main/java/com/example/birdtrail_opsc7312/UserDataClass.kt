package com.example.birdtrail_opsc7312

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.text.TextUtils
import android.util.Patterns
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap

data class UserDataClass(

    var userID: Int = 0,
    var email: String = "",
    var username: String = "",
    var password: String = "",
    var questionID: Int = 0,
    var securityanswer: String = "",
    var badgeID : Int = 0,
    var isMetric: Boolean = true,
    var defaultdistance : Int = 50,
    var score : Int = 0,
    var profilepicture : Bitmap? = null
) {
    @RequiresApi(Build.VERSION_CODES.O)
    fun validateUser(userEmail: String, userPassword: String): Boolean
    {
        //loop through users
        for (indexUser in GlobalClass.userData) {

            //if the entered email matches an existing email
            if (userEmail == indexUser.email && userPassword == indexUser.password) {

                //if user exists
                GlobalClass.currentUser = indexUser

                //exit loop
                break

            }
        }

        return GlobalClass.currentUser.userID != 0
    }

    fun validateUserEmail(attemptedEmail: CharSequence?): Boolean {
        return if (TextUtils.isEmpty(attemptedEmail)) {
            false
        } else {
            Patterns.EMAIL_ADDRESS.matcher(attemptedEmail).matches()
        }
    }


    fun validateUserPassword(attemptedPassword : String, context : Context): String
    {

        var validationErrors = ""

        if (attemptedPassword.length < 8)
        {
            validationErrors += context.getString(R.string.passwordShort) + "\n"
        }

        if (attemptedPassword.count(Char::isDigit) == 0)
        {
            validationErrors+=(context.getString(R.string.passwordNeedsNumber))+ "\n"
        }

        if (attemptedPassword.any(Char::isLowerCase))
        {

        }
        else
        {
            validationErrors+=(context.getString(R.string.passwordNeedsLowerCase))+ "\n"
        }

        if (attemptedPassword.any(Char::isUpperCase))
        {

        }
        else
        {
            validationErrors+=(context.getString(R.string.passwordNeedsUpperCase))+ "\n"
        }


        if (attemptedPassword[0].isUpperCase())
        {

        }
        else
        {
            validationErrors+=(context.getString(R.string.passwordNeedsToStartWithUpperCaseLetter))+ "\n"
        }


        if (attemptedPassword.any { it in context.getString(R.string.passwordSpecialCharacters) })
        {

        }
        else
        {
            validationErrors+=(context.getString(R.string.passwordNeedsSpecialCharacter))+ "\n"
        }


        return validationErrors


    }

    fun validateUserUsername(attemptedUsername : String, context : Context): String
    {

        var validationErrors = ""

        if (attemptedUsername.length < 8)
        {
            validationErrors += context.getString(R.string.usernameShort) + "\n"
        }


        if (attemptedUsername.any(Char::isLowerCase))
        {

        }
        else
        {
            validationErrors+=(context.getString(R.string.usernameNeedsLowerCase))+ "\n"
        }

        if (attemptedUsername.any(Char::isUpperCase))
        {

        }
        else
        {
            validationErrors+=(context.getString(R.string.usernameNeedsUpperCase))+ "\n"
        }


        if (attemptedUsername[0].isUpperCase())
        {

        }
        else
        {
            validationErrors+=(context.getString(R.string.usernameNeedsToStartWithUpperCaseLetter))+ "\n"
        }




        return validationErrors


    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun registerUser(userEmail: String, userUsername: String, userPassword: String, userConfirmPassword: String, securityQuestion: Int, securityAnswer: String, context : Context): String
    {


        var invalidEntries = ""

        //loop through users
        for (indexUser in GlobalClass.userData) {

            //if the entered email matches an existing email
            if (userEmail == indexUser.email) {

                //if user exists
                //GlobalClass.currentUser = indexUser

                invalidEntries += context.getString(R.string.emailAlreadyExists) + "\n"


            }

            //if the entered email matches an existing email
            if (userUsername == indexUser.username) {

                //if user exists
                //GlobalClass.currentUser = indexUser

                invalidEntries += context.getString(R.string.usernameAlreadyExists) + "\n"


            }
        }


        if (!invalidEntries.contains(context.getString(R.string.emailAlreadyExists)))
        {
            var evaluateEmail = validateUserEmail(userEmail)

            if (evaluateEmail == false)
            {
                invalidEntries += context.getString(R.string.emailNotValid) + "\n"
            }
        }

        if (!invalidEntries.contains(context.getString(R.string.usernameAlreadyExists)))
        {
            var evaluateUsername = validateUserUsername(userEmail, context)

            if (evaluateUsername != "")
            {
                invalidEntries += evaluateUsername
            }
        }


        if (userPassword != userConfirmPassword)
        {
            invalidEntries += context.getString(R.string.passwordsNotMatching) + "\n"
        }
        else
        {
            var passwordErrors = validateUserPassword(userPassword, context)

            if (passwordErrors != "")
            {
                invalidEntries += passwordErrors
            }
        }




        if (invalidEntries == "") {

            var newUser = UserDataClass()

            if (GlobalClass.userData.count() > 0)
            {

                newUser.userID = GlobalClass.userData.last().userID + 1

            }

            newUser.email = userEmail
            newUser.password = userPassword
            newUser.questionID = securityQuestion
            newUser.securityanswer = securityanswer
            newUser.profilepicture = ContextCompat.getDrawable(context, R.drawable. imgperson)?.toBitmap()


            GlobalClass.userData.add(newUser)

            /*
            var userID: Int = 0,
            var email: String = "",
            var password: String = "",
            var questionID: Int = 0,
            var securityanswer: String = "",
            var badgeID : Int = 0,
            var isMetric: Boolean = true,
            var defaultdistance : Int = 0,
            var score : Int = 0,
            var profilepicture : Bitmap? = null
             */

        }


        return invalidEntries

        //return Pair(validRegistration, invalidEntries)
    }

}








