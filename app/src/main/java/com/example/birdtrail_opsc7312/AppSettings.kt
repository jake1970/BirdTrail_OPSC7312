package com.example.birdtrail_opsc7312

import android.content.Intent
import android.app.Activity
import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import com.example.birdtrail_opsc7312.databinding.FragmentAppSettingsBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.*
import java.io.IOException
import kotlin.math.roundToInt

class AppSettings : Fragment() {

    private val myPrefsFile = "MyPrefsFile";
    private val myUserID = "";

    //binding along with the image and new password
    private var _binding: FragmentAppSettingsBinding? = null
    private val binding get() = _binding!!
    private var selectedImageBitmap : Bitmap? = null
    private var savedPassword : String? = ""
    private var measurementSymbol = "KM"

    private var startingDistance: Int = 0
    private var startingMetric: Boolean = false

    private lateinit var thisView : FrameLayout

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentAppSettingsBinding.inflate(inflater, container, false)
        val view = binding.root

        //starting values
        startingDistance = GlobalClass.currentUser.defaultdistance
        startingMetric = GlobalClass.currentUser.isMetric

        thisView = view

        try
        {
            //load current settings
            if (GlobalClass.currentUser.isMetric == false)
            {
                measurementSymbol = "mi"
                binding.slDistance.valueTo = 40f
                binding.btnImperial.callOnClick()
                binding.btnImperial.setBackgroundColor(resources.getColor(R.color.light_blue))
                binding.btnImperial.setTextColor(resources.getColor(R.color.white))
            }
            else
            {
                binding.btnMetric.callOnClick()
                binding.btnMetric.setBackgroundColor(resources.getColor(R.color.light_blue))
                binding.btnMetric.setTextColor(resources.getColor(R.color.white))
            }

            //---------------------------------------------------------------------------------------------

            //button change password
            binding.btnChangePassword.setOnClickListener()
            {

                val firebaseAuth = FirebaseAuth.getInstance()
                firebaseAuth.sendPasswordResetEmail(firebaseAuth.currentUser!!.email.toString())
                Toast.makeText(requireActivity(), getString(R.string.passwordResetPrefix) + firebaseAuth.currentUser!!.email.toString(), Toast.LENGTH_SHORT).show()

            }

            //get slider default distance
            binding.slDistance.addOnChangeListener { slider, value, fromUser ->
                // Update the variable with the current slider value

                GlobalClass.currentUser.defaultdistance = value.toInt()
                binding.tvDistanceValue.text = "${value.toInt()}$measurementSymbol"
            }

            //change profile picture
            binding.btnChangeProfilePicture.setOnClickListener()
            {
                Gallery()
            }

            //---------------------------------------------------------------------------------------------

            //log out user
            binding.btnLogOut.setOnClickListener()
            {

                requireContext().getSharedPreferences(myPrefsFile, AppCompatActivity.MODE_PRIVATE)
                    .edit()
                    .putString(myUserID, null)
                    .commit();


                //invalidate the users sign in status
                val firebaseAuth = FirebaseAuth.getInstance()
                firebaseAuth.signOut()

                //update the db on next login?
                //prime the database to be read from upon the next sign in
                GlobalClass.currentUser = UserDataClass()
                var intent = Intent(requireActivity(), LandingPage::class.java)
                startActivity(intent)
            }

            //---------------------------------------------------------------------------------------------

            //metric button
            binding.btnMetric.setOnClickListener()
            {
                if (GlobalClass.currentUser.isMetric == false) {
                    GlobalClass.currentUser.isMetric = true

                    //update user
                    MainScope().launch {
                        withContext(Dispatchers.Default) {

                            var dataHandler = DatabaseHandler()
                            dataHandler.updateUser(GlobalClass.currentUser)
                        }
                    }

                    measurementSymbol = "KM"

                    binding.tvDistanceValue.text = "${binding.slDistance.value}$measurementSymbol"

                    binding.slDistance.valueTo = 60f

                    var convertedBack = milesToKilometers(binding.slDistance.value.toDouble()).roundToInt().toFloat()

                    if (convertedBack <= 60)
                    {
                        binding.slDistance.value = kilometersToMiles(binding.slDistance.value.toDouble()).roundToInt().toFloat()
                    }
                    else
                    {
                        binding.slDistance.value = 60f
                    }
                }
                checkButton(binding.btnMetric)
            }

            //---------------------------------------------------------------------------------------------

            //imperial button
            binding.btnImperial.setOnClickListener()
            {
                if (GlobalClass.currentUser.isMetric == true) {
                    GlobalClass.currentUser.isMetric = false

                    //update user
                    MainScope().launch {
                        withContext(Dispatchers.Default) {

                            var dataHandler = DatabaseHandler()
                            dataHandler.updateUser(GlobalClass.currentUser)
                        }
                    }

                    measurementSymbol = "mi"

                    var convertedBack = kilometersToMiles(binding.slDistance.value.toDouble()).roundToInt().toFloat()

                    if (convertedBack <= 40)
                    {
                        binding.slDistance.value = kilometersToMiles(binding.slDistance.value.toDouble()).roundToInt().toFloat()
                    }
                    else
                    {
                        binding.slDistance.value = 40f
                    }

                    binding.tvDistanceValue.text = "${binding.slDistance.value}$measurementSymbol"

                    binding.slDistance.valueTo = 40f

                }
                checkButton(binding.btnImperial)
            }

            binding.slDistance.value = GlobalClass.currentUser.defaultdistance.toFloat()
            binding.tvDistanceValue.text = "${binding.slDistance.value}$measurementSymbol"


        }catch (e: Exception)
        {
            GlobalClass.InformUser(getString(R.string.errorText),"${e.toString()}", requireContext())
        }
        return view
    }

    //---------------------------------------------------------------------------------------------

    fun checkButton(checkedButton: Button)
    {
        try
        {
            for (button in binding.tgUnitSystem.children)
            {
                (button as Button).setBackgroundColor(resources.getColor(R.color.white))
                (button as Button).setTextColor(resources.getColor(R.color.black))
            }

            checkedButton.setBackgroundColor(resources.getColor(R.color.light_blue))
            checkedButton.setTextColor(resources.getColor(R.color.white))

        }catch (e: Exception)
        {
            GlobalClass.InformUser(getString(R.string.errorText),"${e.toString()}", requireContext())
        }
    }



    //method to access gallery on user's phone
    private fun Gallery()
    {
        try
        {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_PICK_IMAGE)
        }catch (e: Exception)
        {
            GlobalClass.InformUser(getString(R.string.errorText),"${e.toString()}", requireContext())
        }
    }

    //---------------------------------------------------------------------------------------------

    //method to gather the image you have selected and call the Change Uri to Bitmap method to save image
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        try
        {
            if (requestCode == REQUEST_PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
                val selectedImageURI = data.data
                GlobalClass.InformUser(
                    getString(R.string.confirmationText),
                    getString(R.string.selectedImageSaved),
                    requireContext()
                )
                selectedImageBitmap = uriToBitmap(selectedImageURI)
                GlobalClass.currentUser.profilepicture = selectedImageBitmap

                if (selectedImageURI != null) {

                    //Read Data
                    MainScope().launch {

                        val loadingProgressBar =
                            layoutInflater.inflate(R.layout.loading_cover, null) as ViewGroup
                        thisView.addView(loadingProgressBar)



                        withContext(Dispatchers.Default) {
                            val databaseManager = DatabaseHandler()

                            val currentImageState = GlobalClass.currentUser.hasProfile

                            databaseManager.setUserImage(
                                requireActivity(),
                                GlobalClass.currentUser.userID,
                                selectedImageURI
                            )


                            if (currentImageState == false)
                            {
                                databaseManager.updateUser(GlobalClass.currentUser)
                            }

                        }


                        loadingProgressBar.visibility = View.GONE
                    }
                }
            }
        }
        catch (e: Exception)
        {
            GlobalClass.InformUser(getString(R.string.errorText),"${e.toString()}", requireContext())
        }
    }

    //---------------------------------------------------------------------------------------------

    //method to change URI to Bitmap format
    private fun uriToBitmap(uri: Uri?): Bitmap? {
        if (uri == null) return null
        return try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: IOException) {
            e.printStackTrace()
            GlobalClass.InformUser(getString(R.string.errorText),"${e.toString()}", requireContext())
            null
        }
    }

    //---------------------------------------------------------------------------------------------

    private fun kilometersToMiles(kilometers: Double): Double {
        // 1 kilometer = 0.62137119 miles
        return kilometers * 0.62137119
    }

    private fun milesToKilometers(miles: Double): Double{
        return miles / 0.62137119
    }

    //---------------------------------------------------------------------------------------------

    companion object {
        private const val REQUEST_PICK_IMAGE = 123
    }

    //method to destroy fragment view
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onDestroyView() {
        if ((startingMetric != GlobalClass.currentUser.isMetric) || startingDistance != GlobalClass.currentUser.defaultdistance )
        {
            try
            {
                val databaseManager = DatabaseHandler()

                //update user data in database
                GlobalScope.launch {

                    databaseManager.updateUser(GlobalClass.currentUser)

                    withContext(Dispatchers.Main) {

                    }
                }
            }
            catch (e: Exception)
            {
                GlobalClass.InformUser(getString(R.string.errorText),"${e.toString()}", requireContext())
            }
        }
        super.onDestroyView()
    }
}