package com.startup.notespace

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.startup.notespace.adapters.RequestsAdapter
import com.startup.notespace.models.RequestModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class RequestsActivity : AppCompatActivity() {

    var arrayList = ArrayList<RequestModel>()
    lateinit var recyclerView: RecyclerView
    lateinit var adapter : RequestsAdapter
    lateinit var toolbar: MaterialToolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_requests)

        toolbar = findViewById(R.id.requestsToolBar)

        toolbar.setNavigationOnClickListener {
            finish()
        }

        recyclerView = findViewById(R.id.rvRequestActivity)

        adapter = RequestsAdapter(this,arrayList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        try {
            FirebaseDatabase.getInstance().reference.child("users")
                .child(Firebase.auth.currentUser!!.uid)
                .child("request_list")
                .addValueEventListener(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        arrayList.clear()
                        for(item in snapshot.children){
                            var model = item.getValue(RequestModel::class.java)
                            arrayList.add(model!!)
                        }
                        adapter.notifyDataSetChanged()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
        } catch (e: Exception) {
        }

    }
}