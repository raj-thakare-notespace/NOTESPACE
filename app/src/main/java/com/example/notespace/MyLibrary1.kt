package com.example.notespace

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import androidx.appcompat.widget.SearchView
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notespace.adapters.MyLibrary1Adapter
import com.example.notespace.models.MyLibrary1Model
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class MyLibrary1 : AppCompatActivity() {

    lateinit var addDocButton: MaterialButton

    val CHOOSE_PDF_FROM_DEVICE = 2307


    lateinit var folderDetailToolbar: MaterialToolbar

    lateinit var progressDialog: ProgressDialog

    lateinit var dialog: AlertDialog

    private var folderNameList : ArrayList<String> = ArrayList()

    val storage = Firebase.storage

    lateinit var searchView: SearchView


    lateinit var uri: Uri

//    lateinit var noItemFoundTV : TextView
    private lateinit var fileNotFoundIV : ImageView

    private var arrayListDoc = ArrayList<MyLibrary1Model>()

    lateinit var recyclerView: RecyclerView
    lateinit var myLibrary1Adapter: MyLibrary1Adapter
    var uid = ""


    override fun onDestroy() {
        super.onDestroy()
        MyLibrary2().finish()
        MyLibrary3().finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_library_1)

        uid = intent.getStringExtra("uid").toString()


        folderDetailToolbar = findViewById(R.id.folderToolBar)


        searchView = findViewById(R.id.searchViewLibraryFolderDetail)

        val previousFolderName = intent.getStringExtra("folderName").toString()


        folderDetailToolbar.title = previousFolderName

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

        try {// To get names of the folders from the database
            FirebaseDatabase.getInstance().reference.child("Library").child(uid)
                .child(previousFolderName).addValueEventListener(object : ValueEventListener{
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

        fileNotFoundIV = findViewById(R.id.fileNotFoundIV)

        progressDialog = ProgressDialog(this)
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.isIndeterminate = true


        folderDetailToolbar.setNavigationOnClickListener {
            finish()
            MyLibrary2().finish()
            MyLibrary3().finish()
        }

        folderDetailToolbar.setOnMenuItemClickListener {
            when(it.itemId){

                R.id.createFolderIcon -> {
                    dialog.show()
                    true
                }

                else -> false
            }
        }

        addDocButton = findViewById(R.id.addDocumentButton)

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

                    tempName = tempName.replace(".","_")
                    tempName = tempName.replace("$","_dol_")
                    tempName = tempName.replace("[","(")
                    tempName = tempName.replace("]",")")
                    tempName = tempName.replace("#","_hash_")
                    tempName = tempName.replace("/","_")

                    try {
                        FirebaseDatabase.getInstance().reference
                            .child("Library")
                            .child(uid)
                            .child(previousFolderName)
                            .child(tempName)
                            .setValue("folderTrue").addOnCompleteListener {
                                if(it.isSuccessful){
                                    Toast.makeText(this,"Folder created",Toast.LENGTH_SHORT).show()

                                }
                            }
                    } catch (e: Exception) {
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



        Log.i("uuu", uid.toString())

        recyclerView = findViewById(R.id.docRV)
        myLibrary1Adapter = MyLibrary1Adapter(this, arrayListDoc)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = myLibrary1Adapter

        Log.i("keyNvalue", folderDetailToolbar.title.toString())


        try {
            FirebaseDatabase.getInstance().reference.child("Library")
                .child(uid)
                .child(previousFolderName)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists()){
                            arrayListDoc.clear()
                            for (item in snapshot.children) {
                                var name = item.key
                                var uri = item.value
                                var count = 0
                                for(i in item.children){
                                    Log.i("abcdefgh",i.value.toString())
                                    count++
                                }
                                Log.i("keyNvalue", count.toString())
                                if(count > 0 || item.value.toString() == "folderTrue"){

                                    arrayListDoc.add(MyLibrary1Model(uid,"","",previousFolderName,name.toString()))
                                    myLibrary1Adapter.notifyDataSetChanged()
                                }
                                else{
                                    arrayListDoc.add(MyLibrary1Model(uid,name.toString(),uri.toString(),previousFolderName,""))
                                    myLibrary1Adapter.notifyDataSetChanged()
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

                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                intent.type = "application/pdf"
                startActivityForResult(intent, CHOOSE_PDF_FROM_DEVICE)
            }
            else{
                takePermission(this)
            }

        }


    }

    private fun filterList(text: String?) {
        var filteredList = java.util.ArrayList<MyLibrary1Model>()
        for(item in arrayListDoc){
            if (text != null) {
                if(item.pdfName!!.toLowerCase().contains(text.toLowerCase()) || item.folderName2!!.toLowerCase().contains(text.toLowerCase())){
                    filteredList.add(item)
                    fileNotFoundIV.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                }
            }
        }

        if(filteredList.isEmpty()){
            fileNotFoundIV.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
            Toast.makeText(this,"No data found", Toast.LENGTH_SHORT).show()
        }
        else{
            myLibrary1Adapter.setFilteredList(filteredList)
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

                val reference = storage.reference.child("library")
                    .child(uid)
                    .child(folderDetailToolbar.title.toString())
                    .child(filename.toString())

                try {
                    reference.putFile(uri).addOnSuccessListener {
                        if (it.task.isSuccessful) {
                            reference.downloadUrl.addOnSuccessListener {
                                val uri = it.toString()
                                Firebase.database.reference.child("Library")
                                    .child(uid)
                                    .child(folderDetailToolbar.title.toString())
                                    .child(filename.toString())
                                    .setValue(it.toString()).addOnCompleteListener {
                                        if (it.isSuccessful) {
                                            progressDialog.dismiss()
                                            myLibrary1Adapter.notifyDataSetChanged()
                                            Toast.makeText(this, "Posted Successfully", Toast.LENGTH_SHORT).show()

                                            Firebase.database.reference.child("libraryOfPdfUrls")
                                                .child(uid)
                                                .child(folderDetailToolbar.title.toString())
                                                .child(filename.toString()).setValue(uri)
                                        }
                                        if (it.isCanceled) {
                                            progressDialog.dismiss()
                                            Toast.makeText(this, "Posting failed", Toast.LENGTH_SHORT).show()
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