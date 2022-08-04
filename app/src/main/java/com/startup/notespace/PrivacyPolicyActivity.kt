package com.startup.notespace

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.appbar.MaterialToolbar

class PrivacyPolicyActivity : AppCompatActivity() {
    lateinit var toolbar: MaterialToolbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy_policy)
        toolbar = findViewById(R.id.privacyPolicyToolBar)

        toolbar.setOnClickListener {
            finish()
        }
    }
}

