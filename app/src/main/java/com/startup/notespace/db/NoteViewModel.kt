package com.startup.notespace.db

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// View Model for Notes
class NoteViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: NoteRepository
    var allNotes: LiveData<List<Note>>
//    var notesList : List<Note>

    init {
        val noteDao = NotesDatabase.getDatabase(application).getNoteDao()
        repository = NoteRepository(noteDao)
        allNotes = repository.allNotes
//        notesList = repository.notesList
    }


    fun addNote(note: Note) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(note)
    }

    fun delete(note: Note) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(note)
    }

    fun updateNote(note: Note) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(note)
    }
}
