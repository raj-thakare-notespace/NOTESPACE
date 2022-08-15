package com.startup.notespace

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.startup.notespace.models.User
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging

class SignUpVerification : AppCompatActivity() {

    lateinit var button: MaterialButton
    lateinit var confirmButton: MaterialButton
    lateinit var textView: TextView

    override fun onResume() {
        super.onResume()
        try {
            Firebase.auth.currentUser!!.reload().addOnSuccessListener {
                if (Firebase.auth.currentUser!!.isEmailVerified) {
                    registerUserOnDatabase(
                        intent.getStringExtra("username").toString(),
                        intent.getStringExtra("displayName").toString(),
                        intent.getStringExtra("email").toString(),
                        intent.getStringExtra("password").toString()
                    )
                }
            }
        } catch (e: Exception) {
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            Firebase.auth.currentUser!!.reload().addOnSuccessListener {
                if(!Firebase.auth.currentUser!!.isEmailVerified){
                    Firebase.auth.currentUser!!.delete()
                }
            }
        } catch (e: Exception) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up_verification)

        val username = intent.getStringExtra("username").toString()
        val displayName = intent.getStringExtra("displayName").toString()
        val email = intent.getStringExtra("email").toString()
        val password = intent.getStringExtra("password").toString()

        button = findViewById(R.id.resendVerificationMail)
        confirmButton = findViewById(R.id.confirmVerificationMail)
        textView = findViewById(R.id.textViewVerification)

        textView.text = "Verification link has been sent to $email.\nPlease Verify.\nCheck 'spam' folder also."

        val user = Firebase.auth.currentUser!!


        try {
            confirmButton.setOnClickListener {
                user.reload().addOnSuccessListener {
                    if (user.isEmailVerified) {
                        registerUserOnDatabase(username, displayName, email, password)
                    }
                    else if(!user.isEmailVerified){
                        Toast.makeText(this, "Please verify email.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } catch (e: Exception) {
        }

        button.setOnClickListener {
            try {
                val user = Firebase.auth.currentUser!!
                user.reload()
                user.sendEmailVerification().addOnSuccessListener {
                    Toast.makeText(this, "Verification Email Has Been Sent.", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Toast.makeText(this, "Failure occurred", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
            }
        }
    }

    private fun registerUserOnDatabase(
        username: String,
        displayName: String,
        email: String,
        password: String
    ) {
        val db = Firebase.database
        val user = User()
        val auth = Firebase.auth
        user.uid = Firebase.auth.currentUser!!.uid
        user.username = username.toLowerCase()
        user.displayName = displayName
        user.email = email
        user.password = password
        user.accountPrivate = false
        db.reference.child("users").child(auth.currentUser!!.uid).setValue(user)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    db.reference.child("users").child(auth.currentUser!!.uid).child("rank")
                        .child("Helpful").setValue(0L)
                    db.reference.child("users").child(auth.currentUser!!.uid).child("rank")
                        .child("Resourceful").setValue(0L)
                    db.reference.child("users").child(auth.currentUser!!.uid).child("rank")
                        .child("Genius").setValue(0L)
                    db.reference.child("users").child(auth.currentUser!!.uid).child("rank")
                        .child("Reliable").setValue(0L)
                    db.reference.child("users").child(auth.currentUser!!.uid).child("rank")
                        .child("Problem Solver").setValue(0L)
                    Toast.makeText(this, "success", Toast.LENGTH_SHORT).show()

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
                    startActivity(Intent(this, MainActivity::class.java))
                    SignUpVerification().finish()
                }
            }
    }
}