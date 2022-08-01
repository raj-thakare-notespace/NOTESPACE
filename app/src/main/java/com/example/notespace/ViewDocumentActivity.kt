package com.example.notespace

import android.app.ProgressDialog
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.github.barteksc.pdfviewer.PDFView
import com.google.android.material.appbar.MaterialToolbar
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


class ViewDocumentActivity : AppCompatActivity() {

    lateinit var pdfView: PDFView

    lateinit var dialog: ProgressDialog
    lateinit var toolbar: MaterialToolbar


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_document)

        toolbar = findViewById(R.id.viewDocToolBar)

        dialog = ProgressDialog(this)
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
        dialog.setContentView(R.layout.progress_bar_layout_view_doc)


        toolbar.setNavigationOnClickListener {
            finish()
        }

        var pdfPath = intent.getStringExtra("path")
        var pdfName = intent.getStringExtra("pdfName")


        toolbar.title = pdfName


        pdfView = findViewById(R.id.pdfView)
        pdfView.isBestQuality

//        CoroutineScope(Dispatchers.IO).launch {
//            val input: InputStream = URL(pdfPath).openStream()
//            pdfView.fromStream(input).load()
//        }

        RetrivePdfStream().execute(pdfPath)

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