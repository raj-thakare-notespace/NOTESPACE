package com.startup.notespace

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.startup.notespace.adapters.ProfilePostAdapterOthers
import com.startup.notespace.models.*
import com.startup.notespace.usefulClasses.GetStatsInKandM
import com.google.android.material.appbar.MaterialToolbar
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
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class OtherUsersProfileActivity : AppCompatActivity() {


    lateinit var profilePictureIV: CircleImageView

    lateinit var displayName: TextView
    lateinit var username: TextView
    lateinit var profession: TextView
    lateinit var bio: TextView

    var isFollowing = false

    var isBlocked = false

    var TOKEN = ""

    lateinit var profilePostRV: RecyclerView
    lateinit var postAdapter: ProfilePostAdapterOthers
    lateinit var postArrayList: ArrayList<String>

    lateinit var followingLL: LinearLayout
    lateinit var followersLL: LinearLayout


    lateinit var messageButton: Button

    lateinit var documentPostButton: Button

    lateinit var followButton: MaterialButton

    lateinit var libraryButton: Button
    lateinit var notesButton: Button

    lateinit var noOfPosts: TextView
    lateinit var noOfFollowers: TextView
    lateinit var noOfFollowing: TextView

    lateinit var rankImageView: ImageView

    lateinit var toolbar: MaterialToolbar

    var isAccountPrivate = false

    lateinit var dialog: AlertDialog

    var rankName = ""
    var stars: Long = 1

    private var starsHelpful: Long = 0
    private var starsResourceful: Long = 0
    private var starsGenius: Long = 0
    private var starsReliable: Long = 0
    private var starsProblemSolver: Long = 0


    lateinit var requestModel: RequestModel

    var followersArrayList = ArrayList<String>()

    private var topRank = "Helpful"
    private var mostStars: Long = 0

    private var arrayListOfRewards = ArrayList<RankModel>()
    private var arrayListUidFeedbackList = ArrayList<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_other_users_profile)

        rankImageView = findViewById(R.id.rankImageView)


        documentPostButton = findViewById(R.id.documentPostsButtonOther)

        followersLL = findViewById(R.id.followersLLOther)
        followingLL = findViewById(R.id.followingLLOther)
        notesButton = findViewById(R.id.notesButton)


        var userId = intent.getStringExtra("uid")
        var uName = intent.getStringExtra("username")
        var profileImg = intent.getStringExtra("profilePicture")
        var usernameOther = ""

        try {
            Firebase.database.reference.child("users")
                .child(userId.toString()).child("feedbackList")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists()){
                            arrayListUidFeedbackList.clear()
                            for (item in snapshot.children) {
                                arrayListUidFeedbackList.add(item.key.toString())
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
            // To get the no of stars for each rank
            Firebase.database.reference.child("users")
                .child(userId.toString())
                .child("rank")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {

                        if(snapshot.exists()){
                            arrayListOfRewards.clear()
                            for (item in snapshot.children) {

                                arrayListOfRewards.add(
                                    RankModel(
                                        item.key.toString(),
                                        item.value.toString().toLong()
                                    )
                                )

                                if (item.key.toString() == "Helpful") {
                                    starsHelpful = item.value.toString().toLong()
                                }
                                if (item.key.toString() == "Resourceful") {
                                    starsResourceful = item.value.toString().toLong()
                                }
                                if (item.key.toString() == "Genius") {
                                    starsGenius = item.value.toString().toLong()
                                }
                                if (item.key.toString() == "Reliable") {
                                    starsReliable = item.value.toString().toLong()
                                }
                                if (item.key.toString() == "Problem Solver") {
                                    starsProblemSolver = item.value.toString().toLong()
                                }
                            }

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

                            if(max == 0L){
                                topRank = arrayListOfRewards[1].rankName
                                mostStars = arrayListOfRewards[1].stars
                            }

                            when(topRank){

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

                         }

                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
        } catch (e: Exception) {
        }




        documentPostButton.setOnClickListener {
            val intent = Intent(this, DocumentPostsActivityOther::class.java)
            intent.putExtra("uid", userId)
            startActivity(intent)
        }

        followersLL.setOnClickListener {
            val intent = Intent(this, FollowersListActivityOther::class.java)
            intent.putExtra("uid", userId.toString())
            startActivity(intent)
        }

        notesButton.setOnClickListener {
            val intent = Intent(this, NotesActivityOther::class.java)
            intent.putExtra("uid", userId)
            startActivity(intent)
        }

        followingLL.setOnClickListener {
            val intent = Intent(this, FollowingListActivityOther::class.java)
            intent.putExtra("uid", userId.toString())
            startActivity(intent)
        }

        toolbar = findViewById(R.id.otherProfileToolBar)

        toolbar.setNavigationOnClickListener {
            finish()
        }

        try {// To check if other user has blocked the current user or not
            FirebaseDatabase.getInstance().reference.child("users")
                .child(userId.toString())
                .child("blocked_users")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists()){
                            for (item in snapshot.children) {
                                if (item.key == Firebase.auth.currentUser!!.uid) {
                                    messageButton.setOnClickListener {
                                        Toast.makeText(
                                            applicationContext,
                                            "You can not message this user",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    break
                                }
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
        } catch (e: Exception) {
        }


        try {// To check if current user has blocked the other use or not
            FirebaseDatabase.getInstance().reference.child("users")
                .child(Firebase.auth.currentUser!!.uid)
                .child("blocked_users")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists()){
                            for (item in snapshot.children) {
                                if (item.key == userId) {
                                    isBlocked = true
                                    break
                                }
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
        } catch (e: Exception) {
        }

        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {

                R.id.block -> {

                    if (isBlocked) {
                        FirebaseDatabase.getInstance().reference.child("users")
                            .child(Firebase.auth.currentUser!!.uid)
                            .child("blocked_users")
                            .child(userId.toString())
                            .removeValue()
                            .addOnCompleteListener {
                                if (it.isSuccessful) {
                                    isBlocked = false
                                    Toast.makeText(this, "User Unblocked", Toast.LENGTH_SHORT)
                                        .show()

                                }
                            }
                    } else {
                        FirebaseDatabase.getInstance().reference.child("users")
                            .child(Firebase.auth.currentUser!!.uid)
                            .child("blocked_users")
                            .child(userId.toString())
                            .setValue("true")
                            .addOnCompleteListener {
                                if (it.isSuccessful) {
                                    isBlocked = true
                                    Toast.makeText(this, "User blocked", Toast.LENGTH_SHORT).show()
                                }
                            }
                    }

                    true
                }

                else -> false
            }
        }

        noOfPosts = findViewById(R.id.noOfPostsOther)
        noOfFollowers = findViewById(R.id.noOfFollowersOther)
        noOfFollowing = findViewById(R.id.noOfFollowingOther)

        libraryButton = findViewById(R.id.libraryButton)

        displayName = findViewById(R.id.otherUsersDisplayName)
        username = findViewById(R.id.otherUsersUsername)
        profession = findViewById(R.id.otherUsersProfession)
        bio = findViewById(R.id.otherUsersBio)

        profilePictureIV = findViewById(R.id.otherUsersProfilePicture)

        messageButton = findViewById(R.id.message)
        followButton = findViewById(R.id.followButton)

        postArrayList = ArrayList()

        profilePostRV = findViewById(R.id.profilePostRVOther)

        postAdapter = ProfilePostAdapterOthers(this, postArrayList)

        profilePostRV.layoutManager = GridLayoutManager(this, 2)

        profilePostRV.adapter = postAdapter


        val builder = MaterialAlertDialogBuilder(this)
        builder.setTitle("Submit Rank")
        builder.setCancelable(false)

        var view = layoutInflater.inflate(R.layout.alert_dialog_rank, null)
        val submitButton = view.findViewById<MaterialButton>(R.id.submitRatingButton)
        val cancelButton = view.findViewById<MaterialButton>(R.id.cancelButtonRank)
        val helpfulRL = view.findViewById<RelativeLayout>(R.id.helpfulRL)
        val resourcefulRL = view.findViewById<RelativeLayout>(R.id.resourcefulRL)
        val geniusRL = view.findViewById<RelativeLayout>(R.id.geniusRL)
        val reliableRL = view.findViewById<RelativeLayout>(R.id.reliableRL)
        val problemSolverRL = view.findViewById<RelativeLayout>(R.id.problemSolverRL)
        val star1 = view.findViewById<ImageView>(R.id.star1)
        val star2 = view.findViewById<ImageView>(R.id.star2)
        val star3 = view.findViewById<ImageView>(R.id.star3)
        val star4 = view.findViewById<ImageView>(R.id.star4)
        val star5 = view.findViewById<ImageView>(R.id.star5)
        val helpfulIV = view.findViewById<ImageView>(R.id.helpfulIV)
        val resourcefulIV = view.findViewById<ImageView>(R.id.resourcefulIV)
        val geniusIV = view.findViewById<ImageView>(R.id.geniusIV)
        val reliableIV = view.findViewById<ImageView>(R.id.reliableIV)
        val problemSolverIV = view.findViewById<ImageView>(R.id.problemSolverIV)


        helpfulRL.setOnClickListener {
            rankName = "Helpful"
//            helpfulRL.setBackgroundColor(android.graphics.Color.parseColor("#FF9800"))
//            resourcefulRL.setBackgroundColor(android.graphics.Color.parseColor("#FFFFFF"))
//            geniusRL.setBackgroundColor(android.graphics.Color.parseColor("#FFFFFF"))
//            reliableRL.setBackgroundColor(android.graphics.Color.parseColor("#FFFFFF"))
//            problemSolverRL.setBackgroundColor(android.graphics.Color.parseColor("#FFFFFF"))

            helpfulIV.setImageResource(R.drawable.ic_helpful_stroke)
            resourcefulIV.setImageResource(R.drawable.ic_resourceful)
            geniusIV.setImageResource(R.drawable.ic_genius)
            reliableIV.setImageResource(R.drawable.ic_reliable)
            problemSolverIV.setImageResource(R.drawable.ic_problem_solver)
        }
        resourcefulRL.setOnClickListener {
            rankName = "Resourceful"
//            helpfulRL.setBackgroundColor(android.graphics.Color.parseColor("#FFFFFF"))
//            resourcefulRL.setBackgroundColor(android.graphics.Color.parseColor("#FF9800"))
//            geniusRL.setBackgroundColor(android.graphics.Color.parseColor("#FFFFFF"))
//            reliableRL.setBackgroundColor(android.graphics.Color.parseColor("#FFFFFF"))
//            problemSolverRL.setBackgroundColor(android.graphics.Color.parseColor("#FFFFFF"))

            helpfulIV.setImageResource(R.drawable.ic_helpful)
            resourcefulIV.setImageResource(R.drawable.ic_resourceful_stroke)
            geniusIV.setImageResource(R.drawable.ic_genius)
            reliableIV.setImageResource(R.drawable.ic_reliable)
            problemSolverIV.setImageResource(R.drawable.ic_problem_solver)
        }
        geniusRL.setOnClickListener {
            rankName = "Genius"
//            helpfulRL.setBackgroundColor(android.graphics.Color.parseColor("#FFFFFF"))
//            resourcefulRL.setBackgroundColor(android.graphics.Color.parseColor("#FFFFFF"))
//            geniusRL.setBackgroundColor(android.graphics.Color.parseColor("#FF9800"))
//            reliableRL.setBackgroundColor(android.graphics.Color.parseColor("#FFFFFF"))
//            problemSolverRL.setBackgroundColor(android.graphics.Color.parseColor("#FFFFFF"))

            helpfulIV.setImageResource(R.drawable.ic_helpful)
            resourcefulIV.setImageResource(R.drawable.ic_resourceful)
            geniusIV.setImageResource(R.drawable.ic_genius_stroke)
            reliableIV.setImageResource(R.drawable.ic_reliable)
            problemSolverIV.setImageResource(R.drawable.ic_problem_solver)
        }
        reliableRL.setOnClickListener {
            rankName = "Reliable"


            helpfulIV.setImageResource(R.drawable.ic_helpful)
            resourcefulIV.setImageResource(R.drawable.ic_resourceful)
            geniusIV.setImageResource(R.drawable.ic_genius)
            reliableIV.setImageResource(R.drawable.ic_reliable_stroke)
            problemSolverIV.setImageResource(R.drawable.ic_problem_solver)
        }
        problemSolverRL.setOnClickListener {
            rankName = "Problem Solver"

            helpfulIV.setImageResource(R.drawable.ic_helpful)
            resourcefulIV.setImageResource(R.drawable.ic_resourceful)
            geniusIV.setImageResource(R.drawable.ic_genius)
            reliableIV.setImageResource(R.drawable.ic_reliable)
            problemSolverIV.setImageResource(R.drawable.ic_problem_solver_stroke)
        }

        val uri = "@drawable/ic_filled_star" // where myresource (without the extension) is the file
        val imageResource = resources.getIdentifier(uri, null, packageName)
        val res = resources.getDrawable(imageResource)

        val uri2 = "@drawable/ic_blank_star" // where myresource (without the extension) is the file
        val imageResource2 = resources.getIdentifier(uri2, null, packageName)
        val res2 = resources.getDrawable(imageResource2)

        star1.setOnClickListener {
            stars = 1
            star1.setImageDrawable(res)
            star2.setImageDrawable(res2)
            star3.setImageDrawable(res2)
            star4.setImageDrawable(res2)
            star5.setImageDrawable(res2)
        }
        star2.setOnClickListener {
            stars = 2
            star1.setImageDrawable(res)
            star2.setImageDrawable(res)
            star3.setImageDrawable(res2)
            star4.setImageDrawable(res2)
            star5.setImageDrawable(res2)
        }
        star3.setOnClickListener {
            stars = 3
            star1.setImageDrawable(res)
            star2.setImageDrawable(res)
            star3.setImageDrawable(res)
            star4.setImageDrawable(res2)
            star5.setImageDrawable(res2)
        }
        star4.setOnClickListener {
            stars = 4
            star1.setImageDrawable(res)
            star2.setImageDrawable(res)
            star3.setImageDrawable(res)
            star4.setImageDrawable(res)
            star5.setImageDrawable(res2)
        }
        star5.setOnClickListener {
            stars = 5
            star1.setImageDrawable(res)
            star2.setImageDrawable(res)
            star3.setImageDrawable(res)
            star4.setImageDrawable(res)
            star5.setImageDrawable(res)
        }

        rankImageView.setOnClickListener {
            dialog.show()
        }



        submitButton.setOnClickListener {
            if (rankName.isEmpty()) {
                Toast.makeText(this, "Please select attributes", Toast.LENGTH_SHORT).show()
            }
            else {

                try {
                    if (arrayListUidFeedbackList.contains(Firebase.auth.currentUser!!.uid)) {
                        var tempRank = ""
                        var tempStar: Long = 0
                        try {
                            Firebase.database.reference.child("users").child(userId.toString())
                                .child("feedbackList").child(Firebase.auth.currentUser!!.uid)
                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {

                                        if(snapshot.exists()){

                                            tempRank = snapshot.child("rankName").value.toString()
                                            tempStar = snapshot.child("stars").value.toString().toLong()


                                            var noOfStars: Long = 0

                                            try {
                                                FirebaseDatabase.getInstance().reference.child("users")
                                                    .child(userId.toString())
                                                    .child("rank")
                                                    .child(tempRank)
                                                    .addListenerForSingleValueEvent(object : ValueEventListener {
                                                        override fun onDataChange(snapshot: DataSnapshot) {
                                                            if(snapshot.exists()){
                                                                noOfStars = snapshot.value.toString().toLong()
                                                                FirebaseDatabase.getInstance().reference.child("users")
                                                                    .child(userId.toString())
                                                                    .child("rank")
                                                                    .child(tempRank)
                                                                    .setValue(noOfStars - tempStar).addOnSuccessListener {
                                                                        noOfStars -= tempStar
                                                                        when (rankName) {
                                                                            "Helpful" -> {

                                                                                try {
                                                                                    if (tempRank == "Helpful") {
                                                                                        Firebase.database.reference.child("users")
                                                                                            .child(userId.toString())
                                                                                            .child("rank")
                                                                                            .child("Helpful")
                                                                                            .setValue(noOfStars + stars)
                                                                                            .addOnCompleteListener {
                                                                                                if (it.isSuccessful) {
                                                                                                    Firebase.database.reference.child(
                                                                                                        "users"
                                                                                                    )
                                                                                                        .child(userId.toString())
                                                                                                        .child("feedbackList")
                                                                                                        .child(Firebase.auth.currentUser!!.uid)
                                                                                                        .setValue(
                                                                                                            FeedbackModel(
                                                                                                                Firebase.auth.currentUser!!.uid,
                                                                                                                "Helpful",
                                                                                                                stars
                                                                                                            )
                                                                                                        )
                                                                                                }
                                                                                            }
                                                                                    }
                                                                                    else {
                                                                                        Firebase.database.reference.child("users")
                                                                                            .child(userId.toString())
                                                                                            .child("rank")
                                                                                            .child("Helpful")
                                                                                            .setValue(starsHelpful + stars)
                                                                                            .addOnCompleteListener {
                                                                                                if (it.isSuccessful) {
                                                                                                    Firebase.database.reference.child(
                                                                                                        "users"
                                                                                                    )
                                                                                                        .child(userId.toString())
                                                                                                        .child("feedbackList")
                                                                                                        .child(Firebase.auth.currentUser!!.uid)
                                                                                                        .setValue(
                                                                                                            FeedbackModel(
                                                                                                                Firebase.auth.currentUser!!.uid,
                                                                                                                "Helpful",
                                                                                                                stars
                                                                                                            )
                                                                                                        )
                                                                                                }
                                                                                            }
                                                                                    }
                                                                                } catch (e: Exception) {
                                                                                }

                                                                                true
                                                                            }
                                                                            "Resourceful" -> {
                                                                                try {
                                                                                    if (tempRank == "Resourceful") {
                                                                                        Firebase.database.reference.child("users")
                                                                                            .child(userId.toString())
                                                                                            .child("rank")
                                                                                            .child("Resourceful")
                                                                                            .setValue(noOfStars + stars)
                                                                                            .addOnCompleteListener {
                                                                                                if (it.isSuccessful) {
                                                                                                    Firebase.database.reference.child(
                                                                                                        "users"
                                                                                                    )
                                                                                                        .child(userId.toString())
                                                                                                        .child("feedbackList")
                                                                                                        .child(Firebase.auth.currentUser!!.uid)
                                                                                                        .setValue(
                                                                                                            FeedbackModel(
                                                                                                                Firebase.auth.currentUser!!.uid,
                                                                                                                "Resourceful",
                                                                                                                stars
                                                                                                            )
                                                                                                        )
                                                                                                }
                                                                                            }
                                                                                    }
                                                                                    else {
                                                                                        Firebase.database.reference.child("users")
                                                                                            .child(userId.toString())
                                                                                            .child("rank")
                                                                                            .child("Resourceful")
                                                                                            .setValue(starsResourceful + stars)
                                                                                            .addOnCompleteListener {
                                                                                                if (it.isSuccessful) {
                                                                                                    Firebase.database.reference.child(
                                                                                                        "users"
                                                                                                    )
                                                                                                        .child(userId.toString())
                                                                                                        .child("feedbackList")
                                                                                                        .child(Firebase.auth.currentUser!!.uid)
                                                                                                        .setValue(
                                                                                                            FeedbackModel(
                                                                                                                Firebase.auth.currentUser!!.uid,
                                                                                                                "Resourceful",
                                                                                                                stars
                                                                                                            )
                                                                                                        )
                                                                                                }
                                                                                            }
                                                                                    }
                                                                                } catch (e: Exception) {
                                                                                }

                                                                                true
                                                                            }
                                                                            "Genius" -> {
                                                                                try {
                                                                                    if (tempRank == "Genius") {
                                                                                        Firebase.database.reference.child("users")
                                                                                            .child(userId.toString())
                                                                                            .child("rank")
                                                                                            .child("Genius")
                                                                                            .setValue(noOfStars + stars)
                                                                                            .addOnCompleteListener {
                                                                                                if (it.isSuccessful) {
                                                                                                    Firebase.database.reference.child(
                                                                                                        "users"
                                                                                                    )
                                                                                                        .child(userId.toString())
                                                                                                        .child("feedbackList")
                                                                                                        .child(Firebase.auth.currentUser!!.uid)
                                                                                                        .setValue(
                                                                                                            FeedbackModel(
                                                                                                                Firebase.auth.currentUser!!.uid,
                                                                                                                "Genius",
                                                                                                                stars
                                                                                                            )
                                                                                                        )
                                                                                                }
                                                                                            }
                                                                                    }
                                                                                    else {
                                                                                        Firebase.database.reference.child("users")
                                                                                            .child(userId.toString())
                                                                                            .child("rank")
                                                                                            .child("Genius")
                                                                                            .setValue(starsGenius + stars)
                                                                                            .addOnCompleteListener {
                                                                                                if (it.isSuccessful) {
                                                                                                    Firebase.database.reference.child(
                                                                                                        "users"
                                                                                                    )
                                                                                                        .child(userId.toString())
                                                                                                        .child("feedbackList")
                                                                                                        .child(Firebase.auth.currentUser!!.uid)
                                                                                                        .setValue(
                                                                                                            FeedbackModel(
                                                                                                                Firebase.auth.currentUser!!.uid,
                                                                                                                "Genius",
                                                                                                                stars
                                                                                                            )
                                                                                                        )
                                                                                                }
                                                                                            }
                                                                                    }
                                                                                } catch (e: Exception) {
                                                                                }

                                                                                true
                                                                            }
                                                                            "Reliable" -> {
                                                                                try {
                                                                                    if (tempRank == "Reliable") {
                                                                                        Firebase.database.reference.child("users")
                                                                                            .child(userId.toString())
                                                                                            .child("rank")
                                                                                            .child("Reliable")
                                                                                            .setValue(noOfStars + stars)
                                                                                            .addOnCompleteListener {
                                                                                                if (it.isSuccessful) {
                                                                                                    Firebase.database.reference.child(
                                                                                                        "users"
                                                                                                    )
                                                                                                        .child(userId.toString())
                                                                                                        .child("feedbackList")
                                                                                                        .child(Firebase.auth.currentUser!!.uid)
                                                                                                        .setValue(
                                                                                                            FeedbackModel(
                                                                                                                Firebase.auth.currentUser!!.uid,
                                                                                                                "Reliable",
                                                                                                                stars
                                                                                                            )
                                                                                                        )
                                                                                                }
                                                                                            }
                                                                                    }
                                                                                    else {
                                                                                        Firebase.database.reference.child("users")
                                                                                            .child(userId.toString())
                                                                                            .child("rank")
                                                                                            .child("Reliable")
                                                                                            .setValue(starsReliable + stars)
                                                                                            .addOnCompleteListener {
                                                                                                if (it.isSuccessful) {
                                                                                                    Firebase.database.reference.child(
                                                                                                        "users"
                                                                                                    )
                                                                                                        .child(userId.toString())
                                                                                                        .child("feedbackList")
                                                                                                        .child(Firebase.auth.currentUser!!.uid)
                                                                                                        .setValue(
                                                                                                            FeedbackModel(
                                                                                                                Firebase.auth.currentUser!!.uid,
                                                                                                                "Reliable",
                                                                                                                stars
                                                                                                            )
                                                                                                        )
                                                                                                }
                                                                                            }
                                                                                    }
                                                                                } catch (e: Exception) {
                                                                                }

                                                                                true
                                                                            }
                                                                            "Problem Solver" -> {
                                                                                try {
                                                                                    if (tempRank == "Problem Solver") {
                                                                                        Firebase.database.reference.child("users")
                                                                                            .child(userId.toString())
                                                                                            .child("rank")
                                                                                            .child("Problem Solver")
                                                                                            .setValue(noOfStars + stars)
                                                                                            .addOnCompleteListener {
                                                                                                if (it.isSuccessful) {
                                                                                                    Firebase.database.reference.child(
                                                                                                        "users"
                                                                                                    )
                                                                                                        .child(userId.toString())
                                                                                                        .child("feedbackList")
                                                                                                        .child(Firebase.auth.currentUser!!.uid)
                                                                                                        .setValue(
                                                                                                            FeedbackModel(
                                                                                                                Firebase.auth.currentUser!!.uid,
                                                                                                                "Problem Solver",
                                                                                                                stars
                                                                                                            )
                                                                                                        )
                                                                                                }
                                                                                            }
                                                                                    }
                                                                                    else {
                                                                                        Firebase.database.reference.child("users")
                                                                                            .child(userId.toString())
                                                                                            .child("rank")
                                                                                            .child("Problem Solver")
                                                                                            .setValue(starsProblemSolver + stars)
                                                                                            .addOnCompleteListener {
                                                                                                if (it.isSuccessful) {
                                                                                                    Firebase.database.reference.child(
                                                                                                        "users"
                                                                                                    )
                                                                                                        .child(userId.toString())
                                                                                                        .child("feedbackList")
                                                                                                        .child(Firebase.auth.currentUser!!.uid)
                                                                                                        .setValue(
                                                                                                            FeedbackModel(
                                                                                                                Firebase.auth.currentUser!!.uid,
                                                                                                                "Problem Solver",
                                                                                                                stars
                                                                                                            )
                                                                                                        )
                                                                                                }
                                                                                            }
                                                                                    }
                                                                                } catch (e: Exception) {
                                                                                }

                                                                                true
                                                                            }
                                                                            else -> false
                                                                        }
                                                                    }



                                                            }


                                                        }

                                                        override fun onCancelled(error: DatabaseError) {
                                                            TODO("Not yet implemented")
                                                        }

                                                    })
                                            } catch (e: Exception) {
                                            }
                                        }

                                    }


                                    override fun onCancelled(error: DatabaseError) {
                                        TODO("Not yet implemented")
                                    }

                                })
                        } catch (e: Exception) {
                        }



                        Toast.makeText(this, "Rank Edited", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()

                    }
                    else {
                        when (rankName) {
                            "Helpful" -> {
                                Firebase.database.reference.child("users")
                                    .child(userId.toString())
                                    .child("rank")
                                    .child("Helpful")
                                    .setValue(starsHelpful + stars).addOnCompleteListener {
                                        if (it.isSuccessful) {
                                            Firebase.database.reference.child("users")
                                                .child(userId.toString())
                                                .child("feedbackList")
                                                .child(Firebase.auth.currentUser!!.uid)
                                                .setValue(
                                                    FeedbackModel(
                                                        Firebase.auth.currentUser!!.uid,
                                                        "Helpful",
                                                        stars
                                                    )
                                                )
                                        }
                                    }
                                true
                            }
                            "Resourceful" -> {
                                Firebase.database.reference.child("users")
                                    .child(userId.toString())
                                    .child("rank")
                                    .child("Resourceful")
                                    .setValue(starsResourceful + stars).addOnCompleteListener {
                                        if (it.isSuccessful) {
                                            Firebase.database.reference.child("users")
                                                .child(userId.toString())
                                                .child("feedbackList")
                                                .child(Firebase.auth.currentUser!!.uid)
                                                .setValue(
                                                    FeedbackModel(
                                                        Firebase.auth.currentUser!!.uid,
                                                        "Resourceful",
                                                        stars
                                                    )
                                                )
                                        }
                                    }
                                true
                            }
                            "Genius" -> {
                                Firebase.database.reference.child("users")
                                    .child(userId.toString())
                                    .child("rank")
                                    .child("Genius")
                                    .setValue(starsGenius + stars).addOnCompleteListener {
                                        if (it.isSuccessful) {
                                            Firebase.database.reference.child("users")
                                                .child(userId.toString())
                                                .child("feedbackList")
                                                .child(Firebase.auth.currentUser!!.uid)
                                                .setValue(
                                                    FeedbackModel(
                                                        Firebase.auth.currentUser!!.uid,
                                                        "Genius",
                                                        stars
                                                    )
                                                )
                                        }
                                    }
                                true
                            }
                            "Reliable" -> {
                                Firebase.database.reference.child("users")
                                    .child(userId.toString())
                                    .child("rank")
                                    .child("Reliable")
                                    .setValue(starsReliable + stars).addOnCompleteListener {
                                        if (it.isSuccessful) {
                                            Firebase.database.reference.child("users")
                                                .child(userId.toString())
                                                .child("feedbackList")
                                                .child(Firebase.auth.currentUser!!.uid)
                                                .setValue(
                                                    FeedbackModel(
                                                        Firebase.auth.currentUser!!.uid,
                                                        "Reliable",
                                                        stars
                                                    )
                                                )
                                        }
                                    }
                                true
                            }
                            "Problem Solver" -> {
                                Firebase.database.reference.child("users")
                                    .child(userId.toString())
                                    .child("rank")
                                    .child("Problem Solver")
                                    .setValue(starsProblemSolver + stars).addOnCompleteListener {
                                        if (it.isSuccessful) {
                                            Firebase.database.reference.child("users")
                                                .child(userId.toString())
                                                .child("feedbackList")
                                                .child(Firebase.auth.currentUser!!.uid)
                                                .setValue(
                                                    FeedbackModel(
                                                        Firebase.auth.currentUser!!.uid,
                                                        "Problem Solver",
                                                        stars
                                                    )
                                                )
                                        }
                                    }
                                true
                            }
                            else -> false
                        }
                        Toast.makeText(this, "Rank Submitted", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
                } catch (e: Exception) {
                }

            }
//            dialog.dismiss()
        }

        cancelButton.setOnClickListener {
            dialog.dismiss()
        }
        builder.setView(view)
        dialog = builder.create()


        try {
            FirebaseDatabase.getInstance().reference.child("users")
                .child(userId.toString())
                .child("followers")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists()){
                            followersArrayList.clear()
                            for (item in snapshot.children) {
                                followersArrayList.add(item.key.toString())
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
                .child(userId.toString())
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists()){
                            val model = snapshot.getValue(User::class.java)
                            isAccountPrivate = model!!.accountPrivate

                            Log.i("followers", followersArrayList.toString())

                            if (isAccountPrivate && !followersArrayList.contains(Firebase.auth.currentUser!!.uid)) {
                                noOfFollowers.isEnabled = false
                                noOfFollowing.isEnabled = false
                                documentPostButton.visibility = View.GONE
                                libraryButton.visibility = View.GONE
                                notesButton.visibility = View.GONE
                                profilePostRV.visibility = View.GONE
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
                .child(Firebase.auth.currentUser!!.uid)
                .child("following")
                .addValueEventListener(object : ValueEventListener {
                    @SuppressLint("ResourceAsColor")
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists()){
                            for (item in snapshot.children) {
                                if (item.key == userId) {
                                    followButton.backgroundTintList = ContextCompat.getColorStateList(applicationContext,R.color.light_blue)
                                    followButton.text = "Following"
                                    isFollowing = true
                                    break
                                }
                            }
                        }

                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
        } catch (e: Exception) {
        }

        libraryButton.setOnClickListener {
            try {
                val intent = Intent(this, LibraryActivity::class.java)
                intent.putExtra("uid", userId.toString())
                startActivity(intent)
            } catch (e: Exception) {
            }
        }


        var receiverModel = AllChatModel()

        try {
            FirebaseDatabase.getInstance().reference.child("users")
                .child(userId.toString())
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists()){
                            val model = snapshot.getValue(AllChatModel::class.java)!!
                            receiverModel.uid = model.uid
                            receiverModel.displayName = model.displayName
                            receiverModel.profilePicture = model.profilePicture
                            receiverModel.username = model.username
                        }

                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
        } catch (e: Exception) {
        }

        var currentUserModel = AllChatModel()

        try {
            FirebaseDatabase.getInstance().reference.child("users")
                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists()){
                            val model = snapshot.getValue(AllChatModel::class.java)!!
                            currentUserModel.uid = model.uid
                            currentUserModel.displayName = model.displayName
                            currentUserModel.profilePicture = model.profilePicture
                            currentUserModel.username = model.username

                            //building a request model from firebase to add to request list of other user
                            requestModel = snapshot.getValue(RequestModel::class.java)!!
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
        } catch (e: Exception) {
        }



        followButton.setOnClickListener {
            if (isAccountPrivate && !isFollowing) {
                FirebaseDatabase.getInstance().reference.child("users")
                    .child(userId.toString())
                    .child("request_list")
                    .child(Firebase.auth.currentUser!!.uid)
                    .setValue(requestModel).addOnCompleteListener {
                        if (it.isSuccessful) {
                            FirebaseDatabase.getInstance().reference.child("users")
                                .child(Firebase.auth.currentUser!!.uid)
                                .child("following")
                                .child(userId.toString())
                                .setValue(receiverModel)
                                .addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        isFollowing = true
                                        followButton.text = "Following"
                                        followButton.backgroundTintList = ContextCompat.getColorStateList(applicationContext,R.color.light_blue)
                                        refreshProfile(userId.toString())

                                        FirebaseDatabase.getInstance().reference.child("users")
                                            .child(userId.toString()).child("token")
                                            .addListenerForSingleValueEvent(object : ValueEventListener{
                                                override fun onDataChange(snapshot: DataSnapshot) {
                                                    if(snapshot.exists()){
                                                        try {
                                                            TOKEN = snapshot.value.toString()
                                                            PushNotification(
                                                                NotificationData(
                                                                    "${currentUserModel.username} started following you",
                                                                    "click here to see"
                                                                ),
                                                                TOKEN
                                                            ).also {
                                                                try {
                                                                    sendNotification(it)
                                                                } catch (e: Exception) {
                                                                }
                                                            }
                                                        } catch (e: Exception) {
                                                        }
                                                    }

                                                }

                                                override fun onCancelled(error: DatabaseError) {
                                                    TODO("Not yet implemented")
                                                }

                                            })
                                    }
                                }
                        }
                    }
            } else if (!isFollowing) {
                FirebaseDatabase.getInstance().reference.child("users")
                    .child(Firebase.auth.currentUser!!.uid)
                    .child("following")
                    .child(userId.toString())
                    .setValue(receiverModel)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            isFollowing = true
                            FirebaseDatabase.getInstance().reference.child("users")
                                .child(userId.toString())
                                .child("followers")
                                .child(Firebase.auth.currentUser!!.uid)
                                .setValue(currentUserModel).addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        Toast.makeText(
                                            this,
                                            "Successfully followed",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        FirebaseDatabase.getInstance().reference.child("users")
                                            .child(userId.toString()).child("token")
                                            .addListenerForSingleValueEvent(object : ValueEventListener{
                                                override fun onDataChange(snapshot: DataSnapshot) {
                                                    if(snapshot.exists()){
                                                        TOKEN = snapshot.value.toString()
                                                        PushNotification(
                                                            NotificationData(
                                                                "${currentUserModel.username} started following you",
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

                                        followButton.setText("Following")
                                        followButton.backgroundTintList = ContextCompat.getColorStateList(applicationContext,R.color.light_blue)
                                        refreshProfile(userId.toString())
                                    }
                                }
                        }
                    }
            } else {
                FirebaseDatabase.getInstance().reference.child("users")
                    .child(Firebase.auth.currentUser!!.uid)
                    .child("following")
                    .child(userId.toString())
                    .removeValue()
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            isFollowing = false
                            FirebaseDatabase.getInstance().reference.child("users")
                                .child(userId.toString())
                                .child("followers")
                                .child(Firebase.auth.currentUser!!.uid)
                                .removeValue().addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        refreshProfile(userId.toString())
                                        Toast.makeText(
                                            this,
                                            "Successfully unfollowed",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        followButton.setText("Follow")
                                        followButton.backgroundTintList = ContextCompat.getColorStateList(applicationContext,R.color.blue)
                                    }
                                }
                        }
                    }
            }

        }

        messageButton.setOnClickListener {
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("userId", userId)
            intent.putExtra("username", username.text.toString())
            intent.putExtra("profileImage",profileImg )
            startActivity(intent)
        }


        try {// To get post on profile
            Firebase.database.reference.child("users")
                .child(userId.toString()).child("my_posts")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists()){
                            postArrayList.clear()
                            for (item in snapshot.children) {
                                val post = item.getValue(Post::class.java)!!
                                postArrayList.add(post.postImage)
                                postAdapter.notifyDataSetChanged()

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
            FirebaseDatabase.getInstance().reference.child("users").child(userId!!)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            Log.i("ddd", snapshot.toString())
                            usernameOther = snapshot.child("username").value.toString()
                            val usernameL = snapshot.child("username").value
                            val displayNameL = snapshot.child("displayName").value
                            val bioL = snapshot.child("bio").value
                            val professionL = snapshot.child("profession").value
                            val profilePictureL = snapshot.child("profilePicture").value
                            val followersCount = snapshot.child("followers").childrenCount
                            val followingCount = snapshot.child("following").childrenCount
                            val postCount = snapshot.child("my_posts").childrenCount

                            toolbar.title = usernameL.toString()
                            username.text = usernameL.toString()
                            displayName.text = displayNameL.toString()
                            if(bioL.toString().isNotEmpty()){
                                bio.visibility = View.VISIBLE
                            }
                            if(professionL.toString().isNotEmpty()){
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

    private fun sendNotification(notification: PushNotification) =
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.api.postNotification(notification)
            } catch (e: Exception) {
                Log.e("TAG", e.toString())
            }
        }

    private fun refreshProfile(userId: String) {

        try {
            FirebaseDatabase.getInstance().reference.child("users").child(userId!!)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            Log.i("ddd", snapshot.toString())
                            val usernameL = snapshot.child("username").value
                            val displayNameL = snapshot.child("displayName").value
                            val bioL = snapshot.child("bio").value
                            val professionL = snapshot.child("profession").value
                            val profilePictureL = snapshot.child("profilePicture").value
                            val followersCount = snapshot.child("followers").childrenCount
                            val followingCount = snapshot.child("following").childrenCount
                            val postCount = snapshot.child("my_posts").childrenCount

                            if(bioL.toString().isNotEmpty()){
                                bio.visibility = View.VISIBLE
                            }
                            if(professionL.toString().isNotEmpty()){
                                profession.visibility = View.VISIBLE
                            }

                            toolbar.title = usernameL.toString()
                            username.text = usernameL.toString()
                            displayName.text = displayNameL.toString()
                            bio.text = bioL.toString()
                            profession.text = professionL.toString()
                            Glide.with(applicationContext)
                                .load(profilePictureL.toString())
                                .placeholder(R.drawable.profile_placeholder)
                                .into(profilePictureIV)
                            noOfFollowers.text = followersCount.toString()
                            noOfFollowing.text = followingCount.toString()
                            noOfPosts.text = postCount.toString()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
        } catch (e: Exception) {
        }
    }
}