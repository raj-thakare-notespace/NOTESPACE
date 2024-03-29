package com.startup.notespace

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.startup.notespace.adapters.LibraryAdapter
import com.startup.notespace.models.MyLibraryModel

class LibraryActivity : AppCompatActivity() {

    lateinit var foldersRV: RecyclerView

    lateinit var fileNotFoundIV: ImageView

    lateinit var libraryAdapter: LibraryAdapter

    var arrayList = ArrayList<MyLibraryModel>()

    private lateinit var toolbar: MaterialToolbar

    lateinit var searchView: SearchView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_library)

        toolbar = findViewById(R.id.toolBarLibraryOther)

        toolbar.setNavigationOnClickListener {
            this.finish()
        }

        fileNotFoundIV = findViewById(R.id.fileNotFoundIV)


        searchView = findViewById(R.id.searchViewLibrary)

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

        val uid = intent.getStringExtra("uid")

        foldersRV = findViewById(R.id.foldersRVLibrary)
        libraryAdapter = LibraryAdapter(this, arrayList)

        foldersRV.layoutManager = LinearLayoutManager(this)
        foldersRV.adapter = libraryAdapter

        Log.i("userIddd", uid.toString())

        try {
            FirebaseDatabase.getInstance().reference.child("Library")
                .child(uid!!.toString())
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            arrayList.clear()
                            for (item in snapshot.children) {
                                var folderName = item.key
                                Log.i("android", folderName.toString())
                                arrayList.add(MyLibraryModel(uid.toString(), folderName.toString()))
                                libraryAdapter.notifyDataSetChanged()
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
        var filteredList = ArrayList<MyLibraryModel>()
        for (item in arrayList) {
            if (text != null) {
                if (item.folderName1.toLowerCase().contains(text.toLowerCase())) {
                    filteredList.add(item)
//                    noItemFoundTV.visibility = View.GONE
                    fileNotFoundIV.visibility = View.GONE
                    foldersRV.visibility = View.VISIBLE
                }
            }
        }

        if (filteredList.isEmpty()) {
//            noItemFoundTV.visibility = View.VISIBLE
            fileNotFoundIV.visibility = View.VISIBLE
            foldersRV.visibility = View.GONE
        } else {
            libraryAdapter.setFilteredList(filteredList)
        }
    }
}