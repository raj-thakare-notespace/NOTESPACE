package com.startup.notespace

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

//    private fun downloadFilePdf(url : String, name : String) {
//        PRDownloader.initialize(this)
//
//        var file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
//
//        try {
//            PRDownloader.download(url, file.path, name.dropLast(4)+".pdf")
//                .build()
//                .setOnStartOrResumeListener { }
//                .setOnPauseListener { }
//                .start(object : OnDownloadListener {
//                    override fun onDownloadComplete() {
//                        Toast.makeText(applicationContext,"Download Complete.", Toast.LENGTH_SHORT).show()
//                    }
//                    override fun onError(error: com.downloader.Error?) {
//                        Toast.makeText(applicationContext,"Something went wrong.", Toast.LENGTH_SHORT).show()
//                    }
//
//                    fun onError(error: Error?) {}
//                })
//        } catch (e: Exception) {
//        }
//
//    }

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

//        toolbar.setOnMenuItemClickListener {
//            when (it.itemId) {
//                R.id.download_post_view_doc -> {
//
//                    try {
//                        val builder = MaterialAlertDialogBuilder(this)
//                        builder.setTitle("Do you want to download this file?")
//                        builder.setCancelable(false)
//                            .setPositiveButton("Yes") { dialog, id ->
//
//                                Toast.makeText(this,"Downloading...",Toast.LENGTH_SHORT).show()
//
//                                    downloadFilePdf(pdfUrl.toString(),pdfName.toString())
//
//
//                            }
//                            .setNegativeButton("No") { dialog, id ->
//                                dialog.dismiss()
//                            }
//                        val alert = builder.create()
//                        alert.show()
//                    } catch (e: Exception) {
//                    }
//
//                    true
//                }
//                else -> false
//            }
//        }




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