package com.startup.notespace

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.OpenableColumns
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.startup.notespace.adapters.MyLibrary2Adapter
import com.startup.notespace.models.MyLibrary2Model
import com.startup.notespace.usefulClasses.FileNameErrorCorrector
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class MyLibrary2 : AppCompatActivity() {

    lateinit var addDocButton: MaterialButton
    val CHOOSE_PDF_FROM_DEVICE = 2307

    lateinit var dialog: AlertDialog

    lateinit var folderDetailToolbar: MaterialToolbar

    lateinit var progressDialog: ProgressDialog


    val storage = Firebase.storage

    lateinit var searchView: SearchView


    lateinit var uri: Uri

    private lateinit var fileNotFoundIV : ImageView

    private var arrayListDoc = ArrayList<MyLibrary2Model>()

    lateinit var recyclerView: RecyclerView
    lateinit var myLibraryAdapter: MyLibrary2Adapter
    lateinit var folderName1: String
    lateinit var folderName2: String

    var uid = ""

    private var folderNameList : ArrayList<String> = ArrayList()

    override fun onDestroy() {
        super.onDestroy()
        MyLibrary3().finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_library_2)

        folderName1 = intent.getStringExtra("folderName1").toString()
        folderName2 = intent.getStringExtra("folderName2").toString()
        uid  = intent.getStringExtra("uid").toString()



        folderDetailToolbar = findViewById(R.id.folderToolBarSecond2)

        folderDetailToolbar.title = folderName2

        folderDetailToolbar.setNavigationOnClickListener {
            finish()
            MyLibrary3().finish()
        }

        folderDetailToolbar.setOnMenuItemClickListener {
            when (it.itemId) {

                R.id.createFolderIcon -> {
                    dialog.show()
                    true
                }

                else -> false
            }
        }


        searchView = findViewById(R.id.searchViewLibraryFolderDetailSecond2)

        try {// To get names of the folders from the database
            FirebaseDatabase.getInstance().reference.child("Library").child(uid).child(folderName1)
                .child(folderName2).addValueEventListener(object : ValueEventListener{
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

        val builder = MaterialAlertDialogBuilder(this)
        builder.setTitle("Create folder")

        var view = layoutInflater.inflate(R.layout.alert_dialog, null)
        val eName = view.findViewById<EditText>(R.id.folderNameET)
        val okButton = view.findViewById<Button>(R.id.okButton)
        val cancelButton = view.findViewById<Button>(R.id.cancelButton)
        okButton.setOnClickListener {
            if (eName.text.isNotEmpty()) {

                if(folderNameList.contains(eName.text.toString())){
                    Toast.makeText(view.context, "Folder name already exists.", Toast.LENGTH_SHORT).show()
                }
                else{
                    var tempName = eName.text.toString()
                    tempName = FileNameErrorCorrector().correctErrors(tempName)

                    tempName = tempName.replace(".","_")
                    tempName = tempName.replace("$","_dol_")
                    tempName = tempName.replace("[","(")
                    tempName = tempName.replace("]",")")
                    tempName = tempName.replace("#","_hash_")
                    tempName = tempName.replace("/","_")

                    FirebaseDatabase.getInstance().reference
                        .child("Library")
                        .child(uid)
                        .child(folderName1)
                        .child(folderName2)
                        .child(tempName)
                        .setValue("folderTrue").addOnCompleteListener {
                            if (it.isSuccessful) {
                                Toast.makeText(this, "Folder created", Toast.LENGTH_SHORT).show()

                            }
                        }
                }
                dialog.dismiss()
                eName.setText("")
            }
        }
        cancelButton.setOnClickListener {
            dialog.dismiss()
            eName.setText("")
        }

        builder.setView(view)
        dialog = builder.create()


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

        fileNotFoundIV = findViewById(R.id.fileNotFoundIV)

        progressDialog = ProgressDialog(this)
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.isIndeterminate = true


        folderDetailToolbar.setNavigationOnClickListener {
            finish()
        }


        addDocButton = findViewById(R.id.addDocumentButtonSecond2)


        recyclerView = findViewById(R.id.docRVSecond2)
        myLibraryAdapter = MyLibrary2Adapter(this, arrayListDoc)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = myLibraryAdapter


        try {
            FirebaseDatabase.getInstance().reference.child("Library")
                .child(uid)
                .child(folderName1)
                .child(folderName2)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists()){
                            arrayListDoc.clear()
                            for (item in snapshot.children) {
                                var name = item.key
                                var uri = item.value
                                var count=0
                                for(i in item.children){
                                    Log.i("abcdefgh",i.value.toString())
                                    count++
                                }
                                if(count > 0 || item.value.toString() == "folderTrue"){
                                    arrayListDoc.add(MyLibrary2Model(uid, "", "", folderName1, folderName2, item.key.toString()))
                                    myLibraryAdapter.notifyDataSetChanged()
                                }
                                else{
                                    arrayListDoc.add(MyLibrary2Model(uid, item.key.toString(), item.value.toString(), folderName1, folderName2, ""))
                                    myLibraryAdapter.notifyDataSetChanged()
                                }

                            }
                        }

                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
        } catch (e: Exception) {
        }


        addDocButton.setOnClickListener {

                try {
                    progressDialog.show()
                }
                catch (e : Exception){
                    progressDialog.dismiss()
                }
                progressDialog.setContentView(R.layout.progress_bar_layout)
                progressDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

                try {
                    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                    intent.addCategory(Intent.CATEGORY_OPENABLE)
                    intent.setType("application/pdf")
                    startActivityForResult(intent, CHOOSE_PDF_FROM_DEVICE)
                } catch (e: Exception) {
                }
        }


    }

    private fun filterList(text: String?) {
        var filteredList = java.util.ArrayList<MyLibrary2Model>()
        for (item in arrayListDoc) {
            if (text != null) {
                if (item.pdfName!!.toLowerCase().contains(text.toLowerCase()) || item.folderName3!!.toLowerCase().contains(text.toLowerCase())) {
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
            myLibraryAdapter.setFilteredList(filteredList)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CHOOSE_PDF_FROM_DEVICE && resultCode == RESULT_OK) {
            if (data != null) {


                var filename = data.data?.let { getFileName(it) }
                filename = filename!!.dropLast(4)
                uri = data.data!!

                filename = filename.replace(".","_")
                filename = filename.replace("$","_dol_")
                filename = filename.replace("[","(")
                filename = filename.replace("]",")")
                filename = filename.replace("#","_hash_")
                filename = filename.replace("/","_")


                try {
                    val reference = storage.reference.child("library")
                        .child(uid)
                        .child(folderName1)
                        .child(folderName2)
                        .child(filename.toString())


                    reference.putFile(uri).addOnSuccessListener {
                        if (it.task.isSuccessful) {
                            reference.downloadUrl.addOnSuccessListener {
                                val uri = it.toString()

                                Firebase.database.reference.child("Library")
                                    .child(uid)
                                    .child(folderName1)
                                    .child(folderName2)
                                    .child(filename.toString())
                                    .setValue(it.toString()).addOnCompleteListener {
                                        if (it.isSuccessful) {
                                            progressDialog.dismiss()
                                            myLibraryAdapter.notifyDataSetChanged()
                                            Toast.makeText(this, "Posted Successfully", Toast.LENGTH_SHORT).show()

                                            Firebase.database.reference.child("libraryOfPdfUrls")
                                                .child(uid)
                                                .child(folderName1)
                                                .child(filename.toString()).setValue(uri)

                                            Firebase.database.reference.child("libraryOfPdfUrls")
                                                .child(uid)
                                                .child(folderName1)
                                                .child(folderName2)
                                                .child(filename.toString()).setValue(uri)
                                        }
                                        if (it.isCanceled) {
                                            progressDialog.dismiss()
                                            Toast.makeText(
                                                this,
                                                "Posting failed",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }

                                    }
                            }
                        }


                    }
                } catch (e: Exception) {
                }
            }
        }

    }


    // Function to get PDF name
    @SuppressLint("Range")
    fun getFileName(uri: Uri): String? {
        var result: String? = null
        if (uri.getScheme().equals("content")) {
            val cursor: Cursor? = contentResolver.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } finally {
                if (cursor != null) {
                    cursor.close()
                }
            }
        }
        if (result == null) {
            result = uri.getPath()
            val cut = result?.lastIndexOf('/')
            if (cut != -1) {
                if (result != null) {
                    if (cut != null) {
                        result = result.substring(cut + 1)
                    }
                }
            }
        }
        return result
    }


}