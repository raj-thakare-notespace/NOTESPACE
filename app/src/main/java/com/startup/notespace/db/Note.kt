package com.startup.notespace.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// Model for created Note
@Entity(tableName = "notes_table")
class Note(
    @ColumnInfo(name = "title") val noteTitle: String,
    @ColumnInfo(name = "description") val noteDescription: String,
    @ColumnInfo(name = "color") val color: String,
    @ColumnInfo(name = "image") val image: String = ""
) {

    @PrimaryKey(autoGenerate = true)
    var id = 0
}