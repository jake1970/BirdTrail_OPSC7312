package com.example.birdtrail_opsc7312

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.graphics.drawable.toBitmap
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DatabaseHandler
{
    //database
    val db = Firebase.firestore

    //method to get all data of all users form database
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getAllUsers()
    {
        GlobalClass.userData.clear()

        val querySnapshot = db.collection("Users").get().await()
        for (document in querySnapshot){
            val userID: String = document.id
            val username: String = document.data.getValue("username").toString()
            val badgeID: Int = document.data.getValue("badgeID").toString().toInt()
            val defaultDistance: Int = document.data.getValue("defaultDistance").toString().toInt()
            val hasProfile: Boolean = document.data.getValue("hasProfile").toString().toBoolean()
            val ismetric: Boolean = document.data.getValue("isMetric").toString().toBoolean()
            val questionID: String = document.data.getValue("questionID").toString()
            val score: Int = document.data.getValue("score").toString().toInt()
            val securityAnswer: String = document.data.getValue("securityAnswer").toString()

            //create user object
            var user = UserDataClass(
                userID = userID,
                username = username,
                badgeID = badgeID,
                defaultdistance = defaultDistance,
                hasProfile = hasProfile,
                isMetric = ismetric,
                questionID = questionID,
                score = score,
                securityanswer = securityAnswer
            )

            //set current user
            if (userID == GlobalClass.currentUser.userID) {
                if (GlobalClass.currentUser.profilepicture == null)
                {
                    GlobalClass.currentUser = user
                }
            }
            //add user to user list
            GlobalClass.userData.add(user)
        }
    }

    //----------------------------------------------------------------------------------------------

    //method to update local data by fetching from database
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun updateLocalData()
    {
        getAllUsers()
        getUserObservations()
        getUserAchievements()
        getQuestions()
        GlobalClass.UpdateDataBase = false
    }

    //----------------------------------------------------------------------------------------------

    //method to get the current user's observation
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getUserObservations()
    {
        GlobalClass.userObservations.clear()

        val querySnapshot = db.collection("UserObservations").get().await()
        //data formatter
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        for (document in querySnapshot)
        {
            //if observation belongs to current user
            val userID: String = document.data.getValue("userID").toString()
            if (userID==GlobalClass.currentUser.userID || userID!=null)
            {
                val observationID: String = document.id
                val birdname: String = document.data.getValue("birdname").toString()
                val count: Int = document.data.getValue("count").toString().toInt()
                var datestring: String = document.data.getValue("date").toString()
                var date = LocalDate.parse(datestring, formatter)
                var lat: Double = document.data.getValue("lat").toString().toDouble()
                var long: Double = document.data.getValue("long").toString().toDouble()
                var time: String = document.data.getValue("time").toString()

                //create observation
                var observation = UserObservationDataClass(
                    observationID = observationID,
                    userID = userID,
                    lat = lat,
                    long = long,
                    birdName = birdname,
                    count = count,
                    date = date,
                    time = time
                )
                //add observation to observation list
                GlobalClass.userObservations.add(observation)
            }
        }
    }

    //----------------------------------------------------------------------------------------------

    //method to get the achievements of all users
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getUserAchievements()
    {
        GlobalClass.userAchievements.clear()

        val querySnapshot = db.collection("UserAchievements").get().await()
        //data formatter
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        for (document in querySnapshot) {
            val achID = document.data.getValue("achID").toString().toInt()
            var datestring: String = document.data.getValue("date").toString()
            var date = LocalDate.parse(datestring, formatter)
            var userID = document.data.getValue("userID").toString()

            //create achievement
            var userAchievement = UserAchievementsDataClass(
                userID = userID,
                achID = achID,
                date = date
            )
            GlobalClass.userAchievements.add(userAchievement)
        }
    }

    //----------------------------------------------------------------------------------------------

    //method to get security questions
    suspend fun getQuestions()
    {
        GlobalClass.questions.clear()

        val querySnapshot = db.collection("Questions").get().await()
        for (document in querySnapshot) {
            val questionID: String = document.id
            val question: String = document.data.getValue("question").toString()

            //create question
            var newQuestion =  QuestionsDataClass(
                questionID = questionID,
                question = question
            )
            //add question to list
            GlobalClass.questions.add(newQuestion)
        }
    }

    //----------------------------------------------------------------------------------------------

    //method to add user to database
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun AddUser(newUser: UserDataClass, userID: String)
    {
        db.collection("Users")
            .document(userID)
            .set(mapOf(
                "username" to newUser.username,
                "badgeID" to newUser.badgeID,
                "defaultDistance" to newUser.defaultdistance,
                "hasProfile" to newUser.hasProfile,
                "isMetric" to newUser.isMetric,
                "questionID" to newUser.questionID,
                "score" to newUser.score,
                "securityAnswer" to newUser.securityanswer
            )).addOnSuccessListener { GlobalClass.UpdateDataBase = true }
            .await()
    }

    //----------------------------------------------------------------------------------------------

    //method to add user observation to database
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun AddUserObservation(newObservation: UserObservationDataClass)
    {
        db.collection("UserObservations")
            .add(mapOf(
                "userID" to newObservation.userID,
                "date" to LocalDate.now().toString(), //use current date
                "time" to getCurrentTime(), //use current time
                "birdname" to newObservation.birdName,
                "lat" to newObservation.lat,
                "long" to newObservation.long,
                "count" to newObservation.count
            )).addOnSuccessListener { GlobalClass.UpdateDataBase = true }
            .await()
    }

    //----------------------------------------------------------------------------------------------

    //method to add user achievement to database
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun AddUserAchievements(newUserAchievement: UserAchievementsDataClass)
    {
        db.collection("UserAchievements")
            .add(mapOf(
                "achID" to newUserAchievement.achID,
                "userID" to newUserAchievement.userID,
                "date" to newUserAchievement.date.toString()
            )).addOnSuccessListener { GlobalClass.UpdateDataBase = true }
            .await()
    }

    //----------------------------------------------------------------------------------------------

    //method to update user in database
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun updateUser(updatedUser: UserDataClass)
    {
        db.collection("Users").document(updatedUser.userID).update(
            mapOf(
                "badgeID" to updatedUser.badgeID,
                "defaultDistance" to updatedUser.defaultdistance,
                "hasProfile" to updatedUser.hasProfile,
                "isMetric" to updatedUser.isMetric,
                "score" to updatedUser.score
            )
        ).addOnSuccessListener { GlobalClass.UpdateDataBase = true }.await()
    }

    //----------------------------------------------------------------------------------------------

    //method to get the current time as a string
    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentTime(): String {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        return current.format(formatter)
    }

    //----------------------------------------------------------------------------------------------

    //method to get the user's profile image
    suspend fun getUserImage(context: Context, userID: String, userHasImage: Boolean): Bitmap? {

        var defaultUserImage = context.getDrawable(R.drawable.imgdefaultprofile)?.toBitmap()

        if (userHasImage == true) {
            try {
                val storageReference = FirebaseStorage.getInstance().reference.child("UserImages/$userID")

                val imgFile = File.createTempFile("tempImage", "jpg")

                storageReference.getFile(imgFile).await()

                defaultUserImage = BitmapFactory.decodeFile(imgFile.absolutePath)

            } catch (e: Exception) {
                defaultUserImage = context.getDrawable(R.drawable.imgdefaultprofile)?.toBitmap()
            }
        }
        return defaultUserImage
    }

    //----------------------------------------------------------------------------------------------

    //method to set user image
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun setUserImage(context: Context, userID: String, selectedImageUri: Uri) {

        val imageLocation = "UserImages/$userID"
        val storageReference = FirebaseStorage.getInstance().getReference(imageLocation)

        storageReference.putFile(selectedImageUri)
            .addOnFailureListener {
                Toast.makeText(context, "Imaged Failed To Upload", Toast.LENGTH_SHORT).show()
            }
            .addOnSuccessListener {
                GlobalClass.currentUser.hasProfile = true
                Toast.makeText(context, "Updated Profile Image", Toast.LENGTH_SHORT).show()
            }.await()
    }
}