package com.startup.notespace

import android.app.ProgressDialog
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.startup.notespace.adapters.Library3Adapter
import com.startup.notespace.models.MyLibrary3Model
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class Library3 : AppCompatActivity() {
    val CHOOSE_PDF_FROM_DEVICE = 2307


    lateinit var folderDetailToolbar: MaterialToolbar

    lateinit var progressDialog: ProgressDialog


    val storage = Firebase.storage

    lateinit var searchView: SearchView


    lateinit var uri: Uri

//    lateinit var noItemFoundTV: TextView
    lateinit var fileNotFoundIV : ImageView

    private var arrayListDoc = ArrayList<MyLibrary3Model>()

    lateinit var recyclerView: RecyclerView
    lateinit var docAdapter: Library3Adapter
    var userId = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_library_3)

        val folderName1 = intent.getStringExtra("folderName1").toString()
        val folderName2 = intent.getStringExtra("folderName2").toString()
        val folderName3 = intent.getStringExtra("folderName3").toString()
        userId = intent.getStringExtra("uid").toString()

        Log.i("ironman", folderName1+folderName2+folderName3+userId)




        folderDetailToolbar = findViewById(R.id.folderToolBarSecond3)

        folderDetailToolbar.title = folderName3


        searchView = findViewById(R.id.searchViewLibraryFolderDetailSecond3)

        fileNotFoundIV = findViewById(R.id.fileNotFoundIV)


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


        progressDialog = ProgressDialog(this)
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.isIndeterminate = true


        folderDetailToolbar.setNavigationOnClickListener {
            finish()
        }


//        if (!userId.isNullOrEmpty() && !userId.isNullOrBlank() && !userId.isEmpty() && !userId.isBlank()) {
//            uid = userId
//        }


        recyclerView = findViewById(R.id.docRVSecond3)
        docAdapter = Library3Adapter(this, arrayListDoc)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = docAdapter

        Log.i("keyNvalue", folderName3)
        Log.i("keyNvalue", folderDetailToolbar.title.toString())


        try {
            FirebaseDatabase.getInstance().reference.child("Library")
                .child(userId)
                .child(folderName1)
                .child(folderName2)
                .child(folderName3)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (item in snapshot.children) {
                            var name = item.key
                            var uri = item.value



                            arrayListDoc.add(MyLibrary3Model(userId,name.toString(),uri.toString(),folderName1,folderName2,folderName3))
                            docAdapter.notifyDataSetChanged()

    //                        Log.i("keyNvalue", name + uri)
    //                        if (item.value == "folderTrue") {
    //                            continue
    //                        } else {
    //
    //                            arrayListDoc.add(MyLibrary3Model(uid,name.toString(),uri.toString(),folderName1,folderName2,folderName3))
    //                            docAdapter.notifyDataSetChanged()
    //
    //                        }

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
        var filteredList = java.util.ArrayList<MyLibrary3Model>()
        for (item in arrayListDoc) {
            if (text != null) {
                if (item.pdfName!!.toLowerCase().contains(text.toLowerCase())) {
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