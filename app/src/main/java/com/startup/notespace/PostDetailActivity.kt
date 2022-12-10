package com.startup.notespace

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide

import com.google.android.material.appbar.MaterialToolbar
import java.util.*

class PostDetailActivity : AppCompatActivity() {

    lateinit var postImage : ImageView

    lateinit var toolbar: MaterialToolbar

    var postedAt = "Image"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_detail)

        val postUri = intent.getStringExtra("uri")
        postedAt = intent.getStringExtra("postedAt").toString()
        val code = intent.getStringExtra("code")

        toolbar = findViewById(R.id.postDetailToolBar)

        toolbar.setNavigationOnClickListener {
            finish()
        }

//        toolbar.setOnMenuItemClickListener {
//            when(it.itemId){
//                R.id.downloadPost -> {
//
//                    try {
//                        Toast.makeText(this,"Downloading...",Toast.LENGTH_SHORT).show()
//                            downloadFile(postUri.toString())
//
//                    } catch (e: Exception) {
//                    }
//                    true
//                }
//                else -> {
//                    false
//                }
//            }
//        }

        postImage = findViewById(R.id.postImageDetail)




        Log.i("ffff",postedAt.toString())

        Glide.with(this)
            .load(Uri.parse(postUri.toString()))
            .into(postImage)




    }

//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//
//        if(requestCode == 101){
//            if(grantResults.isNotEmpty()){
//                var readExternalStorage : Boolean = grantResults[0] == PackageManager.PERMISSION_GRANTED
//                if(readExternalStorage){
//                    Toast.makeText(this,"Read permission granted in android 10 or below",Toast.LENGTH_SHORT).show()
//                }
//                else{
//                    takePermission(this)
//                }
//            }
//        }
//
//    }
//
//    private fun isPermissionGranted(context: Context) : Boolean {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            // For android 11
//            return Environment.isExternalStorageManager()
//        }
//        else{
//            // For below
//            val  readExternalStorageManager = ContextCompat.checkSelfPermission(context,android.Manifest.permission.READ_EXTERNAL_STORAGE)
//            return readExternalStorageManager == PackageManager.PERMISSION_GRANTED
//        }
//    }
//
//
//    private fun takePermission(context: Context) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            // For android 11
//
//            try {
//                var intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
//                intent.addCategory("android.intent.category.DEFAULT")
//                intent.data = Uri.parse(String.format("package:%s",context.packageName))
//                startActivityForResult(intent,100)
//            }
//            catch (e : Exception){
//                var intent = Intent()
//                intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
//                startActivityForResult(intent,100)
//            }
//
//        }
//        else{
//            // For below versions
//            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),101)
//
//        }
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        // To manage permissions
//        if(requestCode == RESULT_OK){
//            if(requestCode == 100){
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                    // For android 11
//                    if(Environment.isExternalStorageManager()){
//                        Toast.makeText(this,"Permission Granted in android 11",Toast.LENGTH_SHORT).show()
//                    }
//                    else{
//                        takePermission(this)
//                    }
//                }
//            }
//        }
//    }

//    private fun checkPermission(url : String){
//        Dexter.withContext(this)
//            .withPermissions(
//                Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                Manifest.permission.READ_EXTERNAL_STORAGE,
//            ).withListener(object : MultiplePermissionsListener {
//                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
//                    if(report.areAllPermissionsGranted()){
//                        downloadFile(url)
//                    }
//                    else
//                    {
//                        Toast.makeText(applicationContext,"Please grant permissions from settings.",Toast.LENGTH_SHORT).show()
//                    }
//
//                }
//
//                override fun onPermissionRationaleShouldBeShown(
//                    p0: MutableList<PermissionRequest>?,
//                    p1: PermissionToken?
//                ) {
//
//                }
//
//            }).check()
//    }

//    fun downloadFile(url : String) {
//        PRDownloader.initialize(applicationContext)
//
//        var file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
//
//        val time = Date().time.toString()
//
//        try {
//            PRDownloader.download(url, file.path, "$time.jpg")
//                .build()
//                .setOnStartOrResumeListener { }
//                .setOnPauseListener { }
//                .start(object : OnDownloadListener {
//                    override fun onDownloadComplete() {
//                        Toast.makeText(applicationContext,"Download Complete",Toast.LENGTH_SHORT).show()
//                    }
//                    override fun onError(error: com.downloader.Error?) {
//                        Toast.makeText(applicationContext,"Some error occurred",Toast.LENGTH_SHORT).show()
//                    }
//
//                    fun onError(error: Error?) {}
//                })
//        } catch (e: Exception) {
//        }
//    }

}