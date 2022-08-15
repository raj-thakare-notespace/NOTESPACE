package com.startup.notespace

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.startup.notespace.adapters.GroupLibraryAdapter
import com.startup.notespace.adapters.GroupLibraryAdapterOther
import com.startup.notespace.models.MyLibraryModel

class GroupLibraryOther : AppCompatActivity() {

    lateinit var foldersRV: RecyclerView
    lateinit var libraryAdapter: GroupLibraryAdapterOther
    lateinit var dialog: AlertDialog

    lateinit var searchView: SearchView


    var arrayList = ArrayList<MyLibraryModel>()

    var folder_name = ""

    private lateinit var fileNotFoundIV : ImageView

    private lateinit var toolbar: MaterialToolbar

    private var folderNameList : ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_library_other)

        toolbar = findViewById(R.id.toolBarGroupLibrary)

        toolbar.setNavigationOnClickListener {
            finish()
        }

        fileNotFoundIV = findViewById(R.id.fileNotFoundIV)

        searchView = findViewById(R.id.searchViewGroupLibrary)


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


        val uid = intent.getStringExtra("uid")
        val createdBy = intent.getStringExtra("createdBy")

        // To get names of the folders from the database
        try {
            FirebaseDatabase.getInstance().reference.child("Library")
                .child(uid!!.toString()).addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists()){
                            folderNameList.clear()
                            for(item in snapshot.children){
                                folderNameList.add(item.key.toString())
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
        } catch (e: Exception) {
        }

        foldersRV = findViewById(R.id.foldersRVGP)




        libraryAdapter = GroupLibraryAdapterOther(this, arrayList)

        foldersRV.layoutManager = LinearLayoutManager(this)
        foldersRV.adapter = libraryAdapter

        try {
            FirebaseDatabase.getInstance().reference.child("Library")
                .child(uid!!)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists()){
                            arrayList.clear()
                            for (item in snapshot.children) {
                                var folderName = item.key
                                arrayList.add(MyLibraryModel(uid,folderName!!))
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
        for(item in arrayList){
            if (text != null) {
                if(item.folderName1.toLowerCase().contains(text.toLowerCase())){
                    filteredList.add(item)
                    fileNotFoundIV.visibility = View.GONE
                    foldersRV.visibility = View.VISIBLE
                }
            }
        }

        if(filteredList.isEmpty()){
            fileNotFoundIV.visibility = View.VISIBLE
            foldersRV.visibility = View.GONE
            Toast.makeText(this,"No data found", Toast.LENGTH_SHORT).show()
        }
        else{
            libraryAdapter.setFilteredList(filteredList)
        }
    }

}