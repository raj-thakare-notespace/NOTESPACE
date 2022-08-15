package com.startup.notespace

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.startup.notespace.models.AllChatModel
import com.startup.notespace.models.Group
import com.startup.notespace.models.NotificationData
import com.startup.notespace.models.PushNotification
import com.startup.notespace.usefulClasses.GetStatsInKandM
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


class GroupProfileActivity : AppCompatActivity() {

    lateinit var changeProfilePictureIV: ImageView

    lateinit var profilePictureIV: CircleImageView

    lateinit var profileButtonBack: ImageView

    lateinit var dialog: AlertDialog

    lateinit var editProfileButton: MaterialButton
    lateinit var joinGroupButton: MaterialButton
    lateinit var libraryButton: MaterialButton

    lateinit var groupMembersButton: MaterialButton
    lateinit var groupJoinCodeButton: MaterialButton

    lateinit var displayName: TextView
    lateinit var username: TextView
    lateinit var bio: TextView

    private lateinit var progressBar: ProgressBar

    val storage = Firebase.storage

    val database = Firebase.database

    var group: Group = Group()

    var TOKEN = ""

    lateinit var usernameL: String
    lateinit var displayNameL: String
    lateinit var bioL: String
    lateinit var profilePictureL: String
    lateinit var createdByL: String
    lateinit var joinCodeL: String
    lateinit var uidL: String

    lateinit var dialogJoinCode: AlertDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_profile)

        progressBar = findViewById(R.id.profilePictureProgressBarGroup)

        groupMembersButton = findViewById(R.id.groupMembersButton)
        groupJoinCodeButton = findViewById(R.id.groupJoinCodeButton)

        val usernameGroup = intent.getStringExtra("usernameGroup")
        var uid = intent.getStringExtra("uid").toString()
        Log.i("tokyo",uid+"gpprofile")

        // To delete no existing members
        try {
            FirebaseDatabase.getInstance().reference.child("users")
                .child(uid).child("members").addValueEventListener(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists()){
                            for(member in snapshot.children){
                                FirebaseDatabase.getInstance().reference.child("users").child(member.key.toString())
                                    .addListenerForSingleValueEvent(object : ValueEventListener{
                                        override fun onDataChange(snapshot: DataSnapshot) {

                                            try {
                                                if(!snapshot.exists()){
                                                    FirebaseDatabase.getInstance().reference.child("users")
                                                        .child(uid).child("members").child(member.key.toString()).removeValue()
                                                }
                                            } catch (e: Exception) {
                                            }

                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                            TODO("Not yet implemented")
                                        }

                                    })
                            }
                        }

                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
        } catch (e: Exception) {
        }

        try {
            FirebaseDatabase.getInstance().reference.child("users")
                .child(uid).child("members")
                .child(Firebase.auth.currentUser!!.uid).addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(!snapshot.exists()){
                            libraryButton.visibility = View.INVISIBLE
                        }
                        else{
                            libraryButton.visibility = View.VISIBLE
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
        } catch (e: Exception) {
        }

        try {
            FirebaseDatabase.getInstance().reference.child("users")
                .child(uid.toString())
                .child("members")
                .addValueEventListener(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists()){
                            var length = 0
                            for(item in snapshot.children){
                                length++
                            }
                            val noOfMemberInKandM = GetStatsInKandM().getStats(length.toLong())
                            groupMembersButton.text = "$noOfMemberInKandM Members"
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
        } catch (e: Exception) {
        }

//        membersLL = findViewById(R.id.membersLL)

        groupJoinCodeButton.setOnClickListener {
            dialogJoinCode.show()
        }

        val builderJoinCode = MaterialAlertDialogBuilder(this)
        builderJoinCode.setCancelable(false)
        builderJoinCode.setTitle("Join Code")

        var viewJoinCode = layoutInflater.inflate(R.layout.alert_dialog_join_code, null)
        val joinCodeTVAlertDialog = viewJoinCode.findViewById<TextView>(R.id.joiningCodeTVAlertDialog)
        val closeButton = viewJoinCode.findViewById<Button>(R.id.closeButton)
        val copyIV = viewJoinCode.findViewById<ImageView>(R.id.copyIV)
        closeButton.setOnClickListener {
            dialogJoinCode.dismiss()
        }
        copyIV.setOnClickListener {
            val clipboard: ClipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("TextView", joinCodeTVAlertDialog.text.toString())
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this,"Copied!",Toast.LENGTH_SHORT).show()
        }

        builderJoinCode.setView(viewJoinCode)
        dialogJoinCode = builderJoinCode.create()


        groupMembersButton.setOnClickListener{
            val intent = Intent(this,GroupMembersActivity::class.java)
            intent.putExtra("groupUid",uid.toString())
            startActivity(intent)
        }

        changeProfilePictureIV = findViewById(R.id.changeProfilePictureIVGroup)

        profilePictureIV = findViewById(R.id.profilePictureIVGroup)

        editProfileButton = findViewById(R.id.editProfileBtnGroup)

        profileButtonBack = findViewById(R.id.profileButtonBackGroup)

        displayName = findViewById(R.id.profileDisplayNameGroup)
        username = findViewById(R.id.profileUsernameGroup)
        bio = findViewById(R.id.profileBioGroup)

        joinGroupButton = findViewById(R.id.joinGroupButton)

        libraryButton = findViewById(R.id.libraryButtonGP)

        libraryButton.setOnClickListener {
            val intent = Intent(this, GroupLibrary::class.java)
            intent.putExtra("uid", uid)
            intent.putExtra("createdBy", group.createdBy)
            startActivity(intent)
        }

        lateinit var currentUserModel : AllChatModel

        try {
            FirebaseDatabase.getInstance().reference.child("users")
                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                .addValueEventListener(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists()){
                            currentUserModel = snapshot.getValue(AllChatModel::class.java)!!
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
        } catch (e: Exception) {
        }


        val builder = AlertDialog.Builder(this)
        builder.setTitle("Enter joining Code")

        var view = layoutInflater.inflate(R.layout.alert_dialog, null)
        val eName = view.findViewById<EditText>(R.id.folderNameET)
        val okButton = view.findViewById<Button>(R.id.okButton)
        val cancelButton = view.findViewById<Button>(R.id.cancelButton)
        okButton.setOnClickListener {
            if (eName.text.isNotEmpty()) {

                Log.i("eeee",eName.text.toString())

                val eNameText = eName.text.toString()

                try {
                    Firebase.database.reference.child("users")
                        .child(uid.toString())
                        .addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {

                                if(snapshot.exists()){
                                    var groupJoiningCode = ""

                                    for (item in snapshot.children) {
                                        if (item.key == "joinCode") {
                                            groupJoiningCode = item.value.toString()
                                            break
                                        }
                                    }

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

                                                                FirebaseDatabase.getInstance().reference.child("users").child(uid)
                                                                    .child("createdBy").addListenerForSingleValueEvent(object : ValueEventListener{
                                                                        override fun onDataChange(snapshot: DataSnapshot) {

                                                                            if(snapshot.exists()){
                                                                                val creatorId = snapshot.value.toString()

                                                                                FirebaseDatabase.getInstance().reference.child("users")
                                                                                    .child(creatorId).child("token")
                                                                                    .addListenerForSingleValueEvent(object : ValueEventListener{
                                                                                        override fun onDataChange(snapshot: DataSnapshot) {
                                                                                            if(snapshot.exists()){
                                                                                                TOKEN = snapshot.value.toString()
                                                                                                PushNotification(
                                                                                                    NotificationData(
                                                                                                        "${currentUserModel.username} Joined the group",
                                                                                                        "click here to see"
                                                                                                    ),
                                                                                                    TOKEN
                                                                                                ).also {
                                                                                                    try {
                                                                                                        sendNotification(it)
                                                                                                    } catch (e: Exception) {
                                                                                                    }
                                                                                                }
                                                                                            }

                                                                                        }

                                                                                        override fun onCancelled(error: DatabaseError) {
                                                                                            TODO("Not yet implemented")
                                                                                        }

                                                                                    })
                                                                            }



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

                                    }
                                    else {
                                        Toast.makeText(
                                            applicationContext,
                                            "Wrong code",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }



                            }

                            override fun onCancelled(error: DatabaseError) {
                                TODO("Not yet implemented")
                            }

                        })
                } catch (e: Exception) {
                }



                eName.setText("")
                dialog.dismiss()
            }
        }
        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        builder.setView(view)
        dialog = builder.create()

        try {
            FirebaseDatabase.getInstance().reference.child("users")
                .child(uid.toString())
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists()){
                            var group = snapshot.getValue(Group::class.java)
                            if (group!!.createdBy == Firebase.auth.currentUser!!.uid) {
                                joinGroupButton.visibility = View.GONE
                                groupMembersButton.visibility = View.VISIBLE
                                changeProfilePictureIV.visibility = View.VISIBLE
                                editProfileButton.visibility = View.VISIBLE
    //                        groupJoinCode.visibility = View.VISIBLE
                                groupJoinCodeButton.visibility = View.VISIBLE
                            }
                            else {
                                editProfileButton.visibility = View.GONE
                                joinGroupButton.visibility = View.VISIBLE
                                changeProfilePictureIV.visibility = View.GONE
    //                        groupJoinCode.visibility = View.GONE
                                groupJoinCodeButton.visibility = View.GONE
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
        } catch (e: Exception) {
        }


        joinGroupButton.setOnClickListener {

            dialog.show()
        }

        profileButtonBack.setOnClickListener {
            finish()
        }

        editProfileButton.setOnClickListener {
            val intent = Intent(this, GroupProfileEditActivity::class.java)
            intent.putExtra("usernameGroup", username.text.toString())
            intent.putExtra("bioGroup", bio.text.toString())
            intent.putExtra("displayNameGroup", displayName.text.toString())
//            intent.putExtra("joinCodeGroup", groupJoinCode.text.toString())
            intent.putExtra("joinCodeGroup", joinCodeTVAlertDialog.text.toString())
            intent.putExtra("uid", uid.toString())
            startActivity(intent)

        }

        try {
            FirebaseDatabase.getInstance().reference.child("users").child(uid.toString())
                .addValueEventListener(object : ValueEventListener {
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
                            joinCodeTVAlertDialog.text = joinCodeL.toString()
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
        } catch (e: Exception) {
        }

        changeProfilePictureIV.setOnClickListener {
                profilePictureIV.visibility = View.INVISIBLE
                progressBar.visibility = View.VISIBLE
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent, 11)
        }
    }


    private fun sendNotification(notification: PushNotification) =
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.api.postNotification(notification)
            } catch (e: Exception) {
                Log.e("TAG", e.toString())
            }
        }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 11) {
            if (data?.data != null) {

                var groupUid = intent.getStringExtra("uid").toString()

                val uri = data.data
                profilePictureIV.setImageURI(uri)

                val reference = Firebase.auth.currentUser?.let {
                    storage.reference.child("profilePictures").child(groupUid)
                        .child(groupUid)
                }
                if (uri != null) {

                    try {
                        reference?.putFile(uri)?.addOnSuccessListener {
                            if (it.task.isSuccessful) {
                                Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()

                                reference.downloadUrl.addOnSuccessListener {
                                    database.reference.child("users")
                                        .child(intent.getStringExtra("uid").toString())
                                        .child("profilePicture").setValue(it.toString())
                                }
                                reference.downloadUrl.addOnSuccessListener {
                                    database.reference.child("users")
                                        .child(Firebase.auth.currentUser!!.uid)
                                        .child("my_groups")
                                        .child(intent.getStringExtra("uid").toString())
                                        .child("profilePicture").setValue(it.toString()).addOnSuccessListener {
                                            progressBar.visibility = View.GONE
                                            profilePictureIV.visibility = View.VISIBLE
                                            Toast.makeText(this, "Success", Toast.LENGTH_SHORT)
                                                .show()
                                        }
                                }
                            }
                        }
                    } catch (e: Exception) {
                    }

                }
            }
        }

    }

}