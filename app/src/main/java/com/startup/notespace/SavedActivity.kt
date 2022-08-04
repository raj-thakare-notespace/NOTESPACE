package com.startup.notespace

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.startup.notespace.adapters.PostAdapter
import com.startup.notespace.models.Post
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class SavedActivity : AppCompatActivity() {

    private lateinit var adapter: PostAdapter
    lateinit var recyclerView: RecyclerView
    var postList = ArrayList<Post>()
    lateinit var auth: FirebaseAuth
    lateinit var database: FirebaseDatabase
    lateinit var toolBar : MaterialToolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved)

        toolBar = findViewById(R.id.savedToolBar)

        toolBar.setNavigationOnClickListener {
            finish()
        }

        auth = FirebaseAuth.getInstance()
        database = Firebase.database

        recyclerView = findViewById(R.id.recyclerViewPost)

        adapter = PostAdapter(applicationContext, postList)

        recyclerView.layoutManager = LinearLayoutManager(this)

        recyclerView.adapter = adapter

        try {
            database.reference.child("users")
                .child(auth.currentUser!!.uid)
                .child("saved")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists()){
                            postList.clear()
                            for (dataSnapshot in snapshot.children) {
                                val post = dataSnapshot.getValue(Post::class.java)
                                post!!.postId = dataSnapshot.key.toString()
                                postList.add(post)
                                Log.i("postrrr", post.toString())
                            }
                            adapter.notifyDataSetChanged()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })
        } catch (e: Exception) {
        }


    }
}