package com.example.notespace

import android.Manifest
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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.notespace.adapters.ProfilePostsAdapter
import com.example.notespace.models.Post
import com.example.notespace.models.RankModel
import com.example.notespace.models.User
import com.example.notespace.usefulClasses.GetStatsInKandM
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import de.hdodenhof.circleimageview.CircleImageView

class ProfileActivity : AppCompatActivity() {


    val storage = Firebase.storage

    val database = Firebase.database

    lateinit var profilePostRV: RecyclerView
    lateinit var postAdapter: ProfilePostsAdapter
    lateinit var postArrayList: ArrayList<Post>

    lateinit var changeProfilePictureIV: ImageView

    lateinit var profilePictureIV: CircleImageView

    lateinit var editProfileButton: MaterialButton
    lateinit var documentPostsButton: MaterialButton
    lateinit var requestsButton: MaterialButton


    lateinit var displayName: TextView
    lateinit var username: TextView
    lateinit var profession: TextView
    lateinit var bio: TextView

    lateinit var noOfPosts: TextView
    lateinit var noOfFollowers: TextView
    lateinit var noOfFollowing: TextView

    private lateinit var profileToolbar: MaterialToolbar

    lateinit var followingLL: LinearLayout
    lateinit var followersLL: LinearLayout

    var isAccountPrivate = false

    lateinit var progressBar: ProgressBar

    private var topRank = "blank"
    private var mostStars: Long = 0

    lateinit var dialog: AlertDialog

    lateinit var rankImageView: ImageView


    private var arrayListOfRewards = ArrayList<RankModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        rankImageView = findViewById(R.id.rankImageViewMyProfile)


        followersLL = findViewById(R.id.followersLL)
        followingLL = findViewById(R.id.followingLL)
        documentPostsButton = findViewById(R.id.documentPostsButton)

        requestsButton = findViewById(R.id.requestsButton)

        requestsButton.setOnClickListener {
            val intent = Intent(this, RequestsActivity::class.java)
            startActivity(intent)
        }

        documentPostsButton.setOnClickListener {
            val intent = Intent(this, DocumentPostsActivity::class.java)
            intent.putExtra("uid", Firebase.auth.currentUser!!.uid)
            startActivity(intent)
        }

        followersLL.setOnClickListener {
            startActivity(Intent(this, FollowersListActivity::class.java))
        }
        followingLL.setOnClickListener {
            startActivity(Intent(this, FollowingListActivity::class.java))
        }

        progressBar = findViewById(R.id.profilePictureProgressBar)


        profileToolbar = findViewById(R.id.profileToolBar)


        profileToolbar.setNavigationOnClickListener {
            finish()
        }

        val builder = MaterialAlertDialogBuilder(this,R.style.ThemeOverlay_App_MaterialAlertDialog)
        builder.setTitle("Account Settings")
        builder.setCancelable(false)


        var view = layoutInflater.inflate(R.layout.alert_dialog_profile_settings, null)
        val closeButton = view.findViewById<MaterialButton>(R.id.closeButtonProfileSetting)
        val deleteAccountButton = view.findViewById<MaterialButton>(R.id.deleteAccountButton)
        val feedbackButton = view.findViewById<MaterialButton>(R.id.feedbackAlertDialog)
        val accountPrivateAlertDialogBtn =
            view.findViewById<MaterialButton>(R.id.accountPrivateAlertDialog)
        val signOutBtnAlertDialogBtn = view.findViewById<MaterialButton>(R.id.signOutBtnAlertDialog)
        val aboutUsButton = view.findViewById<MaterialButton>(R.id.aboutUsAlertDialog)

        aboutUsButton.setOnClickListener {
            startActivity(Intent(this,AboutUsActivity::class.java))
            dialog.dismiss()
        }

        deleteAccountButton.setOnClickListener {
            val builder = MaterialAlertDialogBuilder(this)
            builder.setTitle("Are you sure you want to Delete this account permanently?")
            builder.setCancelable(false)
                .setPositiveButton("Yes") { dialog, id ->

                    try {
                        FirebaseDatabase.getInstance().reference.child("users")
                            .child(Firebase.auth.currentUser!!.uid)
                            .removeValue().addOnSuccessListener {
                                FirebaseDatabase.getInstance().reference.child("Library")
                                    .child(Firebase.auth.currentUser!!.uid)
                                    .removeValue().addOnSuccessListener {
                                        try {
                                                Firebase.auth.currentUser!!.delete()
                                                    .addOnSuccessListener {
                                                        Toast.makeText(
                                                            this,
                                                            "Account Deleted Permanently",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                        startActivity(
                                                            Intent(
                                                                this,
                                                                SignInActivity::class.java
                                                            )
                                                        )
                                                    }


                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                    }

                            }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }


                }
                .setNegativeButton("No") { dialog, id ->
                    dialog.dismiss()
                }
            val alert = builder.create()
            alert.show()
        }

        accountPrivateAlertDialogBtn.setOnClickListener {
            if (isAccountPrivate) {
                database.reference.child("users")
                    .child(Firebase.auth.currentUser!!.uid)
                    .child("accountPrivate")
                    .setValue(false)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            accountPrivateAlertDialogBtn.text = "Account Public"
                        }
                    }
            } else {
                database.reference.child("users")
                    .child(Firebase.auth.currentUser!!.uid)
                    .child("accountPrivate")
                    .setValue(true)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            accountPrivateAlertDialogBtn.text = "Account Private"
                        }
                    }
            }
        }

        feedbackButton.setOnClickListener {
            startActivity(Intent(this,FeedbackActivity::class.java))
            dialog.dismiss()
        }

        signOutBtnAlertDialogBtn.setOnClickListener {
            try {
                FirebaseDatabase.getInstance().reference.child("users")
                    .child(Firebase.auth.currentUser!!.uid)
                    .child("token")
                    .setValue("").addOnSuccessListener {
                        Firebase.auth.signOut()
                        startActivity(Intent(this, SignInActivity::class.java))
                        dialog.dismiss()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this,"Something went wrong.",Toast.LENGTH_SHORT).show()
                    }
            } catch (e: Exception) {
            }
        }

        closeButton.setOnClickListener {
            dialog.dismiss()
        }

        builder.setView(view)
        dialog = builder.create()

        profileToolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.settingsProfile -> {
                    dialog.show()
                    true
                }
                else -> true
            }
        }

        try {
            Firebase.database.reference.child("users")
                .child(Firebase.auth.currentUser!!.uid)
                .child("rank")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {

                            for (item in snapshot.children) {
                                arrayListOfRewards.add(
                                    RankModel(
                                        item.key.toString(),
                                        item.value.toString().toLong()
                                    )
                                )
                            }
                            Log.i("thankyou", arrayListOfRewards.toString())

                            var max: Long = arrayListOfRewards[0].stars
                            var maxIndex = 0

                            (1..4).forEach { i ->
                                if (arrayListOfRewards[i].stars > max) {
                                    max = arrayListOfRewards[i].stars
                                    maxIndex = i
                                }
                            }

                            when (maxIndex) {

                                0 -> {
                                    topRank = arrayListOfRewards[0].rankName
                                    mostStars = arrayListOfRewards[0].stars
                                    true
                                }
                                1 -> {
                                    topRank = arrayListOfRewards[1].rankName
                                    mostStars = arrayListOfRewards[1].stars
                                    true
                                }
                                2 -> {
                                    topRank = arrayListOfRewards[2].rankName
                                    mostStars = arrayListOfRewards[2].stars
                                    true
                                }
                                3 -> {
                                    topRank = arrayListOfRewards[3].rankName
                                    mostStars = arrayListOfRewards[3].stars
                                    true
                                }
                                4 -> {
                                    topRank = arrayListOfRewards[4].rankName
                                    mostStars = arrayListOfRewards[4].stars
                                    true
                                }

                                else -> false
                            }

                            when (topRank) {

                                "Helpful" -> {
                                    rankImageView.setImageResource(R.drawable.ic_helpful)
                                    true
                                }
                                "Resourceful" -> {
                                    rankImageView.setImageResource(R.drawable.ic_resourceful)
                                    true
                                }
                                "Genius" -> {
                                    rankImageView.setImageResource(R.drawable.ic_genius)
                                    true
                                }
                                "Reliable" -> {
                                    rankImageView.setImageResource(R.drawable.ic_reliable)
                                    true
                                }
                                "Problem Solver" -> {
                                    rankImageView.setImageResource(R.drawable.ic_problem_solver)
                                    true
                                }

                                else -> false
                            }

                            Log.i("thankyou", topRank + " " + mostStars)

                        }


                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
        } catch (e: Exception) {
        }


        database.reference.child("users")
            .child(Firebase.auth.currentUser!!.uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val model = snapshot.getValue(User::class.java)
                        Log.i("JSR", model.toString())
                        if (model!!.accountPrivate) {
                            accountPrivateAlertDialogBtn.setText("Account Private")
                            isAccountPrivate = true
                        } else {
                            accountPrivateAlertDialogBtn.setText("Account Public")
                            isAccountPrivate = false
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })



        postArrayList = ArrayList()

        profilePostRV = findViewById(R.id.profilePostRV)

        postAdapter = ProfilePostsAdapter(this, postArrayList)

        profilePostRV.layoutManager = GridLayoutManager(this, 2)

        profilePostRV.adapter = postAdapter


        noOfPosts = findViewById(R.id.noOfPosts)
        noOfFollowers = findViewById(R.id.noOfFollowers)
        noOfFollowing = findViewById(R.id.noOfFollowing)


        changeProfilePictureIV = findViewById(R.id.changeProfilePictureIV)

        displayName = findViewById(R.id.profileDisplayName)
        username = findViewById(R.id.profileUsername)
        profession = findViewById(R.id.profileProfession)
        bio = findViewById(R.id.profileBio)

        editProfileButton = findViewById(R.id.editProfileBtn)

        editProfileButton.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java)
            intent.putExtra("username", username.text.toString())
            intent.putExtra("displayName", displayName.text.toString())
            intent.putExtra("bio", bio.text.toString())
            intent.putExtra("profession", profession.text.toString())
            startActivity(intent)
        }

        profilePictureIV = findViewById(R.id.profilePictureIV)

        changeProfilePictureIV.setOnClickListener {
            if(isPermissionGranted(this)){
                progressBar.visibility = View.VISIBLE
                profilePictureIV.visibility = View.INVISIBLE
                try {
                    val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(intent, 11)
                } catch (e: Exception) {
                }
            }
            else{
                takePermission(this)
            }

        }

        database.reference.child("users")
            .child(Firebase.auth.currentUser!!.uid)
            .child("my_posts")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        postArrayList.clear()
                        for (item in snapshot.children) {
                            val post = item.getValue(Post::class.java)
                            val postedBy = post!!.postedBy
                            if (postedBy == Firebase.auth.currentUser!!.uid) {
                                postArrayList.add(post)

                            }
                        }
                        postAdapter.notifyDataSetChanged()
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

        try {
            database.reference.child("users").child(Firebase.auth.currentUser!!.uid)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val usernameL = snapshot.child("username").value
                            val displayNameL = snapshot.child("displayName").value
                            val bioL = snapshot.child("bio").value
                            val professionL = snapshot.child("profession").value
                            val profilePictureL = snapshot.child("profilePicture").value
                            val followersCount = snapshot.child("followers").childrenCount
                            val followingCount = snapshot.child("following").childrenCount
                            val postCount = snapshot.child("my_posts").childrenCount

                            profileToolbar.title = usernameL.toString()
                            username.text = usernameL.toString()
                            displayName.text = displayNameL.toString()
                            if (bioL.toString().isNotEmpty()) {
                                bio.visibility = View.VISIBLE
                            }
                            if (professionL.toString().isNotEmpty()) {
                                profession.visibility = View.VISIBLE
                            }
                            bio.text = bioL.toString()
                            profession.text = professionL.toString()
                            Glide.with(applicationContext)
                                .load(profilePictureL.toString())
                                .placeholder(R.drawable.profile_placeholder)
                                .into(profilePictureIV)

                            var noOfPostInK = GetStatsInKandM().getStats(postCount)
                            var noOfFollowerInK = GetStatsInKandM().getStats(followersCount)
                            var noOfFollowingInK = GetStatsInKandM().getStats(followingCount)

                            noOfPosts.text = noOfPostInK
                            noOfFollowers.text = noOfFollowerInK
                            noOfFollowing.text = noOfFollowingInK
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
        } catch (e: Exception) {
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == 101){
            if(grantResults.isNotEmpty()){
                var readExternalStorage : Boolean = grantResults[0] == PackageManager.PERMISSION_GRANTED
                if(readExternalStorage){
                    Toast.makeText(this,"Read permission granted in android 10 or below",Toast.LENGTH_SHORT).show()
                }
                else{
                    takePermission(this)
                }
            }
        }

    }

    private fun isPermissionGranted(context: Context) : Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // For android 11
            return Environment.isExternalStorageManager()
        }
        else{
            // For below
            val  readExternalStorageManager = ContextCompat.checkSelfPermission(context,android.Manifest.permission.READ_EXTERNAL_STORAGE)
            return readExternalStorageManager == PackageManager.PERMISSION_GRANTED
        }
    }


    private fun takePermission(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // For android 11

            try {
                var intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.addCategory("android.intent.category.DEFAULT")
                intent.data = Uri.parse(String.format("package:%s",context.packageName))
                startActivityForResult(intent,100)
            }
            catch (e : Exception){
                var intent = Intent()
                intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                startActivityForResult(intent,100)
            }

        }
        else{
            // For below versions
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),101)

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 11) {
            if (data?.data != null) {
                val uri = data.data
                profilePictureIV.setImageURI(uri)

                try {
                    val reference = Firebase.auth.currentUser?.let {
                        storage.reference.child("profilePictures").child(Firebase.auth.currentUser!!.uid)
                            .child(it.uid)
                    }
                    if (uri != null) {
                        reference?.putFile(uri)?.addOnSuccessListener {
                            if (it.task.isSuccessful) {
                                reference.downloadUrl.addOnSuccessListener {
                                    database.reference.child("users")
                                        .child(Firebase.auth.currentUser!!.uid)
                                        .child("profilePicture").setValue(it.toString())
                                        .addOnCompleteListener {
                                            if (it.isSuccessful) {
                                                progressBar.visibility = View.GONE
                                                profilePictureIV.visibility = View.VISIBLE
                                                Toast.makeText(this, "Success", Toast.LENGTH_SHORT)
                                                    .show()
                                            }
                                        }
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                }
            }
        }

        // To manage permissions
        if(requestCode == RESULT_OK){
            if(requestCode == 100){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    // For android 11
                    if(Environment.isExternalStorageManager()){
                        Toast.makeText(this,"Permission Granted in android 11",Toast.LENGTH_SHORT).show()
                    }
                    else{
                        takePermission(this)
                    }
                }
            }
        }
    }

}