package com.startup.notespace

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.startup.notespace.adapters.FollowingFollowersAdapter
import com.startup.notespace.models.AllChatModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FollowingListActivityOther : AppCompatActivity() {

    lateinit var toolbar: MaterialToolbar

    lateinit var allChatAdapter : FollowingFollowersAdapter

    lateinit var recyclerView: RecyclerView

    var arrayListAllFollowers = ArrayList<AllChatModel>()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_following_list_other)

        val userId = intent.getStringExtra("uid")

        allChatAdapter = FollowingFollowersAdapter(this,arrayListAllFollowers)

        toolbar = findViewById(R.id.followingListToolBarOther)

        toolbar.setNavigationOnClickListener {
            finish()
        }


        recyclerView = findViewById(R.id.followingListRVOther)

        recyclerView.layoutManager = LinearLayoutManager(this)

        recyclerView.adapter = allChatAdapter


        var follwersUids : ArrayList<String> = ArrayList()

        try {
            FirebaseDatabase.getInstance().reference.child("users")
                .child(userId.toString())
                .child("following")
                .addValueEventListener(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists()){
                            arrayListAllFollowers.clear()
                            for(item in snapshot.children){
                                val model = item.getValue(AllChatModel::class.java)!!
                                arrayListAllFollowers.add(model)
                                Log.i("ghfi",model.toString())
                            }
                            allChatAdapter.notifyDataSetChanged()
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