package com.startup.notespace.db

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.startup.notespace.R
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder

// Adapter to load created Notes
class NoteRVAdapter(
    val context: Context,
    val noteClickDeleteInterface: NoteClickDeleteInterface,
    val noteClickInterface: NoteClickInterface
) :
    RecyclerView.Adapter<NoteRVAdapter.ViewHolder>() {

    private var allNotes: ArrayList<Note> = ArrayList()

//    fun setFilteredList(filteredList : ArrayList<Note>){
//        this.allNotes = filteredList
//        notifyDataSetChanged()
//    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var noteTitle: TextView = itemView.findViewById(R.id.idNoteTitle)
        var noteDescription: TextView = itemView.findViewById(R.id.idNoteDescription)
        var cardView: MaterialCardView = itemView.findViewById(R.id.itemCardView)
        var noteImageRV: ImageView = itemView.findViewById(R.id.RVIVNote)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_rv_notes, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if (allNotes[position].image != null && allNotes[position].image.isNotEmpty()) {
            holder.noteImageRV.setImageBitmap(BitmapFactory.decodeFile(allNotes[position].image))
            holder.noteImageRV.visibility = View.VISIBLE
        } else {
            holder.noteImageRV.visibility = View.GONE
        }
        holder.noteTitle.text = allNotes[position].noteTitle
        holder.noteDescription.text = allNotes[position].noteDescription

        holder.itemView.setOnClickListener {
            noteClickInterface.onNoteClick(allNotes[position])
        }
        holder.cardView.setCardBackgroundColor(Color.parseColor(allNotes[position].color))

        holder.itemView.setOnLongClickListener {

            try {
                val builder = MaterialAlertDialogBuilder(context)
                builder.setTitle("Are you sure you want to Delete?")
                builder.setCancelable(false)
                    .setPositiveButton("Yes") { dialog, id ->
                        noteClickDeleteInterface.onDeleteIconClick(allNotes.get(position))
                    }
                    .setNegativeButton("No") { dialog, id ->
                        dialog.dismiss()
                    }
                val alert = builder.create()
                alert.show()
            } catch (e: Exception) {
            }

            return@setOnLongClickListener true
        }


    }

    override fun getItemCount(): Int {
        return allNotes.size
    }

    fun updateList(newList: List<Note>) {
        // on below line we are clearing
        // our notes array list
        allNotes.clear()
        // on below line we are adding a
        // new list to our all notes list.
        allNotes.addAll(newList)
        // on below line we are calling notify data
        // change method to notify our adapter.
        notifyDataSetChanged()
    }

}

interface NoteClickInterface {

    fun onNoteClick(note: Note)

}

interface NoteClickDeleteInterface {
    // creating a method for click
    // action on delete image view.
    fun onDeleteIconClick(note: Note)
}
