package com.example.notespace

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.notespace.models.AllChatModel
import com.example.notespace.models.Group
import com.example.notespace.models.NotificationData
import com.example.notespace.models.PushNotification
import com.example.notespace.usefulClasses.GetStatsInKandM
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GroupProfileOther : AppCompatActivity() {


    lateinit var profilePictureIV: CircleImageView

    lateinit var profileButtonBack: ImageView

    lateinit var dialog: AlertDialog

    lateinit var joinGroupButton: MaterialButton
    lateinit var libraryButton: MaterialButton

    lateinit var groupMembersButton: MaterialButton

    lateinit var displayName: TextView
    lateinit var username: TextView
    lateinit var bio: TextView

//    lateinit var groupJoinCode: TextView

    val storage = Firebase.storage

    val database = Firebase.database

    var group: Group = Group()

    var TOKEN = ""

//    lateinit var membersLL : LinearLayout

    lateinit var usernameL: String
    lateinit var displayNameL: String
    lateinit var bioL: String
    lateinit var profilePictureL: String
    lateinit var createdByL: String
    lateinit var joinCodeL: String
    lateinit var uidL: String

//    lateinit var noOfMembers : TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_profile_other)

        groupMembersButton = findViewById(R.id.groupMembersButtonOther)
        joinGroupButton = findViewById(R.id.joinGroupButtonOther)

        var uid = intent.getStringExtra("uid").toString()
        Log.i("tokyo", uid + "gpprofile")

        FirebaseDatabase.getInstance().reference.child("users")
            .child(uid).child("members")
            .child(Firebase.auth.currentUser!!.uid).addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (!snapshot.exists()) {
                        libraryButton.visibility = View.INVISIBLE
                        joinGroupButton.text = "Join Group"
                    } else {
                        libraryButton.visibility = View.VISIBLE
                        joinGroupButton.text = "Joined"
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })


//        noOfMembers = findViewById(R.id.noOfMembers)

        FirebaseDatabase.getInstance().reference.child("users")
            .child(uid.toString())
            .child("members")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var length = 0
                    for (item in snapshot.children) {
                        length++
                    }
                    val noOfMemberInKandM = GetStatsInKandM().getStats(length.toLong())
                    groupMembersButton.text = "$noOfMemberInKandM Members"
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

//        membersLL = findViewById(R.id.membersLL)


        groupMembersButton.setOnClickListener {
            val intent = Intent(this, GroupMembersActivity::class.java)
            intent.putExtra("groupUid", uid.toString())
            startActivity(intent)
        }


        profilePictureIV = findViewById(R.id.profilePictureIVGroupOther)


        profileButtonBack = findViewById(R.id.profileButtonBackGroupOther)

        displayName = findViewById(R.id.profileDisplayNameGroupOther)
        username = findViewById(R.id.profileUsernameGroupOther)
        bio = findViewById(R.id.profileBioGroupOther)
//        groupJoinCode = findViewById(R.id.profileGroupJoinCode)

        joinGroupButton = findViewById(R.id.joinGroupButtonOther)

        libraryButton = findViewById(R.id.libraryButtonGPOther)

        libraryButton.setOnClickListener {
            val intent = Intent(this, GroupLibrary::class.java)
            intent.putExtra("uid", uid)
            intent.putExtra("createdBy", group.createdBy)
            startActivity(intent)
        }

        lateinit var currentUserModel: AllChatModel

        FirebaseDatabase.getInstance().reference.child("users")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    currentUserModel = snapshot.getValue(AllChatModel::class.java)!!
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })


        val builder = MaterialAlertDialogBuilder(this)
        builder.setTitle("Enter joining Code")

        var view = layoutInflater.inflate(R.layout.alert_dialog_joining_code, null)
        val eName = view.findViewById<EditText>(R.id.folderNameET)
        val okButton = view.findViewById<Button>(R.id.okButton)
        val cancelButton = view.findViewById<Button>(R.id.cancelButton)
        okButton.setOnClickListener {
            if (eName.text.isNotEmpty()) {

                Log.i("eeee", eName.text.toString())

                val eNameText = eName.text.toString()

                Firebase.database.reference.child("users")
                    .child(uid.toString())
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            var groupJoiningCode = ""
                            Log.i("eeeed", eName.text.toString())
                            for (item in snapshot.children) {
                                if (item.key == "joinCode") {
                                    groupJoiningCode = item.value.toString()
                                    break
                                }
                            }

                            val j = eName.text.toString() == groupJoiningCode
                            Log.i(
                                "jcode",
                                j.toString() + " " + eName.text.toString() + " & " + groupJoiningCode
                            )


                            // to check if the code is correct
                            if (eNameText == groupJoiningCode) {


                                FirebaseDatabase.getInstance().reference.child("users")
                                    .child(Firebase.auth.currentUser!!.uid)
                                    .child("my_groups")
                                    .child(uid)
                                    .setValue(group).addOnCompleteListener {
                                        if (it.isSuccessful) {
                                            FirebaseDatabase.getInstance().reference.child("users")
                                                .child(uid)
                                                .child("members")
                                                .child(Firebase.auth.currentUser!!.uid)
                                                .setValue(currentUserModel)
                                                .addOnCompleteListener {
                                                    if (it.isSuccessful) {
                                                        joinGroupButton.text = "Joined"

                                                        FirebaseDatabase.getInstance().reference.child(
                                                            "users"
                                                        ).child(uid)
                                                            .child("createdBy")
                                                            .addListenerForSingleValueEvent(object :
                                                                ValueEventListener {
                                                                override fun onDataChange(snapshot: DataSnapshot) {
                                                                    val creatorId =
                                                                        snapshot.value.toString()

                                                                    FirebaseDatabase.getInstance().reference.child(
                                                                        "users"
                                                                    )
                                                                        .child(creatorId)
                                                                        .child("token")
                                                                        .addListenerForSingleValueEvent(
                                                                            object :
                                                                                ValueEventListener {
                                                                                override fun onDataChange(
                                                                                    snapshot: DataSnapshot
                                                                                ) {
                                                                                    TOKEN =
                                                                                        snapshot.value.toString()
                                                                                    PushNotification(
                                                                                        NotificationData(
                                                                                            "${currentUserModel.username} Joined the group",
                                                                                            "click here to see"
                                                                                        ),
                                                                                        TOKEN
                                                                                    ).also {
                                                                                        try {
                                                                                            sendNotification(
                                                                                                it
                                                                                            )
                                                                                        } catch (e: Exception) {
                                                                                        }
                                                                                    }
                                                                                }

                                                                                override fun onCancelled(
                                                                                    error: DatabaseError
                                                                                ) {
                                                                                    TODO("Not yet implemented")
                                                                                }

                                                                            })

                                                                }

                                                                override fun onCancelled(error: DatabaseError) {
                                                                    TODO("Not yet implemented")
                                                                }

                                                            })

                                                        Toast.makeText(
                                                            applicationContext,
                                                            "Joined group",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }
                                                }
                                        }
                                    }

                            } else {
                                Toast.makeText(
                                    applicationContext,
                                    "Wrong code",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                    })



                eName.setText("")
                dialog.dismiss()
            }
        }
        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        builder.setView(view)
        dialog = builder.create()

        FirebaseDatabase.getInstance().reference.child("users")
            .child(uid.toString())
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var group = snapshot.getValue(Group::class.java)
                    if (group!!.createdBy == Firebase.auth.currentUser!!.uid) {
                        joinGroupButton.visibility = View.GONE
                        groupMembersButton.visibility = View.VISIBLE
                    } else {
                        joinGroupButton.visibility = View.VISIBLE
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })


        joinGroupButton.setOnClickListener {

            dialog.show()
        }

        profileButtonBack.setOnClickListener {
            finish()
        }


        FirebaseDatabase.getInstance().reference.child("users").child(uid.toString())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        usernameL = snapshot.child("username").value.toString()
                        displayNameL = snapshot.child("displayName").value.toString()
                        bioL = snapshot.child("bio").value.toString()
                        profilePictureL = snapshot.child("profilePicture").value.toString()
                        createdByL = snapshot.child("createdBy").value.toString()
                        joinCodeL = snapshot.child("joinCode").value.toString()
                        uidL = snapshot.child("uid").value.toString()

                        group.bio = bioL.toString()
                        group.displayName = displayNameL.toString()
                        group.username = usernameL.toString()
                        group.profilePicture = profilePictureL.toString()
                        group.createdBy = createdByL.toString()
                        group.joinCode = joinCodeL.toString()
                        group.uid = uidL.toString()


                        username.text = usernameL.toString()
                        displayName.text = displayNameL.toString()
                        bio.text = bioL.toString()
//                        groupJoinCode.text = joinCodeL.toString()
                        Glide.with(applicationContext)
                            .load(profilePictureL.toString())
                            .placeholder(R.drawable.profile_placeholder)
                            .into(profilePictureIV)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })


    }

    private fun sendNotification(notification: PushNotification) =
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.api.postNotification(notification)
//            if(response.isSuccessful) {
//                Log.d(TAG, "Response: ${Gson().toJson(response)}")
//            } else {
//                Log.e(TAG, response.errorBody().toString())
//            }
            } catch (e: Exception) {
                Log.e("TAG", e.toString())
            }
        }

}