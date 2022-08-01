package com.example.notespace.db

import androidx.lifecycle.LiveData
import androidx.room.*

// Dao for Note
@Dao
interface NotesDao {

    @Query("Select * from notes_table order by id ASC")
    fun getAllNotes(): LiveData<List<Note>>

//    @Query("Select * from notes_table order by id ASC")
//    fun allNotesArraylist() : List<Note>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: Note)

    @Delete
    suspend fun delete(note: Note)

    @Update
    suspend fun update(note: Note)
}