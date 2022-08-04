package com.startup.notespace

import android.os.Bundle
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

class EditProfileActivity : AppCompatActivity() {

    lateinit var editProfileUsernameET: EditText
    lateinit var editProfileDisplayNameET: EditText
    lateinit var editProfileProfessionET: EditText
    lateinit var editProfileBioEV: EditText

    lateinit var linearProgressIndicator: LinearProgressIndicator

    lateinit var editProfileToolBar: MaterialToolbar

    var arrayList = ArrayList<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        linearProgressIndicator = findViewById(R.id.linearProgressIndicator)

        editProfileUsernameET = findViewById(R.id.editProfileUsernameET)
        editProfileDisplayNameET = findViewById(R.id.editProfileDisplayNameET)
        editProfileProfessionET = findViewById(R.id.editProfileProfessionET)
        editProfileBioEV = findViewById(R.id.editProfileBioEV)

        var currentUsername = intent.getStringExtra("username").toString()

        editProfileUsernameET.setText(intent.getStringExtra("username").toString())
        editProfileDisplayNameET.setText(intent.getStringExtra("displayName").toString())
        editProfileProfessionET.setText(intent.getStringExtra("profession").toString())
        editProfileBioEV.setText(intent.getStringExtra("bio").toString())

        editProfileToolBar = findViewById(R.id.editProfileToolBar)

        editProfileToolBar.setNavigationOnClickListener {
            finish()
        }

        try {
            FirebaseDatabase.getInstance().reference.child("users")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists()){
                            arrayList.clear()
                            for (item in snapshot.children) {
                                val usernameL = item.child("username").value
                                if(usernameL.toString() == currentUsername) {
                                    continue
                                }
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
                    var profession = editProfileProfessionET.text.toString()
                    var bio = editProfileBioEV.text.toString()



                    if(username.isEmpty()){
                        editProfileUsernameET.error = "Username cannot be empty."
                        linearProgressIndicator.visibility = View.GONE
                        linearProgressIndicator.visibility = View.GONE
                    }
                    else if(username.contains(" ")){
                        editProfileUsernameET.error = "Cannot contain spaces."
                        linearProgressIndicator.visibility = View.GONE
                    }
                    else if (arrayList.contains(username)) {
                        editProfileUsernameET.error = "Username already exists"
                        linearProgressIndicator.visibility = View.GONE
                    }
                    else {
                        try {
                            updateProfile(username,displayName,profession,bio)
                        } catch (e: Exception) {
                        }
                    }




                    true
                }
                else -> true
            }
        }


    }

    private fun updateProfile(username : String, displayName : String, profession : String, bio : String){

        val db = Firebase.database
        val auth = Firebase.auth
        if(username.isNotEmpty() && displayName.isNotEmpty()){

            try {
                db.reference.child("users").child(auth.currentUser!!.uid).child("bio").setValue(bio)
                db.reference.child("users").child(auth.currentUser!!.uid).child("username").setValue(username).addOnCompleteListener {
                    if(it.isSuccessful){
                        linearProgressIndicator.visibility = View.GONE
                        finish()
                    }
                    else{
                        Toast.makeText(this,"Something went wrong.",Toast.LENGTH_SHORT).show()
                    }
                }
                db.reference.child("users").child(auth.currentUser!!.uid).child("profession").setValue(profession)
                db.reference.child("users").child(auth.currentUser!!.uid).child("displayName").setValue(displayName)
            } catch (e: Exception) {
            }

        }
    }

}