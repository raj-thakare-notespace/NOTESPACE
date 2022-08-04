package com.example.notespace

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase


class SignUpActivity : AppCompatActivity() {

    private lateinit var passwordEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var usernameEditText: EditText
    private lateinit var signInButton: MaterialButton
    private lateinit var errorTextView: TextView

    private lateinit var usernameTextLayoutSignUp: TextInputLayout
    private lateinit var emailTextLayoutSignUp: TextInputLayout
    private lateinit var passwordTextLayoutSignUp: TextInputLayout

    private lateinit var alreadyHaveAccount: TextView

    private lateinit var auth: FirebaseAuth

    lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        progressDialog = ProgressDialog(this)
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.isIndeterminate = true


        passwordEditText = findViewById(R.id.passwordEditText)
        emailEditText = findViewById(R.id.emailEditText)
        usernameEditText = findViewById(R.id.usernameEditText)
        signInButton = findViewById(R.id.signInButton)

        errorTextView = findViewById(R.id.errorTextView)

        usernameTextLayoutSignUp = findViewById(R.id.usernameTextLayoutSignUp)
        emailTextLayoutSignUp = findViewById(R.id.emailTextLayoutSignUp)
        passwordTextLayoutSignUp = findViewById(R.id.passwordTextLayoutSignUp)

        alreadyHaveAccount = findViewById(R.id.alreadyHaveAccountText)

        alreadyHaveAccount.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }

        auth = Firebase.auth

        // To handle click on signUp Button
        signInButton.setOnClickListener {

            if (usernameEditText.text.toString().isEmpty()) {
                usernameTextLayoutSignUp.error = "Username can not be empty"
            }
            else if(usernameEditText.text.toString().contains(" ")){
                usernameTextLayoutSignUp.error = "Empty spaces not allowed."
            }
            else if(usernameEditText.text.toString().length <= 2){
                usernameTextLayoutSignUp.error = "Username should be minimum 3 characters long"
            }
            else if (emailEditText.text.toString().isEmpty()) {
                emailTextLayoutSignUp.error = "Email can not be empty"
            } else if (passwordEditText.text.toString().isEmpty()) {
                passwordTextLayoutSignUp.error = "Password can not be empty"
            }
            else if (passwordEditText.text.toString().length < 8) {
                passwordTextLayoutSignUp.error = "Password should be minimum 8 characters long"
            }
            else {


                try {
                    FirebaseDatabase.getInstance().reference.child("users")
                        .addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {

                                if(snapshot.exists()){
                                    var ans = false

                                    for (item in snapshot.children) {
                                        val username = item.child("username").value
                                        if (username == usernameEditText.text.toString().toLowerCase()) {
                                            ans = true
                                            break
                                        }
                                    }
                                    if (ans) {
    //                            usernameEditText.error = "Username already exists"
                                        usernameTextLayoutSignUp.error = "Username already exists"
                                    } else {
                                        if (usernameEditText.text.isNotEmpty() && emailEditText.text.isNotEmpty() && passwordEditText.text.isNotEmpty()) {
                                            signUp()
                                        } else {
                                            Toast.makeText(
                                                applicationContext,
                                                "Enter all fields properly",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                }
                                else {
                                    signUp()
                                }

                            }

                            override fun onCancelled(error: DatabaseError) {
                                Log.i("error",error.toString())
                            }

                        })
                } catch (e: Exception) {
                }
            }

        }

    }


    // Function for adding user or signing up to firebase
    fun signUp() {
        progressDialog.show()
        progressDialog.setContentView(R.layout.progress_bar)
        progressDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        auth.createUserWithEmailAndPassword(
            emailEditText.text.toString(),
            passwordEditText.text.toString()
        ).addOnCompleteListener {
            if (it.isSuccessful) {
                errorTextView.visibility = View.GONE
                val user = Firebase.auth.currentUser!!
                user.sendEmailVerification().addOnSuccessListener {
                    progressDialog.dismiss()
                    user.reload()
                    Toast.makeText(this,"Verification Email Has Been Sent.",Toast.LENGTH_SHORT).show()
                    val intent = Intent(this,SignUpVerification::class.java)
                    intent.putExtra("username",usernameEditText.text.toString().toLowerCase())
                    intent.putExtra("displayName",usernameEditText.text.toString())
                    intent.putExtra("email",emailEditText.text.toString())
                    intent.putExtra("password",passwordEditText.text.toString())
                    startActivity(intent)
                }.addOnFailureListener {
                    try {
                        Firebase.auth.currentUser!!.delete().addOnSuccessListener {
                            Toast.makeText(this,"Failure occurred",Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                    }
                }

            }
        }.addOnFailureListener {
            progressDialog.dismiss()
            val error = it.toString().substringAfter(":")
            errorTextView.visibility = View.VISIBLE
            errorTextView.text = error
            }

    }

}