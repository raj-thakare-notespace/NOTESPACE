package com.example.notespace

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class GroupProfileEditActivity : AppCompatActivity() {

    lateinit var editProfileUsernameET: EditText
    lateinit var editProfileDisplayNameET: EditText
    lateinit var editProfileBioEV: EditText
    lateinit var editProfileJoinCodeETGroup: EditText

    lateinit var linearProgressIndicator: LinearProgressIndicator

    lateinit var editProfileToolBar: MaterialToolbar

    var arrayList = ArrayList<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_profile_edit)

        linearProgressIndicator = findViewById(R.id.linearProgressIndicatorEditGroupProfile)

        editProfileToolBar = findViewById(R.id.editProfileToolBarGroup)


        val usernameGroup = intent.getStringExtra("usernameGroup").toString()
        val uid = intent.getStringExtra("uid").toString()
        val bioGroup = intent.getStringExtra("bioGroup")
        val displayNameGroup = intent.getStringExtra("displayNameGroup")
        val joinCodeGroup = intent.getStringExtra("joinCodeGroup")

        editProfileUsernameET = findViewById(R.id.editProfileUsernameETGroup)
        editProfileDisplayNameET = findViewById(R.id.editProfileDisplayNameETGroup)
        editProfileBioEV = findViewById(R.id.editProfileBioEVGroup)
        editProfileJoinCodeETGroup = findViewById(R.id.editProfileJoinCodeETGroup)

        editProfileUsernameET.setText(intent.getStringExtra("usernameGroup"))
        editProfileDisplayNameET.setText(intent.getStringExtra("displayNameGroup"))
        editProfileBioEV.setText(intent.getStringExtra("bioGroup"))
        editProfileJoinCodeETGroup.setText(intent.getStringExtra("joinCodeGroup"))

        editProfileToolBar.setNavigationOnClickListener {
            finish()
        }

        val db = Firebase.database
        val auth = Firebase.auth


        try {
            FirebaseDatabase.getInstance().reference.child("users")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {

                        if (snapshot.exists()) {
                            arrayList.clear()

                            for (item in snapshot.children) {
                                val usernameL = item.child("username").value
                                if (usernameL.toString() == usernameGroup) {
                                    continue
                                }
                                Log.i("bjp", usernameL.toString())
                                arrayList.add(usernameL.toString())
                            }


                        }

                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
        } catch (e: Exception) {
        }


        editProfileToolBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.saveProfileEdits -> {

                    linearProgressIndicator.visibility = View.VISIBLE

                    var username = editProfileUsernameET.text.toString()
                    var displayName = editProfileDisplayNameET.text.toString()
                    var bio = editProfileBioEV.text.toString()
                    var joinCode = editProfileJoinCodeETGroup.text.toString()

                    Log.i("eleven", arrayList.toString())


                    if (arrayList.contains(username)) {
                        editProfileUsernameET.error = "Username already exists"
                        linearProgressIndicator.visibility = View.GONE
                    }
                    else {
                        updateProfile(uid, username, displayName, joinCode, bio)
                    }

                    true
                }
                else -> true
            }
        }

    }

    private fun updateProfile(
        uid: String,
        usernameGroup: String,
        displayName: String,
        joinCode: String,
        bio: String
    ) {
        val db = Firebase.database
        val auth = Firebase.auth

        if (usernameGroup.isNotEmpty() && displayName.isNotEmpty() && joinCode.isNotEmpty()) {


            try {
                db.reference.child("users").child(uid).child("username").setValue(usernameGroup)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            linearProgressIndicator.visibility = View.GONE

                        }
                    }
                db.reference.child("users").child(uid).child("displayName").setValue(displayName)
                db.reference.child("users").child(uid).child("bio").setValue(bio)
                db.reference.child("users").child(uid).child("joinCode").setValue(joinCode)

                finish()
            } catch (e: Exception) {
            }

        }
        else {
            Toast.makeText(applicationContext, "Enter all fields.", Toast.LENGTH_SHORT).show()
        }
    }
}