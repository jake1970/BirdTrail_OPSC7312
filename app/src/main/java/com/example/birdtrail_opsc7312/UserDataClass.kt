package com.example.birdtrail_opsc7312

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

//data class for user data

data class UserDataClass @RequiresApi(Build.VERSION_CODES.O) constructor(

    var userID: String = "",
    var email: String = "",
    var username: String = "",
    var password: String = "",
    var questionID: String = "",
    var securityanswer: String = "",
    var badgeID : Int = 0,
    var isMetric: Boolean = true,
    var defaultdistance : Int = 50,
    var score : Int = 0,
    var profilepicture : Bitmap? = null,
    var registrationDate : LocalDate = LocalDate.now(),
    var hasProfile : Boolean = false
) {

    //---------------------------------------------------------------------------------------------
    //method to validate that a user exists
    //---------------------------------------------------------------------------------------------
    /*
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

        return GlobalClass.currentUser.userID != -1
    }
     */
    //---------------------------------------------------------------------------------------------



    //---------------------------------------------------------------------------------------------
    //method to validate the users email
    //---------------------------------------------------------------------------------------------
    fun validateUserEmail(attemptedEmail: CharSequence?): Boolean {
        return if (TextUtils.isEmpty(attemptedEmail)) {
            false
        } else {
            Patterns.EMAIL_ADDRESS.matcher(attemptedEmail).matches()
        }
    }
    //---------------------------------------------------------------------------------------------



    //---------------------------------------------------------------------------------------------
    //method to validate the users password
    //---------------------------------------------------------------------------------------------
    fun validateUserPassword(attemptedPassword : String, context : Context): String
    {

        //the errors found with the users password
        var validationErrors = ""

        //check the length of the password
        if (attemptedPassword.length < 8)
        {
            validationErrors += context.getString(R.string.passwordShort) + "\n"
        }

        //check the amount of numbers in the password
        if (attemptedPassword.count(Char::isDigit) == 0)
        {
            validationErrors+=(context.getString(R.string.passwordNeedsNumber))+ "\n"
        }

        //check if the password contains any lower case characters
        if (!attemptedPassword.any(Char::isLowerCase))
        {
            validationErrors+=(context.getString(R.string.passwordNeedsLowerCase))+ "\n"
        }

        //check if the user password contains any uppercase characters
        if (!attemptedPassword.any(Char::isUpperCase))
        {
            validationErrors+=(context.getString(R.string.passwordNeedsUpperCase))+ "\n"
        }

        //check if the users passwords first character is uppercase
        if (!attemptedPassword[0].isUpperCase())
        {
            validationErrors+=(context.getString(R.string.passwordNeedsToStartWithUpperCaseLetter))+ "\n"
        }

        //check if the users password contains a valid special character
        if (!attemptedPassword.any { it in context.getString(R.string.passwordSpecialCharacters) })
        {
            validationErrors+=(context.getString(R.string.passwordNeedsSpecialCharacter))+ "\n"
        }

        //return the password errors
        return validationErrors


    }
    //---------------------------------------------------------------------------------------------



    //---------------------------------------------------------------------------------------------
    //method to validate the users username
    //---------------------------------------------------------------------------------------------
    fun validateUserUsername(attemptedUsername : String, context : Context): String
    {

        //the errors found with the users username
        var validationErrors = ""


        //check the length of the username
        if (attemptedUsername.length < 8)
        {
            validationErrors += context.getString(R.string.usernameShort) + "\n"
        }

        //check if the username contains any lower case characters
        if (!attemptedUsername.any(Char::isLowerCase))
        {
            validationErrors+=(context.getString(R.string.usernameNeedsLowerCase))+ "\n"
        }

        //check if the user username contains any uppercase characters
        if (!attemptedUsername.any(Char::isUpperCase))
        {
            validationErrors+=(context.getString(R.string.usernameNeedsUpperCase))+ "\n"
        }

        //check if the username first character is uppercase
        if (!attemptedUsername[0].isUpperCase())
        {
            validationErrors+=(context.getString(R.string.usernameNeedsToStartWithUpperCaseLetter))+ "\n"
        }


        //return the username errors
        return validationErrors


    }
    //---------------------------------------------------------------------------------------------



    //---------------------------------------------------------------------------------------------
    //method to register a new user
    //---------------------------------------------------------------------------------------------
    @RequiresApi(Build.VERSION_CODES.O)
    fun registerUser(userEmail: String, userUsername: String, userPassword: String, userConfirmPassword: String, securityQuestion: String, securityAnswer: String, context : Context): String
    {

        //the errors found with the users username
        var invalidEntries = ""

        //loop through users
        for (indexUser in GlobalClass.userData) {

            //if the entered email matches an existing email
            if (userEmail == indexUser.email) {

                //if email exists
                invalidEntries += context.getString(R.string.emailAlreadyExists) + "\n"
            }


            //if the entered username matches an existing username
            if (userUsername == indexUser.username) {

                //if username exists
                invalidEntries += context.getString(R.string.usernameAlreadyExists) + "\n"
            }
        }

        //check if the the email doesn't exist
        if (!invalidEntries.contains(context.getString(R.string.emailAlreadyExists)))
        {
            //call method to evaluate the email
            var evaluateEmail = validateUserEmail(userEmail)

            //check if the email is not valid
            if (evaluateEmail == false)
            {
                invalidEntries += context.getString(R.string.emailNotValid) + "\n"
            }
        }

        //check if the the username doesn't exist
        if (!invalidEntries.contains(context.getString(R.string.usernameAlreadyExists)))
        {
            //call method to evaluate the username
            var evaluateUsername = validateUserUsername(userUsername, context)

            //check if the username is not valid
            if (evaluateUsername != "")
            {
                invalidEntries += evaluateUsername
            }
        }


        //check if the password matches the confirm password
        if (userPassword != userConfirmPassword)
        {
            invalidEntries += context.getString(R.string.passwordsNotMatching) + "\n"
        }
        else
        {
            //call method to validate the password
            var passwordErrors = validateUserPassword(userPassword, context)

            //check if the username is not valid
            if (passwordErrors != "")
            {
                invalidEntries += passwordErrors
            }
        }



        //check if all of the user data is valid
        if (invalidEntries == "") {

            //create a new user
            var newUser = UserDataClass()

            //check if there are any existing users
            if (GlobalClass.userData.count() > 0)
            {
                //set the new users ID as an increment of the previous users ID
                newUser.userID = GlobalClass.userData.last().userID + 1
            }

            //set the users details
            newUser.email = userEmail
            newUser.username = userUsername
            newUser.password = userPassword
            newUser.questionID = securityQuestion
            newUser.securityanswer = securityAnswer
            newUser.profilepicture = ContextCompat.getDrawable(context, R.drawable. imgdefaultprofile)?.toBitmap()
            newUser.registrationDate = LocalDate.now()


            //**************************************
            val firebaseAuth = FirebaseAuth.getInstance()
            firebaseAuth.createUserWithEmailAndPassword(newUser.email, newUser.password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {


                        val databaseManager = DatabaseHandler()

                        MainScope().launch {
                            withContext(Dispatchers.Default) {
                                databaseManager.AddUser(newUser, firebaseAuth.currentUser!!.uid)
                            }
                        }

                    }

                    }
            //*************************************

            //add the user to the list of users
            GlobalClass.userData.add(newUser)

        }

        //return the invalid data responses
        return invalidEntries

    }
    //---------------------------------------------------------------------------------------------

}








