package com.example.notespace

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.notespace.adapters.MessageAdapter
import com.example.notespace.models.MessageModel
import com.example.notespace.models.NotificationData
import com.example.notespace.models.PushNotification
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class GroupChatActivity : AppCompatActivity() {

    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageBox: EditText
    private lateinit var sendButton: ImageView
    private lateinit var profileImageIV: ImageView
    private lateinit var groupNameTV: TextView
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<MessageModel>

    var uid = ""


    private lateinit var materialToolbar: MaterialToolbar

    lateinit var createdBy: String

    var arrayListMembersToken = ArrayList<String>()

    override fun onStart() {
        super.onStart()
        if (Firebase.auth.currentUser!!.uid != createdBy) {
            messageBox.visibility = View.GONE
            sendButton.visibility = View.GONE
        }
    }

    private fun refreshGroupMessages(){

        try {
            FirebaseDatabase.getInstance().reference
                .child("groupChat")
                .child(uid!!)
                .child("messages")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists()){
                            messageList.clear()
                            for (dataSnapshot in snapshot.children) {
                                val model = dataSnapshot.getValue(MessageModel::class.java)!!
                                model.createdBy = createdBy
                                messageList.add(model!!)
                                chatRecyclerView.smoothScrollToPosition(messageList.count() - 1)
                            }
                            messageAdapter.notifyDataSetChanged()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
        } catch (e: Exception) {
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        messageList = ArrayList()
        setContentView(R.layout.activity_group_chat)
        profileImageIV = findViewById(R.id.profileImageGroupChat)
        groupNameTV = findViewById(R.id.groupNameTVGroupChat)

        materialToolbar = findViewById(R.id.groupToolBarChat)

        materialToolbar.setNavigationOnClickListener {
            finish()
        }

        val username = intent.getStringExtra("usernameGroup")
        uid = intent.getStringExtra("uid").toString()
        val displayName = intent.getStringExtra("groupDisplayName")
        val profilePicture = intent.getStringExtra("profilePicture")
        createdBy = intent.getStringExtra("createdBy").toString()


        materialToolbar.setOnClickListener {
            if(createdBy == Firebase.auth.currentUser!!.uid){
                val intent = Intent(this, GroupProfileActivity::class.java)
                intent.putExtra("uid", uid)
                startActivity(intent)
            }
            else{
                val intent = Intent(this, GroupProfileOther::class.java)
                intent.putExtra("uid", uid)
                startActivity(intent)
            }

        }

        groupNameTV.text = displayName

        Glide.with(this)
            .load(profilePicture)
            .placeholder(R.drawable.profile_placeholder)
            .into(profileImageIV)

        materialToolbar.setOnMenuItemClickListener {
            when (it.itemId) {

                R.id.library -> {

                    val intent = Intent(this, GroupLibrary::class.java)
                    intent.putExtra("uid", uid)
                    intent.putExtra("createdBy", createdBy)
                    startActivity(intent)

                    true
                }

                else -> true
            }

        }


        messageAdapter = MessageAdapter(this, messageList)


        chatRecyclerView = findViewById(R.id.chatRecyclerViewChat)
        messageBox = findViewById(R.id.messageBoxChat)
        sendButton = findViewById(R.id.sendMessageChat)

        val senderId = Firebase.auth.currentUser!!.uid

        chatRecyclerView.layoutManager = LinearLayoutManager(this)


        chatRecyclerView.adapter = messageAdapter

        try {
            FirebaseDatabase.getInstance().reference
                .child("groupChat")
                .child(uid!!)
                .child("messages")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists()){
                            messageList.clear()
                            for (dataSnapshot in snapshot.children) {
                                val model = dataSnapshot.getValue(MessageModel::class.java)!!
                                model.createdBy = createdBy
                                messageList.add(model!!)
                                chatRecyclerView.smoothScrollToPosition(messageList.count() - 1)
                            }
                            messageAdapter.notifyDataSetChanged()
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
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists()){
                            for (item in snapshot.children) {
                                if(item.key.toString() == Firebase.auth.currentUser!!.uid){
                                    continue
                                }
                                FirebaseDatabase.getInstance().reference.child("users")
                                    .child(item.key.toString())
                                    .child("token")
                                    .addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            arrayListMembersToken.add(snapshot.value.toString())
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

        sendButton.setOnClickListener {

            var titleNotification = "'${displayName}' has new message"
            var messageNotification = "${messageBox.text.toString()}"

            if (messageBox.text.toString().isNotEmpty()) {
                val message = messageBox.text.toString()
                var time = Date().time
                val model = MessageModel(message, senderId, null, time, username,uid)
                messageBox.setText("")

                try {
                    FirebaseDatabase.getInstance().reference.child("groupChat")
                        .child(uid)
                        .child("messages")
                        .push()
                        .setValue(model).addOnCompleteListener {
                            if (it.isSuccessful) {
                                refreshGroupMessages()
                                for (item in arrayListMembersToken) {
                                    PushNotification(
                                        NotificationData(titleNotification, messageNotification),
                                        item
                                    ).also {
                                        try {
                                            sendNotification(it)
                                        } catch (e: Exception) {
                                        }
                                    }
                                }
                            }
                        }
                } catch (e: Exception) {
                }
            }
        }
    }

    private fun refreshMessages(uid: String, messageList: ArrayList<MessageModel>) {

        try {
            FirebaseDatabase.getInstance().reference.child("groupChat").child(uid!!)
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


    override fun onStop() {
        super.onStop()
        refreshMessages(uid, messageList)

        try {
            FirebaseDatabase.getInstance().reference.child("users").child(FirebaseAuth.getInstance().currentUser!!.uid).child("my_groups")
                .child(uid).child("noOfMessages").setValue(messageList.size)
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
}