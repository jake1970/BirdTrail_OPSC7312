package com.example.birdtrail_opsc7312

import android.content.Intent
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import android.provider.MediaStore
import android.widget.EditText
import android.widget.Toast
import com.example.birdtrail_opsc7312.databinding.FragmentAddObservationBinding
import com.example.birdtrail_opsc7312.databinding.FragmentAppSettingsBinding
import com.example.birdtrail_opsc7312.databinding.FragmentHomeBinding
import com.example.birdtrail_opsc7312.databinding.LandingPageBinding
import java.io.IOException

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class AppSettings : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var _binding: FragmentAppSettingsBinding? = null
    private val binding get() = _binding!!
    private var selectedImageBitmap : Bitmap? = null
    private var SavedPassword : String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentAppSettingsBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.btnChangePassword.setOnClickListener()
        {
            showPasswordChangeDialog()
        }

        binding.btnChangeProfilePicture.setOnClickListener()
        {
            Gallery()
            UserDataClass().profilepicture = selectedImageBitmap
        }

        binding.btnLogOut.setOnClickListener()
        {
            var intent = Intent(requireActivity(), LandingPage::class.java)
            startActivity(intent)
        }

        binding.btnMetric.setOnClickListener()
        {

            val blue = ContextCompat.getColor(requireContext(),R.color.light_blue)
            val white = ContextCompat.getColor(requireContext(),R.color.white)
            binding.btnImperial.setBackgroundColor(white)
            binding.btnMetric.setBackgroundColor(blue)

        }

        binding.btnImperial.setOnClickListener()
        {
            val blue = ContextCompat.getColor(requireContext(),R.color.light_blue)
            val white = ContextCompat.getColor(requireContext(),R.color.white)
            binding.btnImperial.setBackgroundColor(blue)
            binding.btnMetric.setBackgroundColor(white)
        }
        return view
    }


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
                Toast.makeText(requireContext(), "Password changed successfully", Toast.LENGTH_SHORT).show()
                SavedPassword = newPassword

            } else
            {
                Toast.makeText(requireContext(), "Password changed failed", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("Cancel") { dialog, which ->
            dialog.cancel()
        }

        builder.show()
    }

    private fun Gallery()
    {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_PICK_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null)
        {
            Toast.makeText(requireContext(), "Image Selected Saved", Toast.LENGTH_SHORT).show()
            val selectedImageURI = data.data
            selectedImageBitmap = uriToBitmap(selectedImageURI)
        }

    }

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


    companion object {

        private const val REQUEST_PICK_IMAGE = 123
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AppSettings().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

}