package com.startup.notespace

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.startup.notespace.adapters.Library1Adapter
import com.startup.notespace.models.MyLibrary1Model
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.util.ArrayList

class Library1 : AppCompatActivity() {


    lateinit var toolBarLibraryDetail : MaterialToolbar

    lateinit var searchView: SearchView


    private var arrayListDoc = ArrayList<MyLibrary1Model>()

    lateinit var recyclerView: RecyclerView
    lateinit var docAdapter: Library1Adapter

    lateinit var fileNotFoundIV : ImageView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_library_1)

        val folder1 = intent.getStringExtra("folderName").toString()
        val userId = intent.getStringExtra("uid").toString()


        fileNotFoundIV = findViewById(R.id.fileNotFoundIV)

        searchView = findViewById(R.id.searchViewLibraryDetail)

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

        toolBarLibraryDetail = findViewById(R.id.folderToolBarLibraryDetail)

        toolBarLibraryDetail.setNavigationOnClickListener {
            this.finish()
        }



        toolBarLibraryDetail.title = folder1


        recyclerView = findViewById(R.id.docRVLibDetail)
        docAdapter = Library1Adapter(this,arrayListDoc)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = docAdapter



        try {
            FirebaseDatabase.getInstance().reference.child("Library")
                .child(userId!!)
                .child(folder1)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists()){
                            arrayListDoc.clear()
                            for(item in snapshot.children){
                                var name = item.key
                                var uri = item.value

                                var count = 0
                                for(i in item.children){
                                    count++
                                }

                                if(count > 0 || item.value.toString() == "folderTrue"){

                                    arrayListDoc.add(MyLibrary1Model(userId,"","",folder1,item.key.toString()))
                                    docAdapter.notifyDataSetChanged()
                                }
                                else{
                                    arrayListDoc.add(MyLibrary1Model(userId,name.toString(),uri.toString(),folder1,""))
                                    docAdapter.notifyDataSetChanged()
                                }
                            }
                        }

                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
        } catch (e: Exception) {
            Log.i("island",e.toString())
        }

    }

    private fun filterList(text: String?) {
        var filteredList = ArrayList<MyLibrary1Model>()
        for(item in arrayListDoc){
            if (text != null) {
                if(item.pdfName.toLowerCase().contains(text.toLowerCase()) || item.folderName2.toLowerCase().contains(text.toLowerCase())){
                    filteredList.add(item)
                    fileNotFoundIV.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                }
            }
        }

        if(filteredList.isEmpty()){
            fileNotFoundIV.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        }
        else{
            docAdapter.setFilteredList(filteredList)
        }
    }
}