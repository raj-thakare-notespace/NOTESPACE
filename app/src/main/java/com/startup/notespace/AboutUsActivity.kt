package com.startup.notespace

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.google.android.material.appbar.MaterialToolbar

class AboutUsActivity : AppCompatActivity() {
    lateinit var toolbar: MaterialToolbar
    lateinit var privacyPolicyTV: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_us)

        privacyPolicyTV = findViewById(R.id.privacyPolicyTV)

        privacyPolicyTV.setOnClickListener {
            startActivity(Intent(this,PrivacyPolicyActivity::class.java))
        }

        // White Tiger

        toolbar = findViewById(R.id.aboutUsToolBar)
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }
}