package com.example.notespace

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class DeleteAccountActivity : AppCompatActivity() {

    lateinit var deleteButton : MaterialButton
    lateinit var toolbar: MaterialToolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delete_account)

        toolbar = findViewById(R.id.deleteAccountToolBar)

        toolbar.setNavigationOnClickListener {
            finish()
        }

        deleteButton = findViewById(R.id.deleteAccountButtonActivity)

        deleteButton.setOnClickListener {

            val reference = FirebaseStorage.getInstance()

            var uidArrayList = ArrayList<String>()
            try {
                Firebase.database.reference.child("libraryOfPdfUrls")
                    .child(Firebase.auth.currentUser!!.uid)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if(snapshot.exists()){
                                uidArrayList.clear()
                                for(item in snapshot.children){
                                    try {
                                        Firebase.database.reference.child("libraryOfPdfUrls")
                                            .child(Firebase.auth.currentUser!!.uid)
                                            .child(item.key.toString())
                                            .addListenerForSingleValueEvent(object :
                                                ValueEventListener {
                                                override fun onDataChange(snapshot: DataSnapshot) {
                                                    if(snapshot.exists()){
                                                        for (item in snapshot.children) {
                                                            var count = 0
                                                            for (i in item.children) {
                                                                Log.i("abcdefgh", i.value.toString())
                                                                count++
                                                            }
                                                            if (count > 0 || item.value.toString() == "folderTrue") {
                                                                continue
                                                            } else {
                                                                uidArrayList.add(item.value.toString())
                                                            }
                                                        }
                                                    }
                                                }

                                                override fun onCancelled(error: DatabaseError) {
                                                    TODO("Not yet implemented")
                                                }

                                            })
                                    } catch (e: Exception) {
                                    }
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                    })
            } catch (e: Exception) {
            }



            val builder = MaterialAlertDialogBuilder(this)
            builder.setTitle("Delete account permanently?")
            builder.setCancelable(false)
                .setPositiveButton("Yes") { dialogL, id ->

                    try {
                        FirebaseDatabase.getInstance().reference.child("users")
                            .child(Firebase.auth.currentUser!!.uid)
                            .child("token")
                            .removeValue()
                    } catch (e: Exception) {
                    }

                    try {
                        FirebaseDatabase.getInstance().reference.child("users")
                            .child(Firebase.auth.currentUser!!.uid)
                            .removeValue().addOnSuccessListener {
                                FirebaseDatabase.getInstance().reference.child("Library")
                                    .child(Firebase.auth.currentUser!!.uid)
                                    .removeValue().addOnSuccessListener {
                                        try {
                                            for (item in uidArrayList) {
                                                val ref = reference.getReferenceFromUrl(item)
                                                ref.delete()
                                            }
                                            Firebase.database.reference.child("libraryOfPdfUrls")
                                                .child(Firebase.auth.currentUser!!.uid)
                                                .removeValue()
                                        } catch (e: Exception) {
                                        }


                                        try {
                                            Firebase.auth.currentUser!!.delete()
                                                .addOnSuccessListener {
                                                    Toast.makeText(this, "Account Deleted Permanently", Toast.LENGTH_SHORT).show()
                                                    startActivity(Intent(this, SignInActivity::class.java))
                                                    MainActivity().finish()
                                                    ProfileActivity().finish()
                                                    finish()
                                                }


                                        }
                                        catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                    }

                            }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }


                }
                .setNegativeButton("No") { dialog, id ->
                    dialog.dismiss()
                }
            val alert = builder.create()
            alert.show()
        }
    }
}