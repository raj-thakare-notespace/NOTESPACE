package com.startup.notespace

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.startup.notespace.models.NoteModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.util.*

class CreateOnlineNoteActivity : AppCompatActivity() {

    val myRequestCode = 123
    var selectedColor = "#ffffff"
    var noteIsPrivate = false
    lateinit var noteImage: ImageView
    private var selectedImagePath: String = ""
    lateinit var noteTitleEdt: EditText
    lateinit var noteDescEdt: EditText
    lateinit var createNoteRL: RelativeLayout
    lateinit var createNoteToolbar: MaterialToolbar
    val REQUEST_CODE_STORAGE_PERMISSION = 1
    val REQUEST_CODE_SELECT_IMAGE = 2
    lateinit var switch: SwitchMaterial

    lateinit var allContainingRL: RelativeLayout

    lateinit var uri: Uri


    lateinit var progressBar: ProgressBar

    var noteImageUri = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_online_note)

        allContainingRL = findViewById(R.id.allContainingRL)

        progressBar = findViewById(R.id.createOnlineNoteProgressBar)

        var noteCode = intent.getStringExtra("noteCode")
        var noteTitleIntent = intent.getStringExtra("noteTitle")
        noteImageUri = intent.getStringExtra("noteImageUri").toString()
        var noteDesc = intent.getStringExtra("noteDesc").toString()

        val place = intent.getStringExtra("place").toString()

        uri = Uri.parse("")


        val uid = FirebaseAuth.getInstance().currentUser!!.uid

        var count = 1

        noteImage = findViewById(R.id.noteImageOnlineNote)
        noteTitleEdt = findViewById(R.id.idEdtNoteTitleOnlineNote)
        noteDescEdt = findViewById(R.id.idEdtNoteDescOnlineNote)
        createNoteRL = findViewById(R.id.createNoteRLOnlineNote)
        createNoteToolbar = findViewById(R.id.createNoteToolBarOnlineNote)
        switch = findViewById(R.id.switchPrivateNote)


        try {
            if (noteCode == "Edit") {
                try {
                    Firebase.database.reference.child("notes")
                        .child(uid)
                        .child(noteTitleIntent.toString())
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            @SuppressLint("Range")
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()){
                                    val model = snapshot.getValue(NoteModel::class.java)
                                    if (model!!.image.isNotEmpty() || model!!.image.isNullOrBlank()) {
                                        Glide.with(applicationContext)
                                            .load(model.image)
                                            .into(noteImage)
                                    }
                                    noteTitleEdt.setText(model.title)
                                    noteDescEdt.setText(model.description)
                                    createNoteRL.setBackgroundColor(Color.parseColor(model.color))
                                    switch.isChecked = model.isPrivate
                                    selectedColor = model.color
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                TODO("Not yet implemented")
                            }

                        })
                } catch (e: Exception) {
                }

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        

        switch.setOnCheckedChangeListener { _, isChecked ->
            noteIsPrivate = isChecked
        }

        createNoteToolbar.setNavigationOnClickListener {
            finish()
        }

        createNoteToolbar.setOnMenuItemClickListener {

            when (it.itemId) {

                // all okay here
                R.id.iconAddImage -> {

                    if(isPermissionGranted(this)){
                        selectImage()
                    }
                    else{
                        takePermission(this)
                    }


                    true
                }

                R.id.changeColor -> {

                    count++

                    if (count > 9) {
                        count = 1
                    }

                    when (count) {

                        1 -> {
                            createNoteRL.setBackgroundColor(
                                ContextCompat.getColor(
                                    this,
                                    R.color.white
                                )
                            )
                            selectedColor = "#ffffff"
                            true
                        }

                        2 -> {
                            createNoteRL.setBackgroundColor(
                                ContextCompat.getColor(
                                    this,
                                    R.color.ColorBlueNote
                                )
                            )
                            selectedColor = "#CCCCFF"
                            true
                        }

                        3 -> {
                            createNoteRL.setBackgroundColor(
                                ContextCompat.getColor(
                                    this,
                                    R.color.ColorYellowNote
                                )
                            )
                            selectedColor = "#FFFFCC"
                            true
                        }

                        4 -> {
                            createNoteRL.setBackgroundColor(
                                ContextCompat.getColor(
                                    this,
                                    R.color.ColorPurpleNote
                                )
                            )
                            selectedColor = "#E5CCFF"
                            true
                        }

                        5 -> {
                            createNoteRL.setBackgroundColor(
                                ContextCompat.getColor(
                                    this,
                                    R.color.ColorGreenNote
                                )
                            )
                            selectedColor = "#CCFFCC"
                            true
                        }

                        6 -> {
                            createNoteRL.setBackgroundColor(
                                ContextCompat.getColor(
                                    this,
                                    R.color.ColorOrangeNote
                                )
                            )
                            selectedColor = "#F3805C"
                            true
                        }

                        7 -> {
                            createNoteRL.setBackgroundColor(
                                ContextCompat.getColor(
                                    this,
                                    R.color.ColorBlackNote
                                )
                            )
                            selectedColor = "#A0A0A0"
                            true
                        }

                        8 -> {
                            createNoteRL.setBackgroundColor(
                                ContextCompat.getColor(
                                    this,
                                    R.color.ColorPinkNote
                                )
                            )
                            selectedColor = "#ff7eb9"
                            true
                        }

                        9 -> {
                            createNoteRL.setBackgroundColor(
                                ContextCompat.getColor(
                                    this,
                                    R.color.ColorSkyNote
                                )
                            )
                            selectedColor = "#9CF2F4"
                            true
                        }


                    }

                    true
                }
                R.id.iconSaveNote -> {

                        progressBar.visibility = View.VISIBLE
                        allContainingRL.visibility = View.GONE

                        if (noteTitleEdt.text.toString().isNullOrEmpty() || noteDescEdt.text.toString().isNullOrEmpty()) {
                            Toast.makeText(this, "Enter title and description", Toast.LENGTH_SHORT).show()
                            progressBar.visibility = View.GONE
                            allContainingRL.visibility = View.VISIBLE
                            false
                        }
                        else {
                            var noteTitle = noteTitleEdt.text.toString()

                            noteTitle = noteTitle.replace(".","")
                            noteTitle = noteTitle.replace("$","_dol_")
                            noteTitle = noteTitle.replace("[","(")
                            noteTitle = noteTitle.replace("]",")")
                            noteTitle = noteTitle.replace("#","_hash_")
                            noteTitle = noteTitle.replace("/","_")

                            val noteDescription = noteDescEdt.text.toString()

                            val reference = Firebase.storage.reference.child("notes")
                                .child(uid)
                                .child(Date().time.toString())


                            if (uri.toString().isNotEmpty() || !uri.toString().isNullOrBlank()) {
                                try {
                                    reference.putFile(uri).addOnSuccessListener {
                                        if (it.task.isSuccessful) {
                                            reference.downloadUrl.addOnSuccessListener {
                                                val noteModel = NoteModel(uid, noteTitle, noteDescription, it.toString(), selectedColor, noteIsPrivate)
                                                FirebaseDatabase.getInstance().reference.child("notes")
                                                    .child(uid)
                                                    .child(noteTitle)
                                                    .setValue(noteModel)
                                                    .addOnCompleteListener {
                                                        if(it.isSuccessful){
                                                            try {
                                                                if(!noteTitleIntent.isNullOrEmpty() && (noteTitleIntent.toString() != noteTitleEdt.text.toString()) ){
                                                                    FirebaseDatabase.getInstance().reference.child("notes")
                                                                        .child(uid)
                                                                        .child(noteTitleIntent).removeValue().addOnSuccessListener {
                                                                            progressBar.visibility = View.GONE
                                                                            allContainingRL.visibility = View.VISIBLE
                                                                            Toast.makeText(this, "Note Updated successfully", Toast.LENGTH_SHORT).show()
                                                                            finish()
    //                                                                        startActivity(Intent(applicationContext, NotesActivity::class.java))
                                                                        }
                                                                }
                                                                else{
                                                                    progressBar.visibility = View.GONE
                                                                    allContainingRL.visibility = View.VISIBLE
                                                                    Toast.makeText(this, "Note added successfully", Toast.LENGTH_SHORT).show()
                                                                    if(place == "home"){
                                                                        startActivity(Intent(applicationContext, NotesActivity::class.java))
                                                                    }
                                                                    else{
                                                                        finish()
                                                                    }

                                                                }
                                                            } catch (e: Exception) {
                                                                e.printStackTrace()
                                                            }

                                                        }

                                                    }

                                            }
                                        }
                                    }
                                } catch (e: Exception) {
                                }
                            }
                            else if(noteImageUri.isNotEmpty() || !noteImageUri.isNullOrEmpty()){
                                val noteModel = NoteModel(uid, noteTitle, noteDescription, noteImageUri, selectedColor, noteIsPrivate)

                                try {
                                    FirebaseDatabase.getInstance().reference.child("notes")
                                        .child(uid)
                                        .child(noteTitle)
                                        .setValue(noteModel)
                                        .addOnCompleteListener {
                                            if (it.isSuccessful) {
                                                try {
                                                    if(!noteTitleIntent.isNullOrEmpty() && (noteTitleIntent.toString() != noteTitleEdt.text.toString()) ){
                                                        FirebaseDatabase.getInstance().reference.child("notes")
                                                            .child(uid)
                                                            .child(noteTitleIntent).removeValue().addOnSuccessListener {
                                                                progressBar.visibility = View.GONE
                                                                allContainingRL.visibility = View.VISIBLE
                                                                Toast.makeText(this, "Note Updated successfully", Toast.LENGTH_SHORT).show()
                                                                finish()
                                                            }
                                                    }
                                                    else{
                                                        progressBar.visibility = View.GONE
                                                        allContainingRL.visibility = View.VISIBLE
                                                        Toast.makeText(this, "Note added successfully", Toast.LENGTH_SHORT).show()
                                                        if(place == "home"){
                                                            startActivity(Intent(applicationContext, NotesActivity::class.java))
                                                        }
                                                        else{
                                                            finish()
                                                        }
                                                    }
                                                } catch (e: Exception) {
                                                    e.printStackTrace()
                                                }

                                            }
                                        }
                                } catch (e: Exception) {
                                }
                            }
                            else {
                                val noteModel = NoteModel(uid, noteTitle, noteDescription, "", selectedColor, noteIsPrivate)

                                try {
                                    FirebaseDatabase.getInstance().reference.child("notes")
                                        .child(uid)
                                        .child(noteTitle)
                                        .setValue(noteModel)
                                        .addOnCompleteListener {
                                            if (it.isSuccessful) {
                                                try {
                                                    if(!noteTitleIntent.isNullOrEmpty() && (noteTitleIntent.toString() != noteTitleEdt.text.toString()) ){
                                                        FirebaseDatabase.getInstance().reference.child("notes")
                                                            .child(uid)
                                                            .child(noteTitleIntent).removeValue().addOnSuccessListener {
                                                                progressBar.visibility = View.GONE
                                                                allContainingRL.visibility = View.VISIBLE
                                                                Toast.makeText(this, "Note updated successfully", Toast.LENGTH_SHORT).show()
                                                                finish()
                                                            }
                                                    }
                                                    else{
                                                        progressBar.visibility = View.GONE
                                                        allContainingRL.visibility = View.VISIBLE
                                                        Toast.makeText(this, "Note added successfully", Toast.LENGTH_SHORT).show()
                                                        if(place == "home"){
                                                            startActivity(Intent(applicationContext, NotesActivity::class.java))
                                                        }
                                                        else{
                                                            finish()
                                                        }
                                                    }
                                                } catch (e: Exception) {
                                                    e.printStackTrace()
                                                }
                                            }
                                        }
                                } catch (e: Exception) {
                                }
                            }

                            true
                        }



                    true

                }
                else -> false
            }
        }
    }

    private fun selectImage() {
        try {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            if (intent.resolveActivity(packageManager) != null) {
                startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE)
            }
        } catch (e: Exception) {
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
        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK) {
            if (data != null) {
                val it = data.data
                noteImage.setImageURI(it)
                uri = it!!
                noteImageUri = ""
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


}