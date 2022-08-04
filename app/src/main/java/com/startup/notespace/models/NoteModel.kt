package com.startup.notespace.models

data class NoteModel(
    var uid : String = "",
    var title : String = "",
    var description : String = "",
    var image : String = "",
    var color : String = "",
    var isPrivate : Boolean = false
)
