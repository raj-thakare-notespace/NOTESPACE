package com.startup.notespace

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.SearchView
import com.startup.notespace.adapters.GroupLibraryAdapter
import com.startup.notespace.models.MyLibraryModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class GroupLibrary : AppCompatActivity() {

    lateinit var foldersRV: RecyclerView
    lateinit var libraryAdapter: GroupLibraryAdapter
    lateinit var dialog: AlertDialog
    lateinit var addFolderButton: MaterialButton

    lateinit var searchView: SearchView


    var arrayList = ArrayList<MyLibraryModel>()

    var folder_name = ""

    private lateinit var fileNotFoundIV : ImageView

    private lateinit var toolbar: MaterialToolbar

    private var folderNameList : ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_library)

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
                .child(uid!!.toString()).addValueEventListener(object : ValueEventListener{
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

        addFolderButton = findViewById(R.id.addFolderButtonGP)
        foldersRV = findViewById(R.id.foldersRVGP)

        if (createdBy != Firebase.auth.currentUser!!.uid) {
            addFolderButton.visibility = View.GONE
        }


        val builder = MaterialAlertDialogBuilder(this)
        builder.setTitle("Create folder")

        var view = layoutInflater.inflate(R.layout.alert_dialog, null)
        val eName = view.findViewById<EditText>(R.id.folderNameET)
        val okButton = view.findViewById<Button>(R.id.okButton)
        val cancelButton = view.findViewById<Button>(R.id.cancelButton)
        okButton.setOnClickListener {
            if (eName.text.isNotEmpty()) {
                folder_name = eName.text.toString()

                folder_name = folder_name.replace(".","_")
                folder_name = folder_name.replace("$","_dol_")
                folder_name = folder_name.replace("[","(")
                folder_name = folder_name.replace("]",")")
                folder_name = folder_name.replace("#","_hash_")
                folder_name = folder_name.replace("/","_")

                libraryAdapter.notifyDataSetChanged()
                dialog.dismiss()
                if (folder_name.isNotEmpty()) {

                    if(folderNameList.contains(folder_name)){
                        Toast.makeText(view.context, "Folder name already exists.", Toast.LENGTH_SHORT).show()
                    }
                    else{
                        FirebaseDatabase.getInstance().reference.child("Library")
                            .child(uid!!.toString())
                            .child(folder_name).setValue("").addOnCompleteListener {
                                if (it.isSuccessful) {
                                    Toast.makeText(view.context, "$folder_name created.", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(view.context, "Failed", Toast.LENGTH_SHORT).show()
                                }
                            }
                    }

                }
                eName.setText("")
            }
        }
        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        builder.setView(view)
        dialog = builder.create()

        libraryAdapter = GroupLibraryAdapter(this, arrayList)

        foldersRV.layoutManager = LinearLayoutManager(view.context)
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

        addFolderButton.setOnClickListener {
            dialog.show()

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
            Toast.makeText(this,"No data found",Toast.LENGTH_SHORT).show()
        }
        else{
            libraryAdapter.setFilteredList(filteredList)
        }
    }

}