package com.startup.notespace

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.startup.notespace.models.NoteModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class NoteDetailActivity : AppCompatActivity() {

    lateinit var imageView : ImageView
    lateinit var noteTitle : TextView
    lateinit var noteDescription : TextView
    lateinit var back : ImageView
    lateinit var relativeLayout: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_detail)

        val userId = intent.getStringExtra("uid")
        val title = intent.getStringExtra("noteTitle")


        imageView = findViewById(R.id.noteImageNoteDetail)
        noteTitle = findViewById(R.id.idEdtNoteTitleNoteDetail)
        noteDescription = findViewById(R.id.idEdtNoteDescNoteDetail)
        back = findViewById(R.id.backIV)
        relativeLayout = findViewById(R.id.relativeLayoutNoteDetail)

        back.setOnClickListener {
            finish()
        }

        try {
            FirebaseDatabase.getInstance().reference.child("notes")
                .child(userId.toString())
                .child(title.toString())
                .addValueEventListener(object : ValueEventListener{
                    @SuppressLint("Range")
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists()){
                            val model = snapshot.getValue(NoteModel::class.java)
                            Glide.with(applicationContext)
                                .load(model!!.image)
                                .into(imageView)
                            noteTitle.setText(model.title)
                            noteDescription.setText(model.description)
                            relativeLayout.setBackgroundColor(android.graphics.Color.parseColor(model.color))
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