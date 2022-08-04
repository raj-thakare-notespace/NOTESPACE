package com.startup.notespace

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.startup.notespace.db.*
import com.google.android.material.floatingactionbutton.FloatingActionButton

class YourNotesActivity : AppCompatActivity()
    , NoteClickDeleteInterface,NoteClickInterface {

    lateinit var fab : FloatingActionButton

    lateinit var viewModel: NoteViewModel
    lateinit var notesRV: RecyclerView
    lateinit var backButton : ImageView

    // White Tiger

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_your_notes)

        backButton = findViewById(R.id.backArrowYourNotes)

        fab = findViewById(R.id.fabCreateYourNote)

        fab.setOnClickListener {
            val intent = Intent(this, CreateNoteActivity::class.java)
            startActivity(intent)
        }

        backButton.setOnClickListener {
            finish()
        }

        notesRV = findViewById(R.id.recycler_view)

        notesRV.layoutManager =  StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)


        val noteRVAdapter = NoteRVAdapter(this,this,this)

        notesRV.adapter = noteRVAdapter

        viewModel = ViewModelProvider(this,ViewModelProvider.AndroidViewModelFactory.getInstance(application)).get(NoteViewModel::class.java)





        viewModel.allNotes.observe(this, Observer { list ->
            list?.let {
                // on below line we are updating our list.
                noteRVAdapter.updateList(it)
//                notesList = it
            }
        })


    }


    override fun onDeleteIconClick(note: Note) {
        viewModel.delete(note)
        Toast.makeText(this,"${note.noteTitle} has been deleted",Toast.LENGTH_SHORT).show()

    }

    override fun onNoteClick(note: Note) {
        Log.i("wooh",note.image!!)

        val intent = Intent(this, CreateNoteActivity::class.java)
        intent.putExtra("noteType", "Edit")
        intent.putExtra("noteTitle", note.noteTitle)
        intent.putExtra("noteDescription", note.noteDescription)
        intent.putExtra("noteId", note.id)
        intent.putExtra("noteImageUris",note.image!!)
        intent.putExtra("noteColor",note.color)
        startActivity(intent)
        this.finish()
    }
}