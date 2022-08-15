package com.startup.notespace

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.startup.notespace.adapters.Library2Adapter
import com.startup.notespace.models.MyLibrary2Model
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class Library2 : AppCompatActivity() {


    val storage = Firebase.storage

    lateinit var toolBarLibraryDetail: MaterialToolbar

    lateinit var searchView: SearchView


    lateinit var uri: Uri

    private var arrayListDoc = ArrayList<MyLibrary2Model>()

    private lateinit var recyclerView: RecyclerView
    lateinit var docAdapter: Library2Adapter

    lateinit var fileNotFoundIV: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_library_2)

        val folderName1 = intent.getStringExtra("folderName1").toString()
        val folderName2 = intent.getStringExtra("folderName2").toString()
        val userId = intent.getStringExtra("uid").toString()

        Log.i("ironman", folderName1+folderName2+userId)


        fileNotFoundIV = findViewById(R.id.fileNotFoundIV)

        searchView = findViewById(R.id.searchViewLibrary2)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchView.clearFocus()
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText)
                return true
            }

        })

        toolBarLibraryDetail = findViewById(R.id.folderToolBarLibrary2)

        toolBarLibraryDetail.setNavigationOnClickListener {
            finish()
        }



        toolBarLibraryDetail.title = folderName2


        recyclerView = findViewById(R.id.docRVLib2)
        docAdapter = Library2Adapter(this, arrayListDoc)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = docAdapter

        Log.i("keyNvalue", userId)


        try {
            FirebaseDatabase.getInstance().reference.child("Library")
                .child(userId!!)
                .child(folderName1)
                .child(folderName2)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        arrayListDoc.clear()
                        for (item in snapshot.children) {
                            var name = item.key
                            var uri = item.value

                            var count = 0
                            for (i in item.children) {
                                Log.i("abcdefgh", i.value.toString())
                                count++
                            }

                            if (count > 0 || item.value.toString() == "folderTrue") {

                                arrayListDoc.add(MyLibrary2Model(userId,"","",folderName1,folderName2,item.key.toString()))
                                docAdapter.notifyDataSetChanged()
                            } else {
                                arrayListDoc.add(MyLibrary2Model(userId,item.key.toString(),item.value.toString(),folderName1,folderName2,""))
                                docAdapter.notifyDataSetChanged()
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
        var filteredList = ArrayList<MyLibrary2Model>()
        for (item in arrayListDoc) {
            if (text != null) {
                if (item.pdfName.toLowerCase().contains(text.toLowerCase()) || item.folderName3.toLowerCase().contains(text.toLowerCase())) {
                    filteredList.add(item)
                    fileNotFoundIV.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                }
            }
        }

        if (filteredList.isEmpty()) {
            fileNotFoundIV.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            docAdapter.setFilteredList(filteredList)
        }
    }
}