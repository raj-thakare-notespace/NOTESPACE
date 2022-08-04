package com.startup.notespace

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var editTextForgotPassEmail : EditText
    private lateinit var resetPasswordButton : MaterialButton
    lateinit var editTextLayout : TextInputLayout

    lateinit var signInTV : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        signInTV = findViewById(R.id.singInTV)

        signInTV.setOnClickListener {
            finish()
        }

        editTextForgotPassEmail = findViewById(R.id.resetPasswordETMaterial)
        resetPasswordButton = findViewById(R.id.resetPassword)
        editTextLayout = findViewById(R.id.editTextLayout)

        resetPasswordButton.setOnClickListener {

            if(editTextForgotPassEmail.text.isEmpty()){
                editTextLayout.error = "Email is required."
                editTextLayout.requestFocus()
            }
            else if(!Patterns.EMAIL_ADDRESS.matcher(editTextForgotPassEmail.text.toString()).matches()){
                editTextLayout.error = "Please provide valid email."
                editTextLayout.requestFocus()
            }
            else{
                Firebase.auth.sendPasswordResetEmail(editTextForgotPassEmail.text.toString().trim()).addOnCompleteListener {
                    if(it.isSuccessful){
                        Toast.makeText(this,"Check your email (spam folder).",Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this,SignInActivity::class.java))
                    }
                    else{
                        Toast.makeText(this,"Try again.",Toast.LENGTH_SHORT).show()
                    }
                }
            }

        }

    }
}