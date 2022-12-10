package com.startup.notespace

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.startup.notespace.models.Post
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.shockwave.pdfium.PdfDocument
import com.shockwave.pdfium.PdfiumCore
import java.io.ByteArrayOutputStream
import java.util.*


class MakePostActivity : AppCompatActivity() {


    val CHOOSE_PDF_FROM_DEVICE = 2307

    lateinit var pdfName: TextView

    lateinit var cardView: CardView

    val storage = Firebase.storage

    lateinit var uri: Uri

    var bitmap : String = ""

    lateinit var  docPostBigIV: ImageView

    private lateinit var auth: FirebaseAuth

    var CODE = 0


    lateinit var progressDialog: ProgressDialog


    private lateinit var addImageButton: MaterialButton
    private lateinit var addDocumentButton: MaterialButton
    private lateinit var postInputCaption: EditText

    private lateinit var postImageView: ImageView
    private lateinit var postImageUri: String

    private lateinit var createPostToolbar: MaterialToolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_make_post)

        docPostBigIV = findViewById(R.id.docPostBigIV)

        progressDialog = ProgressDialog(this)
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.isIndeterminate = true


        cardView = findViewById(R.id.pdfCardView)

        auth = Firebase.auth

        postImageView = findViewById(R.id.imageMakePost)

        pdfName = findViewById(R.id.pdfNameTV)
        addImageButton = findViewById(R.id.addImageButton)
        addDocumentButton = findViewById(R.id.addDocumentButton)
        postInputCaption = findViewById(R.id.postInputCaption)

        createPostToolbar = findViewById(R.id.createPostToolBar)

        addDocumentButton.setOnClickListener {
                CODE = 1
                cardView.visibility = View.VISIBLE
                docPostBigIV.visibility = View.VISIBLE
                postImageView.visibility = View.GONE
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                intent.type = "application/pdf"
                startActivityForResult(intent, CHOOSE_PDF_FROM_DEVICE)


        }

        createPostToolbar.setNavigationOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }


        createPostToolbar.setOnMenuItemClickListener {
            when (it.itemId) {

                R.id.iconSaveNotePost -> {

                     progressDialog.show()
                    progressDialog.setContentView(R.layout.progress_bar_layout)
                    progressDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

                    if (CODE == 0) {

                        try {
                            val reference = storage.reference.child("posts")
                                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                .child(Date().time.toString())

                            reference.putFile(uri).addOnSuccessListener {
                                if (it.task.isSuccessful) {
                                    reference.downloadUrl.addOnSuccessListener {
                                        val post = Post()
                                        post.postImage = it.toString()
                                        Log.i("postimage", it.toString())
                                        post.postedBy = FirebaseAuth.getInstance().currentUser!!.uid
                                        post.postDescription = postInputCaption.text.toString()
                                        post.postedAt = Date().time
                                        post.docUrl = ""
                                        post.docName = ""

                                        Firebase.database.reference.child("posts")
                                            .push()
                                            .setValue(post).addOnCompleteListener {
                                                if (it.isSuccessful) {
                                                    Firebase.database.reference.child("users")
                                                        .child(Firebase.auth.currentUser!!.uid)
                                                        .child("my_posts")
                                                        .push()
                                                        .setValue(post).addOnSuccessListener {
                                                            progressDialog.dismiss()
                                                            Toast.makeText(
                                                                this,
                                                                "Posted Successfully",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                            finish()
                                                        }

                                                }
                                                else{
                                                    progressDialog.dismiss()
                                                    finish()
                                                }
                                            }


                                    }
                                }
                                else{
                                    Toast.makeText(this,"Something went wrong.",Toast.LENGTH_SHORT).show()
                                    finish()
                                }
                            }
                        } catch (e: Exception) {
                        }


                    }
                    else if(CODE==1){

                        try {
                            val reference = storage.reference.child("postDocuments")
                                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                .child(pdfName.text.toString())

                            val thumbnailReference = storage.reference.child("pdfThumbnails")
                                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                .child(pdfName.text.toString())

                            generateImageFromPdf(uri)


                            val bitmap = (docPostBigIV.drawable as BitmapDrawable).bitmap
                            val baos = ByteArrayOutputStream()
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                            val data = baos.toByteArray()

                            var thumbnailUrl = ""

                            thumbnailReference.putBytes(data).addOnSuccessListener {
                                if(it.task.isSuccessful){
                                    thumbnailReference.downloadUrl.addOnSuccessListener {
                                        thumbnailUrl = it.toString()
                                    }
                                }
                                else{
                                    thumbnailUrl = "https://firebasestorage.googleapis.com/v0/b/notespace-5ca23.appspot.com/o/usefulResources%2Fpdf%20thumbnail%20image.jpg?alt=media&token=0a9b3cae-7887-4a13-9001-dfa2104fedeb"
                                }
                            }

                            if(thumbnailUrl.isNullOrEmpty()){
                                thumbnailUrl = "https://firebasestorage.googleapis.com/v0/b/notespace-5ca23.appspot.com/o/usefulResources%2Fpdf%20thumbnail%20image.jpg?alt=media&token=0a9b3cae-7887-4a13-9001-dfa2104fedeb"
                            }

                            reference.putFile(uri).addOnSuccessListener {
                                if (it.task.isSuccessful) {
                                    reference.downloadUrl.addOnSuccessListener {
                                        val post = Post()

                                        post.postImage = ""
                                        post.docName = pdfName.text.toString()
                                        post.docUrl = it.toString()
                                        post.postedBy = FirebaseAuth.getInstance().currentUser!!.uid
                                        post.postDescription = postInputCaption.text.toString()
                                        post.postedAt = Date().time
                                        post.docThumbnail = thumbnailUrl

                                        Log.i("bitmapMake",thumbnailUrl)

                                        Firebase.database.reference.child("posts")
                                            .push()
                                            .setValue(post).addOnCompleteListener {
                                                if (it.isSuccessful) {

                                                    Firebase.database.reference.child("users")
                                                        .child(Firebase.auth.currentUser!!.uid)
                                                        .child("my_document_posts")
                                                        .push()
                                                        .setValue(post).addOnSuccessListener {
                                                            progressDialog.dismiss()
                                                            Toast.makeText(
                                                                this,
                                                                "Posted Successfully",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                            finish()
                                                        }

                                                }
                                                else{
                                                    progressDialog.dismiss()
                                                    finish()
                                                }
                                            }


                                    }
                                }
                                else{
                                    Toast.makeText(this,"Something went wrong.",Toast.LENGTH_SHORT).show()
                                    finish()
                                }


                            }
                        } catch (e: Exception) {
                        }
                    }
                    true
                }

                else -> true
            }
        }

        addImageButton.setOnClickListener {
                CODE = 0
                cardView.visibility = View.GONE
                docPostBigIV.visibility = View.GONE
                postImageView.visibility = View.VISIBLE
                try {
                    val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(intent, 11)
                } catch (e: Exception) {
                }

        }


    }





    // For picking pdf from device
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CHOOSE_PDF_FROM_DEVICE && resultCode == RESULT_OK) {
            if (data != null) {
                Log.d("SOME", "onActivityResult: " + data.data)
                var filename = data.data?.let { getFileName(it) }
                filename = filename!!.dropLast(4)
                filename = filename.replace(".","_")
                filename = filename.replace("$","_dol_")
                filename = filename.replace("[","(")
                filename = filename.replace("]",")")
                filename = filename.replace("#","_hash_")
                filename = filename.replace("/","_")
                filename = "$filename.pdf"

                pdfName.text = filename
                cardView.visibility = View.VISIBLE
                docPostBigIV.visibility = View.VISIBLE
                uri = data.data!!
                generateImageFromPdf(uri)
            }
        }

        // for image selection
        if (requestCode == 11) {
            if (data?.data != null) {

                val it = data.data

                postImageView.setImageURI(it)
                postImageUri = it.toString()
                postImageView.setImageURI(it)
                uri = it!!
            }
        }
    }

    fun generateImageFromPdf(pdfUri: Uri?) {
        val pageNumber = 0
        val pdfiumCore = PdfiumCore(this)
        try {
            //http://www.programcreek.com/java-api-examples/index.php?api=android.os.ParcelFileDescriptor
            val fd = contentResolver.openFileDescriptor(pdfUri!!, "r")
            val pdfDocument: PdfDocument = pdfiumCore.newDocument(fd)
            pdfiumCore.openPage(pdfDocument, pageNumber)
            val width = pdfiumCore.getPageWidthPoint(pdfDocument, pageNumber)
            val height = pdfiumCore.getPageHeightPoint(pdfDocument, pageNumber)
            val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            pdfiumCore.renderPageBitmap(pdfDocument, bmp, pageNumber, 0, 0, width, height)
            Log.i("some",bmp.toString())
//            saveImage(bmp)
            docPostBigIV.setImageBitmap(bmp)
            bitmap = bmp.toString()
            pdfiumCore.closeDocument(pdfDocument) // important!
        } catch (e: Exception) {
            //todo with exception
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