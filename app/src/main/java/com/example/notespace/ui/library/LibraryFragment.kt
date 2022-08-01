package com.example.notespace.ui.library

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.notespace.*

class LibraryFragment : Fragment() {

    lateinit var youtNoteItem : CardView
    lateinit var savedNoteItem : CardView
    lateinit var myLibraryItem : CardView
    lateinit var notesItem : CardView


    companion object {
        fun newInstance() = LibraryFragment()
    }

    private lateinit var viewModel: LibraryViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        youtNoteItem = view.findViewById(R.id.yourNoteItem)
        savedNoteItem = view.findViewById(R.id.savedNoteItem)
        myLibraryItem = view.findViewById(R.id.myLibraryItem)
        notesItem = view.findViewById(R.id.notesItem)

        notesItem.setOnClickListener {
            val intent = Intent(context,NotesActivity::class.java)
            startActivity(intent)
        }


        youtNoteItem.setOnClickListener {
            val intent = Intent(context,YourNotesActivity::class.java)
            startActivity(intent)
        }
        myLibraryItem.setOnClickListener {
            val intent = Intent(context,MyLibraryActivity::class.java)
            startActivity(intent)
        }


        savedNoteItem.setOnClickListener {
            val intent = Intent(context,SavedActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.library_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(LibraryViewModel::class.java)
        // TODO: Use the ViewModel
    }

}