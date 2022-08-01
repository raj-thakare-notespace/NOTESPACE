package com.example.notespace

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.widget.SearchView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notespace.adapters.MyLibraryAdapter
import com.example.notespace.models.MyLibraryModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class MyLibraryActivity : AppCompatActivity() {


    lateinit var addFolderButton: MaterialButton

    lateinit var foldersRV : RecyclerView

    lateinit var myLibraryAdapter: MyLibraryAdapter

    lateinit var dialog : AlertDialog

    var arrayList = ArrayList<MyLibraryModel>()

    var folder_name = ""

    lateinit var searchView: SearchView

//    lateinit var noItemNoteTV : TextView

    private lateinit var toolbar: MaterialToolbar

    private var folderNameList : ArrayList<String> = ArrayList()

    private lateinit var fileNotFoundIV : ImageView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_library)


        toolbar = findViewById(R.id.toolBarMyLibrary)

        toolbar.setNavigationOnClickListener {
            finish()
        }

        val uid = Firebase.auth.currentUser!!.uid.toString()

        fileNotFoundIV = findViewById(R.id.fileNotFoundIV)

        searchView = findViewById(R.id.searchViewMyLibrary)
        searchView.queryHint = "Search here"

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

        // To get names of the folders from the database
        FirebaseDatabase.getInstance().reference.child("Library")
            .child(Firebase.auth.currentUser!!.uid).addValueEventListener(object : ValueEventListener{
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


        addFolderButton = findViewById(R.id.addFolderButton)
        foldersRV = findViewById(R.id.foldersRV)

        myLibraryAdapter = MyLibraryAdapter(this, arrayList)

        foldersRV.layoutManager = LinearLayoutManager(this)
        foldersRV.adapter = myLibraryAdapter

        val builder = MaterialAlertDialogBuilder(this)
        builder.setTitle("Create Folder")

        var view = layoutInflater.inflate(R.layout.alert_dialog,null)
        val eName = view.findViewById<EditText>(R.id.folderNameET)
        val okButton = view.findViewById<Button>(R.id.okButton)
        val cancelButton = view.findViewById<Button>(R.id.cancelButton)

        okButton.setOnClickListener {
            Log.i("folderNameList",folderNameList.toString())
            if(eName.text.isNotEmpty()){
                folder_name = eName.text.toString()

                folder_name = folder_name.replace(".","_")
                folder_name = folder_name.replace("$","_dol_")
                folder_name = folder_name.replace("[","(")
                folder_name = folder_name.replace("]",")")
                folder_name = folder_name.replace("#","_hash_")
                folder_name = folder_name.replace("/","_")

                Log.i("eName",folder_name)
                if (folder_name.isNotEmpty()) {

                    if(folderNameList.contains(folder_name)){
                        Toast.makeText(view.context, "Folder name already exists.", Toast.LENGTH_SHORT).show()
                        eName.setText("")
                    }
                    else{
                        FirebaseDatabase.getInstance().reference.child("Library")
                            .child(Firebase.auth.currentUser!!.uid)
                            .child(folder_name).setValue("").addOnCompleteListener {
                                if (it.isSuccessful) {
                                    Toast.makeText(view.context, "Success", Toast.LENGTH_SHORT).show()
                                    eName.setText("")
                                } else {
                                    Toast.makeText(view.context, "Failed", Toast.LENGTH_SHORT).show()
                                }
                            }
                    }
                }
                dialog.dismiss()
            }
        }
        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        builder.setView(view)
        dialog = builder.create()


        FirebaseDatabase.getInstance().reference.child("Library")
            .child(Firebase.auth.currentUser!!.uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        arrayList.clear()
                        for (item in snapshot.children) {
                            var folderName = item.key
                            Log.i("okurrr", folderName.toString())
                            arrayList.add(MyLibraryModel(uid,folderName.toString()))
                            myLibraryAdapter.notifyDataSetChanged()
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

        addFolderButton.setOnClickListener {
            dialog.show()
        }

    }


    private fun filterList(text: String?) {
        var filteredList = ArrayList<MyLibraryModel>()
        for(item in arrayList){
            if (text != null) {
                if(item.folderName1.toLowerCase().contains(text.toLowerCase())){
                    filteredList.add(MyLibraryModel(Firebase.auth.currentUser!!.uid,item.folderName1))
                    fileNotFoundIV.visibility = View.GONE
                    foldersRV.visibility = View.VISIBLE
                }
            }
        }

        if(filteredList.isEmpty()){
            fileNotFoundIV.visibility = View.VISIBLE
            foldersRV.visibility = View.GONE
        }
        else{
            myLibraryAdapter.setFilteredList(filteredList)
        }
    }

}