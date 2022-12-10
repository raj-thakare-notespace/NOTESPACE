package com.startup.notespace

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AboutUsActivity : AppCompatActivity() {
    lateinit var toolbar: MaterialToolbar
    lateinit var privacyPolicyTV: TextView
    lateinit var howToUseTV : TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_us)


        privacyPolicyTV = findViewById(R.id.privacyPolicyTV)
        howToUseTV = findViewById(R.id.howToUseTV)

        howToUseTV.setOnClickListener {
                try {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://youtu.be/bKc9f84jcr8")))
                } catch (e: Exception) {
                }
        }

        privacyPolicyTV.setOnClickListener {
            startActivity(Intent(this,PrivacyPolicyActivity::class.java))
        }


        toolbar = findViewById(R.id.aboutUsToolBar)
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }
}