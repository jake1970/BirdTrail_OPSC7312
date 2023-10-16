package com.example.birdtrail_opsc7312

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.core.view.iterator
import com.example.birdtrail_opsc7312.databinding.ActivitySignUpBinding


class SignUp : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)


        //---------------------------------------------------------------------------------------//
        //initial view config
        //---------------------------------------------------------------------------------------//
        var binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Hide the action bar
        supportActionBar?.hide()

        //set status bar color
        window.statusBarColor = ContextCompat.getColor(this, R.color.dark_blue)

        //---------------------------------------------------------------------------------------//


        binding.btnSignUp.setOnClickListener()
        {

            var allFilled = true
            val container = binding.llFields

            for (component in container.children)
            {
                if (component is EditText && component.text.isNullOrEmpty())
                {
                    component.error = getString(R.string.missingField)
                    allFilled = false
                }
            }



            if (allFilled == true) {
                val attemptRegister = UserDataClass().registerUser(
                    binding.etEmail.text.toString(),
                    binding.etPassword.text.toString(),
                    binding.etConfirmPassword.text.toString(),
                    (binding.spnSecurityQuestion.selectedItemPosition + 1),
                    binding.etSecurityAnswer.text.toString(),
                    this
                )


                if (attemptRegister == "") {
                    GlobalClass.InformUser(getString(R.string.completedSignUp), getString(R.string.instructToSignIn), this)
                    finish()
                } else {
                    GlobalClass.InformUser(getString(R.string.failedSignUp), attemptRegister, this)
                }
            }







        }

        binding.tvBack.setOnClickListener()
        {
            finish()
        }

        binding.tvSignIn.setOnClickListener()
        {
            var intent = Intent(this, SignIn::class.java)
            startActivity(intent)
        }

        binding.spnSecurityQuestion.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                (view as TextView).setTextColor(Color.BLACK) //Change selected text color
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })


    }

}