package com.startup.notespace

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
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

    private fun deleteUser(email: String, password: String) {
        val user = FirebaseAuth.getInstance().currentUser

        // Get auth credentials from the user for re-authentication. The example below shows
        // email and password credentials but there are multiple possible providers,
        // such as GoogleAuthProvider or FacebookAuthProvider.
        val credential = EmailAuthProvider.getCredential(email, password)

        // Prompt the user to re-provide their sign-in credentials
        user?.reauthenticate(credential)?.addOnCompleteListener {
            user.delete()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        startActivity(Intent(this, SignInActivity::class.java))
                        Toast.makeText(this, "Account Deleted Permanently.", Toast.LENGTH_LONG).show()
                        MainActivity().finish()
                        ProfileActivity().finish()
                        finish()
                    }
                }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delete_account)

        val email = intent.getStringExtra("email").toString()
        val password = intent.getStringExtra("password").toString()

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
                                            if(uidArrayList.isNotEmpty()){
                                                for (item in uidArrayList) {
                                                    try {
                                                        val ref = reference.getReferenceFromUrl(item)
                                                        ref.delete()
                                                    } catch (e: Exception) {
                                                    }
                                                }
                                            }

                                            Firebase.database.reference.child("libraryOfPdfUrls")
                                                .child(Firebase.auth.currentUser!!.uid)
                                                .removeValue()

                                        } catch (e: Exception) {
                                        }


                                        try {
//                                            Firebase.auth.currentUser!!.delete()
//                                                .addOnSuccessListener {
//                                                    Toast.makeText(this, "Account Deleted Permanently", Toast.LENGTH_SHORT).show()
//                                                    startActivity(Intent(this, SignInActivity::class.java))
//                                                    MainActivity().finish()
//                                                    ProfileActivity().finish()
//                                                    finish()
//                                                }

                                            deleteUser(email,password)



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