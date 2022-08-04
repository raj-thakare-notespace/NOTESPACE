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
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.OpenableColumns
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.startup.notespace.adapters.MyLibrary3Adapter
import com.startup.notespace.models.MyLibrary3Model
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class MyLibrary3 : AppCompatActivity() {

    lateinit var addDocButton: MaterialButton
    val CHOOSE_PDF_FROM_DEVICE = 2307


    lateinit var folderDetailToolbar: MaterialToolbar

    lateinit var progressDialog: ProgressDialog


    val storage = Firebase.storage

    lateinit var searchView: SearchView


    lateinit var uri: Uri

    private lateinit var fileNotFoundIV : ImageView

    private var arrayListDoc = ArrayList<MyLibrary3Model>()

    lateinit var recyclerView: RecyclerView
    lateinit var myLibraryAdapter: MyLibrary3Adapter
    var uid = ""

    lateinit var folderName1 : String
    lateinit var folderName2 : String
    lateinit var folderName3 : String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_library_3)

        folderName1 = intent.getStringExtra("folderName1").toString()
        folderName2 = intent.getStringExtra("folderName2").toString()
        folderName3 = intent.getStringExtra("folderName3").toString()
        uid = intent.getStringExtra("uid").toString()



        folderDetailToolbar = findViewById(R.id.folderToolBarSecond3)

        folderDetailToolbar.title = folderName3


        searchView = findViewById(R.id.searchViewLibraryFolderDetailSecond3)

        folderDetailToolbar.setOnMenuItemClickListener {
            when(it.itemId){

                R.id.createFolderIcon -> {
                    Toast.makeText(this,"Hi there", Toast.LENGTH_SHORT).show()
                    true
                }

                else -> false
            }
        }



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


        fileNotFoundIV = findViewById(R.id.fileNotFoundIV)

        progressDialog = ProgressDialog(this)
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.isIndeterminate = true


        folderDetailToolbar.setNavigationOnClickListener {
            finish()
        }


        addDocButton = findViewById(R.id.addDocumentButtonSecond3)


        recyclerView = findViewById(R.id.docRVSecond3)
        myLibraryAdapter = MyLibrary3Adapter(this, arrayListDoc)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = myLibraryAdapter



        try {
            FirebaseDatabase.getInstance().reference.child("Library")
                .child(uid)
                .child(folderName1)
                .child(folderName2)
                .child(folderName3)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists()){
                            arrayListDoc.clear()
                            for (item in snapshot.children) {
                                var name = item.key
                                var uri = item.value
                                Log.i("keyNvalue", name + uri)
                                if(item.value == "folderTrue"){
                                    continue
                                }
                                else{
                                    arrayListDoc.add(MyLibrary3Model(uid,name.toString(),uri.toString(),folderName1,folderName2,folderName3))
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
            if(isPermissionGranted(this)){
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
            else{
                takePermission(this)
            }
        }


    }

    private fun filterList(text: String?) {
        var filteredList = java.util.ArrayList<MyLibrary3Model>()
        for(item in arrayListDoc){
            if (text != null) {
                if(item.pdfName!!.toLowerCase().contains(text.toLowerCase())){
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
            myLibraryAdapter.setFilteredList(filteredList)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CHOOSE_PDF_FROM_DEVICE && resultCode == RESULT_OK) {
            if (data != null) {
                Log.d("SOME", "onActivityResult: " + data.data)

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
                        .child(folderName3)
                        .child(filename.toString())


                    reference.putFile(uri).addOnSuccessListener {
                        if (it.task.isSuccessful) {
                            reference.downloadUrl.addOnSuccessListener {
                                val uri = it.toString()
                                Firebase.database.reference.child("Library")
                                    .child(uid)
                                    .child(folderName1)
                                    .child(folderName2)
                                    .child(folderName3)
                                    .child(filename.toString())
                                    .setValue(it.toString()).addOnCompleteListener {
                                        if (it.isSuccessful) {
                                            progressDialog.dismiss()
                                            myLibraryAdapter.notifyDataSetChanged()
                                            Toast.makeText(this, "Posted Successfully",Toast.LENGTH_SHORT).show()

                                            Firebase.database.reference.child("libraryOfPdfUrls")
                                                .child(uid)
                                                .child(folderName1)
                                                .child(filename.toString()).setValue(uri)


                                            Firebase.database.reference.child("libraryOfPdfUrls")
                                                .child(uid)
                                                .child(folderName1)
                                                .child(folderName2)
                                                .child(filename.toString()).setValue(uri)

                                            Firebase.database.reference.child("libraryOfPdfUrls")
                                                .child(uid)
                                                .child(folderName1)
                                                .child(folderName2)
                                                .child(folderName3)
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

        // To manage permissions
        if(requestCode == RESULT_OK){
            if(requestCode == 100){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    // For android 11
                    if(Environment.isExternalStorageManager()){
                        Toast.makeText(this,"Permission Granted in android 11",Toast.LENGTH_SHORT).show()
                    }
                    else{
                        takePermission(this)
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == 101){
            if(grantResults.isNotEmpty()){
                var readExternalStorage : Boolean = grantResults[0] == PackageManager.PERMISSION_GRANTED
                if(readExternalStorage){
                    Toast.makeText(this,"Read permission granted in android 10 or below",Toast.LENGTH_SHORT).show()
                }
                else{
                    takePermission(this)
                }
            }
        }

    }

    private fun isPermissionGranted(context: Context) : Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // For android 11
            return Environment.isExternalStorageManager()
        }
        else{
            // For below
            val  readExternalStorageManager = ContextCompat.checkSelfPermission(context,android.Manifest.permission.READ_EXTERNAL_STORAGE)
            return readExternalStorageManager == PackageManager.PERMISSION_GRANTED
        }
    }


    private fun takePermission(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // For android 11

            try {
                var intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.addCategory("android.intent.category.DEFAULT")
                intent.data = Uri.parse(String.format("package:%s",context.packageName))
                startActivityForResult(intent,100)
            }
            catch (e : Exception){
                var intent = Intent()
                intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                startActivityForResult(intent,100)
            }

        }
        else{
            // For below versions
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),101)

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