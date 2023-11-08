package com.example.birdtrail_opsc7312

import android.app.AlertDialog
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
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SignUp : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)


        //---------------------------------------------------------------------------------------//
        //initial view config
        //---------------------------------------------------------------------------------------//

        //set view binding
        var binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Hide the action bar
        supportActionBar?.hide()

        //set status bar color
        window.statusBarColor = ContextCompat.getColor(this, R.color.dark_blue)

        //---------------------------------------------------------------------------------------//


        //when the sign up button is clicked
        binding.btnSignUp.setOnClickListener()
        {

            //boolean to determine if all fields are filled in
            var allFilled = true

            //the container where the input fields are
            val container = binding.llFields


            //loop through the inputs
            for (component in container.children)
            {
                //check that the current component is a text edit and that it doesn't contain a value
                if (component is EditText && component.text.isNullOrEmpty())
                {
                    //set the components error text
                    component.error = getString(R.string.missingField)

                    //set the filled status to false
                    allFilled = false
                }
            }


            //if all components are filled in
            if (allFilled == true) {

                val databaseManager = DatabaseHandler()

                MainScope().launch {
                    withContext(Dispatchers.Default) {
                        databaseManager.getQuestions()
                    }


                    var selectedQuestionIndex =
                        GlobalClass.questions.indexOfLast { it.question == binding.spnSecurityQuestion.selectedItem.toString() }

                    //if question exists
                    if (selectedQuestionIndex != -1) {

                        val selectedQuestionID =
                            GlobalClass.questions[selectedQuestionIndex].questionID

                        //register the user with the given inputs
                        val attemptRegister = UserDataClass().registerUser(
                            binding.etEmail.text.toString(),
                            binding.etUsername.text.toString(),
                            binding.etPassword.text.toString(),
                            binding.etConfirmPassword.text.toString(),
                            (selectedQuestionID),
                            binding.etSecurityAnswer.text.toString(),
                            this@SignUp
                        )

                        //if there are no issues with registration
                        if (attemptRegister == "") {

                            //new alert dialog to inform the user that their registration was successful
                            val alert = AlertDialog.Builder(this@SignUp)
                            alert.setTitle(getString(R.string.completedSignUp))
                            alert.setMessage(getString(R.string.instructToSignIn))
                            alert.setPositiveButton(getString(R.string.okText)) { dialog, which ->

                                //take the user back to the landing page once the dialog is dismissed
                                var intent = Intent(this@SignUp, LandingPage::class.java)
                                startActivity(intent)

                            }
                            //show the dialog
                            alert.show()


                        } else {

                            //if the registration is invalid

                            //show the user what information is invalid
                            GlobalClass.InformUser(
                                getString(R.string.failedSignUp),
                                attemptRegister,
                                this@SignUp
                            )
                        }
                    }
                }
            }
        }


        //when the back button is clicked
        binding.tvBack.setOnClickListener()
        {
            //take the user back to the landing page
            var intent = Intent(this, LandingPage::class.java)
            startActivity(intent)
        }


        //when the sign in text is clicked
        binding.tvSignIn.setOnClickListener()
        {
            //take the user to the sign in page
            var intent = Intent(this, SignIn::class.java)
            startActivity(intent)
        }


        //fix for spinner not showing text in correct color
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

    override fun onBackPressed() {
        // Do nothing
    }

}