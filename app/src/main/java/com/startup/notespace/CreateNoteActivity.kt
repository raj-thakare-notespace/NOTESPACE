package com.startup.notespace

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.startup.notespace.db.Note
import com.startup.notespace.db.NoteViewModel
import com.google.android.material.appbar.MaterialToolbar


class CreateNoteActivity : AppCompatActivity() {

    lateinit var viewModel: NoteViewModel
    var noteID = -1

    val myRequestCode = 123
    var selectedColor = "#ffffff"
    lateinit var noteImage: ImageView
    private var selectedImagePath: String = ""
    lateinit var noteTitleEdt: EditText
    lateinit var noteDescEdt: EditText
    lateinit var createNoteRL: RelativeLayout
    lateinit var createNoteToolbar: MaterialToolbar
    val REQUEST_CODE_STORAGE_PERMISSION = 1
    val REQUEST_CODE_SELECT_IMAGE = 2


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_note)

        noteTitleEdt = findViewById(R.id.idEdtNoteTitle)
        noteDescEdt = findViewById(R.id.idEdtNoteDesc)
        noteImage = findViewById(R.id.noteImage)
        createNoteRL = findViewById(R.id.createNoteRL)
        createNoteToolbar = findViewById(R.id.createNoteAppBar)
        noteTitleEdt = findViewById(R.id.idEdtNoteTitle)
        noteDescEdt = findViewById(R.id.idEdtNoteDesc)

        var count = 1


        val noteType = intent.getStringExtra("noteType")

        if (noteType.equals("Edit")) {
            val noteTitle = intent.getStringExtra("noteTitle")
            val noteDescription = intent.getStringExtra("noteDescription")
            val noteColor = intent.getStringExtra("noteColor")
            val noteImageView = intent.getStringExtra("noteImageUris")
            Log.i("sdjl", noteImageView.toString())
            noteID = intent.getIntExtra("noteId", -1)
            noteTitleEdt.setText(noteTitle)
            noteDescEdt.setText(noteDescription)
            noteImage.visibility = View.VISIBLE
            noteImage.setImageBitmap(BitmapFactory.decodeFile(noteImageView))
            createNoteRL.setBackgroundColor(Color.parseColor("#ffffff"))
        }


        createNoteToolbar.setNavigationOnClickListener {
            this.finish()
        }



        viewModel = ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[NoteViewModel::class.java]


        createNoteToolbar.setOnMenuItemClickListener {

            when (it.itemId) {

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

                    if(isPermissionGranted(this)){
                        if(noteTitleEdt.text.toString().isNullOrEmpty() || noteDescEdt.text.toString().isNullOrEmpty()){
                            Toast.makeText(this,"Enter title and description.",Toast.LENGTH_SHORT).show()
                            false
                        }
                        else{
                            val noteTitle = noteTitleEdt.text.toString()
                            val noteDescription = noteDescEdt.text.toString()
                            val noteImage = selectedImagePath

                            if (noteType.equals("Edit")) {

                                if (noteTitle.isNotEmpty() && noteDescription.isNotEmpty()) {
                                    val noteTitle = intent.getStringExtra("noteTitle").toString()
                                    val noteDescription = intent.getStringExtra("noteDescription").toString()
                                    val noteColor = intent.getStringExtra("noteColor").toString()
                                    var noteImageUri = intent.getStringExtra("noteImageUris").toString()

                                    if(!selectedImagePath.isNullOrEmpty()){
                                        noteImageUri = selectedImagePath.toString()
                                    }
                                    val updatedNote: Note? = Note(noteTitle, noteDescription, selectedColor, noteImageUri)
                                    updatedNote?.id = noteID
                                    if (updatedNote != null) {
                                        viewModel.updateNote(updatedNote)
                                    }
                                    Toast.makeText(this, "Note Updated..", Toast.LENGTH_LONG).show()
                                    startActivity(Intent(this,YourNotesActivity::class.java))
                                }
                            } else {
                                if (noteTitle.isNotEmpty() && noteDescription.isNotEmpty()) {
                                    // if the string is not empty we are calling a
                                    // add note method to add data to our room database.
                                    viewModel.addNote(
                                        Note(
                                            noteTitle,
                                            noteDescription,
                                            selectedColor,
                                            selectedImagePath
                                        )
                                    )
                                }
                            }
                            finish()
                            true
                        }
                    }
                    else{
                        takePermission(this)
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

    private fun getPathFromUri(contentUri: Uri): String? {
        var filePath: String? = null
        var cursor = contentResolver.query(contentUri, null, null, null, null)
        if (cursor == null) {
            filePath = contentUri.path
        } else {
            cursor.moveToFirst()
            var index = cursor.getColumnIndex("_data")
            filePath = cursor.getString(index)
            cursor.close()
        }
        return filePath
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK) {
            if (data != null) {
                val selectedImageUri = data.data
                if (selectedImageUri != null) {
                    try {
                        val inputStream = contentResolver.openInputStream(selectedImageUri)
                        val bitmap = BitmapFactory.decodeStream(inputStream)
                        noteImage.setImageBitmap(bitmap)
                        noteImage.visibility = View.VISIBLE

                        selectedImagePath = getPathFromUri(selectedImageUri).toString()

                    } catch (e: Exception) {
                        Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                    }
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


}

