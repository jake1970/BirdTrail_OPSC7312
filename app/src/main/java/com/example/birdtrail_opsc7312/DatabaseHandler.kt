package com.example.birdtrail_opsc7312

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.graphics.drawable.toBitmap
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.io.File

class DatabaseHandler
{

    suspend fun getUserImage(context: Context, userID: String, userHasImage: Boolean): Bitmap? {

        var defaultUserImage = context.getDrawable(R.drawable.imgdefaultprofile)?.toBitmap()

        if (userHasImage) {
            try {

                val storageReference = FirebaseStorage.getInstance().reference.child("UserImages/$userID")

                val imgFile = File.createTempFile("tempImage", "jpg")

                storageReference.getFile(imgFile).await()

                defaultUserImage = BitmapFactory.decodeFile(imgFile.absolutePath)

            } catch (e: Exception) {
                GlobalClass.InformUser(
                    context.getString(R.string.errorText),
                    "${e.toString()}",
                    context
                )
            }
        }

        return defaultUserImage
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun setUserImage(context: Context, userID: String, selectedImageUri: Uri) {

        val imageLocation = "UserImages/$userID"
        val storageReference = FirebaseStorage.getInstance().getReference(imageLocation)

        // binding.ivMyProfileImage.image

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