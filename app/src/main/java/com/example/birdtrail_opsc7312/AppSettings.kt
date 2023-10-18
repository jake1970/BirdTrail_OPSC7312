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
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import com.example.birdtrail_opsc7312.databinding.FragmentAppSettingsBinding
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
    //private var sliderValue : Int = 0
    //private var defaultDistance: Int = 0

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentAppSettingsBinding.inflate(inflater, container, false)
        val view = binding.root

        if (GlobalClass.currentUser.isMetric == false)
        {
            /*

            binding.slDistance.valueTo = 40f
            binding.tgUnitSystem.check(R.id.btnImperial)
            //binding.tg

             */
            measurementSymbol = "mi"
            binding.slDistance.valueTo = 40f
            binding.btnImperial.callOnClick()
            binding.btnImperial.setBackgroundColor(resources.getColor(R.color.light_blue))
            binding.btnImperial.setTextColor(resources.getColor(R.color.white))
           // checkButton(binding.btnImperial)
        }
        else
        {
          //  binding.tgUnitSystem.check(R.id.btnMetric)
           // checkButton(binding.btnImperial)
            binding.btnMetric.callOnClick()
            binding.btnMetric.setBackgroundColor(resources.getColor(R.color.light_blue))
            binding.btnMetric.setTextColor(resources.getColor(R.color.white))
        }


        //button change password
        binding.btnChangePassword.setOnClickListener()
        {
            showPasswordChangeDialog()
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

        //log out user
        binding.btnLogOut.setOnClickListener()
        {

            requireContext().getSharedPreferences(myPrefsFile, AppCompatActivity.MODE_PRIVATE)
                .edit()
                .putString(myUserID, null)
                .commit();

            GlobalClass.currentUser = UserDataClass()
            var intent = Intent(requireActivity(), LandingPage::class.java)
            startActivity(intent)
        }

        //metric button
        binding.btnMetric.setOnClickListener()
        {
            if (GlobalClass.currentUser.isMetric == false) {
                GlobalClass.currentUser.isMetric = true
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

        //imperial button
        binding.btnImperial.setOnClickListener()
        {
            if (GlobalClass.currentUser.isMetric == true) {
                GlobalClass.currentUser.isMetric = false
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



        return view
    }

    fun checkButton(checkedButton: Button)
    {
        for (button in binding.tgUnitSystem.children)
        {
            (button as Button).setBackgroundColor(resources.getColor(R.color.white))
            (button as Button).setTextColor(resources.getColor(R.color.black))
        }

        checkedButton.setBackgroundColor(resources.getColor(R.color.light_blue))
        checkedButton.setTextColor(resources.getColor(R.color.white))

       // checkedButton.callOnClick()
    }

//Password Change Dialog, this method will prompt the user as they have elected to change password
//User will enter a password and then go through the validation method before telling the user if the password has been changed
    @RequiresApi(Build.VERSION_CODES.O)
    private fun showPasswordChangeDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Change Password")

        val input = EditText(requireContext())
        input.hint = "Enter new password"
        builder.setView(input)

        builder.setPositiveButton("Change") { dialog, which ->
            val newPassword = input.text.toString()

            val validatePassword = UserDataClass().validateUserPassword(newPassword,requireContext())

            if (validatePassword.isEmpty())
            {
                GlobalClass.InformUser("Confirmation","Password changed successfully", requireContext())
                savedPassword = newPassword

                GlobalClass.currentUser.password = savedPassword as String

            } else
            {
                GlobalClass.InformUser("Confirmation","Password Invalid", requireContext())
            }
        }

        builder.setNegativeButton("Cancel") { dialog, which ->
            dialog.cancel()
        }

        builder.show()
    }
    //method to access gallery on your phone
    private fun Gallery()
    {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_PICK_IMAGE)
    }

    //method to gather the image you have selected and call the Change Uri to Bitmap method to save image
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null)
        {
            val selectedImageURI = data.data
            GlobalClass.InformUser("Confirmation","Image Selected Saved", requireContext())
            selectedImageBitmap = uriToBitmap(selectedImageURI)
            GlobalClass.currentUser.profilepicture = selectedImageBitmap
        }
    }

    //method to change URI to Bitmap format
    private fun uriToBitmap(uri: Uri?): Bitmap? {
        if (uri == null) return null
        return try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    private fun kilometersToMiles(kilometers: Double): Double {
        // 1 kilometer = 0.62137119 miles
        return kilometers * 0.62137119
    }

    private fun milesToKilometers(miles: Double): Double{
        return miles / 0.62137119
    }

    companion object {
        private const val REQUEST_PICK_IMAGE = 123


    }



}