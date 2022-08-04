package com.startup.notespace

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging

class SignInActivity : AppCompatActivity() {

    private lateinit var passwordEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var signInButton: Button
    private lateinit var emailTextLayoutSignIn: TextInputLayout
    private lateinit var passwordTextLayoutSignIn: TextInputLayout

    private lateinit var createAnAccountTextView: TextView
    private lateinit var forgotPasswordTV: TextView

    private lateinit var auth: FirebaseAuth

    lateinit var progressDialog: ProgressDialog

    private var token = ""

    override fun onStart() {
        super.onStart()
        if (auth.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        FirebaseApp.initializeApp(this)

        passwordEditText = findViewById(R.id.passwordEditTextSignIn)
        emailEditText = findViewById(R.id.emailEditTextSignIn)
        signInButton = findViewById(R.id.signInButtonSignIn)

        emailTextLayoutSignIn = findViewById(R.id.emailTextLayoutSignIn)
        passwordTextLayoutSignIn = findViewById(R.id.passwordTextLayoutSignIn)

        forgotPasswordTV = findViewById(R.id.forgotPasswordTV)

        forgotPasswordTV.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

        createAnAccountTextView = findViewById(R.id.createAnAccountSignIn)

        progressDialog = ProgressDialog(this)
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.isIndeterminate = true

        createAnAccountTextView.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        auth = Firebase.auth

        signInButton.setOnClickListener {

            if(emailEditText.text.toString().isEmpty()){
                emailTextLayoutSignIn.error = "Email can not be empty"
            }
            else if(passwordEditText.text.toString().isEmpty()){
                passwordTextLayoutSignIn.error = "Password can not be empty"
            }
            else{

                try {
                    progressDialog.show()
                    progressDialog.setContentView(R.layout.progress_bar)
                    progressDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

                    auth.signInWithEmailAndPassword(
                        emailEditText.text.toString(),
                        passwordEditText.text.toString()
                    ).addOnCompleteListener(this) {
                        if (it.isSuccessful) {
                            FirebaseService.sharedPref = getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
                            FirebaseMessaging.getInstance().token.addOnCompleteListener {
                                if (it.isSuccessful) {
                                    FirebaseService.token = it.result
                                    FirebaseDatabase.getInstance().reference.child("users")
                                        .child(Firebase.auth.currentUser!!.uid)
                                        .child("token")
                                        .setValue(it.result)

                                }
                            }
                            progressDialog.dismiss()
                            Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, MainActivity::class.java))
                            SignUpVerification().finish()
                        } else {
                            progressDialog.dismiss()
                            Toast.makeText(this, "Login Unsuccessful", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                }
            }
        }

    }
}