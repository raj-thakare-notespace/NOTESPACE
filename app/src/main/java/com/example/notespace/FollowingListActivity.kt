package com.example.notespace

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notespace.adapters.FollowingFollowersAdapter
import com.example.notespace.models.AllChatModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FollowingListActivity : AppCompatActivity() {

    lateinit var toolbar: MaterialToolbar

    lateinit var allChatAdapter : FollowingFollowersAdapter

    lateinit var recyclerView: RecyclerView

    var arrayListAllFollowers = ArrayList<AllChatModel>()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_following_list)

        allChatAdapter = FollowingFollowersAdapter(this,arrayListAllFollowers)

        toolbar = findViewById(R.id.followingListToolBar)

        toolbar.setNavigationOnClickListener {
            finish()
        }


        recyclerView = findViewById(R.id.followingListRV)

        recyclerView.layoutManager = LinearLayoutManager(this)

        recyclerView.adapter = allChatAdapter


        var follwersUids : ArrayList<String> = ArrayList()

        FirebaseDatabase.getInstance().reference.child("users")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child("following")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    arrayListAllFollowers.clear()
                    for(item in snapshot.children){
                        val model = item.getValue(AllChatModel::class.java)!!
                        arrayListAllFollowers.add(model)
                        Log.i("ghfi",model.toString())
                    }
                    allChatAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })


    }
}