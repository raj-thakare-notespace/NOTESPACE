package com.example.notespace

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notespace.adapters.MembersAdapter
import com.example.notespace.models.Group
import com.example.notespace.models.GroupMemberModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class GroupMembersActivity : AppCompatActivity() {

    lateinit var toolbar: MaterialToolbar

    lateinit var allChatAdapter: MembersAdapter

    lateinit var recyclerView: RecyclerView

    lateinit var groupCreatedBy : String

    var arrayListAllFollowers = ArrayList<GroupMemberModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_members)

        val uid = intent.getStringExtra("groupUid")

        FirebaseDatabase.getInstance()
            .reference.child("users")
            .child(uid.toString())
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val model = snapshot.getValue(Group::class.java)
                    groupCreatedBy = model!!.createdBy
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

        allChatAdapter = MembersAdapter(this, arrayListAllFollowers)

        toolbar = findViewById(R.id.groupMembersToolBar)

        toolbar.setNavigationOnClickListener {
            finish()
        }


        recyclerView = findViewById(R.id.groupMembersRV)

        recyclerView.layoutManager = LinearLayoutManager(this)

        recyclerView.adapter = allChatAdapter


        var membersUidsList: ArrayList<String> = ArrayList()

        // To get all the group members uids
        FirebaseDatabase.getInstance().reference.child("users")
            .child(uid.toString())
            .child("members")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    membersUidsList.clear()
                    for (item in snapshot.children) {
                        membersUidsList.add(item.key.toString())
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

        FirebaseDatabase.getInstance().reference.child("users")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    arrayListAllFollowers.clear()
                    for (i in snapshot.children) {

                        if (membersUidsList.contains(i.key.toString())) {


                            val model = i.getValue(GroupMemberModel::class.java)!!
                            model.groupUid = uid.toString()
                            model.createdBy = groupCreatedBy
                            arrayListAllFollowers.add(model)
                            Log.i("wuff", model.toString())
                            allChatAdapter.notifyDataSetChanged()

                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

    }
}