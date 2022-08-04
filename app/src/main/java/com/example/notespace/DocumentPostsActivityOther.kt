package com.example.notespace

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notespace.adapters.DocPostAdapterOther
import com.example.notespace.models.DocPostModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DocumentPostsActivityOther : AppCompatActivity() {

    lateinit var recyclerView : RecyclerView
    lateinit var adapter : DocPostAdapterOther
    lateinit var arrayList: ArrayList<DocPostModel>
    lateinit var toolbar: MaterialToolbar

    lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_document_posts_other)

        toolbar = findViewById(R.id.docPostToolBar)

        searchView = findViewById(R.id.searchViewDocPost)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchView.clearFocus()
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText)
                return true
            }

        })

        toolbar.setNavigationOnClickListener {
            finish()
        }

        val uid = intent.getStringExtra("uid").toString()

        arrayList = ArrayList()

        recyclerView = findViewById(R.id.docPostRecyclerView)
        adapter = DocPostAdapterOther(this,arrayList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        try {
            FirebaseDatabase.getInstance().reference.child("users")
                .child(uid)
                .child("my_document_posts")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()){
                            arrayList.clear()
                            for(item in snapshot.children){
                                val model = item.getValue(DocPostModel::class.java)
                                arrayList.add(model!!)
                                adapter.notifyDataSetChanged()
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

    private fun filterList(text: String?) {
        var filteredList = java.util.ArrayList<DocPostModel>()
        for(item in arrayList){
            if (text != null) {
                if(item.docName!!.toLowerCase().contains(text.toLowerCase()) || item.docName!!.toLowerCase().contains(text.toLowerCase())){
                    filteredList.add(item)
                }
            }
        }

        if(filteredList.isEmpty()){
//            Toast.makeText(this,"No data found", Toast.LENGTH_SHORT).show()
        }
        else{
            adapter.setFilteredList(filteredList)
        }
    }
}