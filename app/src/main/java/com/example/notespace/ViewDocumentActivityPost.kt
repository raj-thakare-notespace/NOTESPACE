package com.example.notespace

import android.Manifest
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.github.barteksc.pdfviewer.PDFView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class ViewDocumentActivityPost : AppCompatActivity() {
    lateinit var pdfView: PDFView

    lateinit var dialog: ProgressDialog
    lateinit var toolbar: MaterialToolbar

    private fun downloadFilePdf(url : String, name : String) {
        PRDownloader.initialize(this)

        var file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

        try {
            PRDownloader.download(url, file.path, name.dropLast(4)+".pdf")
                .build()
                .setOnStartOrResumeListener { }
                .setOnPauseListener { }
                .start(object : OnDownloadListener {
                    override fun onDownloadComplete() {
                        Toast.makeText(applicationContext,"Download Complete.", Toast.LENGTH_SHORT).show()
                    }
                    override fun onError(error: com.downloader.Error?) {
                        Toast.makeText(applicationContext,"Something went wrong.", Toast.LENGTH_SHORT).show()
                    }

                    fun onError(error: Error?) {}
                })
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


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_document_post)

        var pdfUrl = intent.getStringExtra("path")
        var pdfName = intent.getStringExtra("pdfName")

        toolbar = findViewById(R.id.viewDocToolBar)

        dialog = ProgressDialog(this)
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
        dialog.setContentView(R.layout.progress_bar_layout_view_doc)


        toolbar.setNavigationOnClickListener {
            finish()
        }

        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.download_post_view_doc -> {

                    try {
                        val builder = MaterialAlertDialogBuilder(this)
                        builder.setTitle("Do you want to download this file?")
                        builder.setCancelable(false)
                            .setPositiveButton("Yes") { dialog, id ->

                                Toast.makeText(this,"Downloading...",Toast.LENGTH_SHORT).show()

                                if(isPermissionGranted(this)){
                                    downloadFilePdf(pdfUrl.toString(),pdfName.toString())
                                }
                                else{
                                    takePermission(this)
                                }

                            }
                            .setNegativeButton("No") { dialog, id ->
                                dialog.dismiss()
                            }
                        val alert = builder.create()
                        alert.show()
                    } catch (e: Exception) {
                    }

                    true
                }
                else -> false
            }
        }




        toolbar.title = pdfName


        pdfView = findViewById(R.id.pdfView)
        pdfView.isBestQuality

//        CoroutineScope(Dispatchers.IO).launch {
//            val input: InputStream = URL(pdfPath).openStream()
//            pdfView.fromStream(input).load()
//        }

        RetrivePdfStream().execute(pdfUrl)

//        var ref = FirebaseStorage.getInstance().getReferenceFromUrl(pdfPath.toString())
//        Log.i("referenceD", ref.toString())
//        val localFile = File.createTempFile(pdfName, ".pdf")
//        ref.getFile(localFile).addOnSuccessListener {
//            pdfView.fromFile(localFile).load()
//        }

    }

    internal inner class RetrivePdfStream :
        AsyncTask<String?, Void?, InputStream?>() {

        // Here load the pdf and dismiss the dialog box
        override fun onPostExecute(inputStream: InputStream?) {
            pdfView.fromStream(inputStream).load()
            dialog.dismiss()
        }

        override fun doInBackground(vararg p0: String?): InputStream? {
            var inputStream: InputStream? = null
            try {

                // adding url
                val url = URL(p0[0])
                val urlConnection = url.openConnection() as HttpURLConnection

                // if url connection response code is 200 means ok the execute
                if (urlConnection.responseCode == 200) {
                    inputStream = BufferedInputStream(urlConnection.inputStream)
                }
            } // if error return null
            catch (e: IOException) {
                return null
            }
            return inputStream
        }
    }
}