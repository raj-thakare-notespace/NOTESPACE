package com.example.notespace

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*

class FeedbackActivity : AppCompatActivity() {

    lateinit var toolbar: MaterialToolbar
    lateinit var editText: EditText
    lateinit var submitButton: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback)
        toolbar = findViewById(R.id.feedbackToolBar)
        editText = findViewById(R.id.feedbackEditText)
        submitButton = findViewById(R.id.submitButton)


        toolbar.setNavigationOnClickListener {
            finish()
        }

        submitButton.setOnClickListener {
            if(editText.text.toString().length > 500){
                Toast.makeText(this,"Maximum 500 characters long.",Toast.LENGTH_SHORT).show()
            }
            else if(editText.text.isNotEmpty())
            {
                try {
                    Firebase.database.reference.child("feedback")
                        .child(Firebase.auth.currentUser!!.uid)
                        .child(Date().time.toString())
                        .setValue(editText.text.toString()).addOnSuccessListener {
                            Toast.makeText(this,"Feedback Submitted",Toast.LENGTH_SHORT).show()
                            editText.setText("")
                            finish()
                        }
                } catch (e: Exception) {
                }
            }
            else{
                Toast.makeText(this,"Text field cannot be empty.",Toast.LENGTH_SHORT).show()
            }
        }

    }
}