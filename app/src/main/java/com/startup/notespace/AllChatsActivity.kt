package com.startup.notespace

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.startup.notespace.adapters.AllChatAdapter
import com.startup.notespace.models.AllChatModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AllChatsActivity : AppCompatActivity() {

    //Hi

    lateinit var toolbar: MaterialToolbar

    lateinit var allChatAdapter: AllChatAdapter

    lateinit var recyclerView: RecyclerView

    var arrayListAllChat = ArrayList<AllChatModel>()

    var chattedList = ArrayList<AllChatModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_chats)

        allChatAdapter = AllChatAdapter(this, arrayListAllChat)

        toolbar = findViewById(R.id.allChatsToolBar)

        toolbar.setNavigationOnClickListener {
            finish()
        }



        recyclerView = findViewById(R.id.all_chats_recycler_view)

        recyclerView.layoutManager = LinearLayoutManager(this)

        recyclerView.adapter = allChatAdapter


        try {
            FirebaseDatabase.getInstance().reference
                .child("users")
                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                .child("chatted_list")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists()){
                            arrayListAllChat.clear()
                            chattedList.clear()
                            for (item in snapshot.children) {
                                val model = item.getValue(AllChatModel::class.java)!!
                                Log.i("model11", model.toString())
                                chattedList.add(model)
                            }
                            Log.i("uyt", chattedList.toString())
                            arrayListAllChat.addAll(chattedList)
                            allChatAdapter.notifyDataSetChanged()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })
        } catch (e: Exception) {
        }


    }
}