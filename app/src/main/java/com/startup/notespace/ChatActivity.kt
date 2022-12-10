package com.startup.notespace

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.startup.notespace.adapters.MessageAdapter
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.startup.notespace.models.*
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

private var TOKEN = ""

class ChatActivity : AppCompatActivity() {

    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageBox: EditText
    private lateinit var sendButton: ImageView
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<MessageModel>
    private lateinit var profileImage : CircleImageView
    private lateinit var usernameTV : TextView

    lateinit var toolbar: MaterialToolbar

    val TAG = "MainActivity"

    var receiverRoom: String? = null
    var senderRoom: String? = null

    var chattedList = ArrayList<AllChatModel>()
    var chattedListUidOnly = ArrayList<String>()


    override fun onStart() {
        super.onStart()
        refreshChattedListUidOnly()
    }

    private fun refreshMessages(senderRoom : String,messageList : ArrayList<MessageModel>){

        try {
            FirebaseDatabase.getInstance().reference.child("chats").child(senderRoom!!)
                .child("messages")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists()){
                            messageList.clear()
                            for (postSnapshot in snapshot.children) {
                                val message = postSnapshot.getValue(MessageModel::class.java)
                                messageList.add(message!!)
                                chatRecyclerView.smoothScrollToPosition(messageList.count() - 1)
                            }

                            messageAdapter.notifyDataSetChanged()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })
        } catch (e: Exception) {
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val receiverUid = intent.getStringExtra("userId")
        val name = intent.getStringExtra("username")
        val profileImg = intent.getStringExtra("profileImage")



        refreshChattedListUidOnly()

        chatRecyclerView = findViewById(R.id.chatRecyclerView)
        messageBox = findViewById(R.id.messageBox)
        sendButton = findViewById(R.id.sendMessage)
        usernameTV = findViewById(R.id.userNameTVChat)
        profileImage = findViewById(R.id.profileImageChat)
        toolbar = findViewById(R.id.toolBarChat)

        toolbar.setNavigationOnClickListener {
            finish()
        }

        toolbar.setOnClickListener {
            val intent = Intent(this,OtherUsersProfileActivity::class.java)
            intent.putExtra("uid",receiverUid.toString())
            intent.putExtra("username",name.toString())
            intent.putExtra("profilePicture",profileImg.toString())
            startActivity(intent)
        }


        try {
            Firebase.database.reference.child("users").child(receiverUid.toString())
                .addValueEventListener(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists()){

                            val model = snapshot.getValue(User::class.java)!!
                            Glide.with(applicationContext)
                                .load(model.profilePicture)
                                .placeholder(R.drawable.profile_placeholder)
                                .into(profileImage)
                            usernameTV.text = model.username

                        }

                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
        } catch (e: Exception) {
        }


        val senderUid = Firebase.auth.currentUser!!.uid

        senderRoom = receiverUid + senderUid
        receiverRoom = senderUid + receiverUid

        supportActionBar?.title = name

        messageList = ArrayList()
        messageAdapter = MessageAdapter(this, messageList)

        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatRecyclerView.adapter = messageAdapter

        // To check with which users this user has chatted
        try {
            FirebaseDatabase.getInstance().reference.child("users")
                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                .child("chatted_list")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists()){
                            chattedList.clear()
                            chattedListUidOnly.clear()
                            for (item in snapshot.children) {
                                val model = item.getValue(AllChatModel::class.java)!!
                                chattedList.add(model)
                                chattedListUidOnly.add(model.uid)
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })
        } catch (e: Exception) {
        }


        try {
            FirebaseDatabase.getInstance().reference.child("chats").child(senderRoom!!)
                .child("messages")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        messageList.clear()
                        for (postSnapshot in snapshot.children) {
                            val message = postSnapshot.getValue(MessageModel::class.java)
                            messageList.add(message!!)
                            chatRecyclerView.smoothScrollToPosition(messageList.count() - 1)
                        }

                        messageAdapter.notifyDataSetChanged()
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })
        } catch (e: Exception) {
        }

        lateinit var receiverModel: AllChatModel

        try {
            FirebaseDatabase.getInstance().reference.child("users")
                .child(receiverUid.toString())
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists()){
                            receiverModel = snapshot.getValue(AllChatModel::class.java)!!
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
        } catch (e: Exception) {
        }

        lateinit var currentUserModel: AllChatModel

        try {
            FirebaseDatabase.getInstance().reference.child("users")
                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                .addValueEventListener(object : ValueEventListener {
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


        sendButton.setOnClickListener {

            var titleNotification = "New message from '${currentUserModel.username}'"
            var messageNotification = "${messageBox.text.toString()}"

            // Code to add newly chatted user to all chats activity
            try {
                if (!chattedListUidOnly.contains(receiverModel.uid)) {
                    FirebaseDatabase.getInstance().reference
                        .child("users")
                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                        .child("chatted_list")
                        .child(receiverModel.uid)
                        .setValue(receiverModel)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                chattedList.add(receiverModel)
                                chattedListUidOnly.add(receiverModel.uid)
                                FirebaseDatabase.getInstance().reference
                                    .child("users")
                                    .child(receiverUid.toString())
                                    .child("chatted_list")
                                    .child(Firebase.auth.currentUser!!.uid)
                                    .setValue(currentUserModel)
                            }
                        }
                }
            } catch (e: Exception) {
            }


            if (messageBox.text.toString().isNotEmpty()) {
                val message = messageBox.text.toString()
                var time = Date().time
                val messageObject = MessageModel(message, senderUid, receiverUid, time)

                try {
                    FirebaseDatabase.getInstance().reference.child("users")
                        .child(receiverUid.toString()).child("token")
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if(snapshot.exists()){
                                    TOKEN = snapshot.value.toString()
                                    FirebaseDatabase.getInstance().reference.child("chats")
                                        .child(senderRoom!!).child("messages")
                                        .push().setValue(messageObject).addOnSuccessListener {
                                            FirebaseDatabase.getInstance().reference.child("chats")
                                                .child(receiverRoom!!).child("messages")
                                                .push().setValue(messageObject)
                                                .addOnCompleteListener {
                                                    if (it.isSuccessful) {
                                                        try {
                                                            refreshMessages(senderRoom.toString(),messageList)
                                                            PushNotification(
                                                                NotificationData(
                                                                    titleNotification,
                                                                    messageNotification
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


                                        }
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                TODO("Not yet implemented")
                            }

                        })
                } catch (e: Exception) {
                }


                messageBox.setText("")
            }
        }




    }

    override fun onStop() {
        super.onStop()
        try {
            FirebaseDatabase.getInstance().reference.child("chats").child(senderRoom!!)
                .child("noOfMessages").setValue(messageList.size)
        } catch (e: Exception) {
        }
    }

    private fun sendNotification(notification: PushNotification) =
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.api.postNotification(notification)
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }
        }


    private fun refreshChattedListUidOnly() {

        try {
            FirebaseDatabase.getInstance().reference
                .child("users")
                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                .child("chatted_list")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists()){
                            chattedList.clear()
                            chattedListUidOnly.clear()
                            for (item in snapshot.children) {
                                val model = item.getValue(AllChatModel::class.java)!!
                                Log.i("model11", model.toString())
                                chattedListUidOnly.add(model.uid)
                                chattedList.add(model)
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })
        } catch (e: Exception) {
        }
    }

}