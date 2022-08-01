package com.example.notespace.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.notespace.CreateOnlineNoteActivity
import com.example.notespace.NoteDetailActivity
import com.example.notespace.R
import com.example.notespace.models.NoteModel
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class OnlineNoteAdapter(val context: Context, var arrayList: ArrayList<NoteModel>) :
    RecyclerView.Adapter<OnlineNoteAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var noteTitle: TextView = itemView.findViewById(R.id.idNoteTitle)
        var noteDescription: TextView = itemView.findViewById(R.id.idNoteDescription)
        var cardView: MaterialCardView = itemView.findViewById(R.id.itemCardView)
        var noteImageRV: ImageView = itemView.findViewById(R.id.RVIVNote)
        var lock: ImageView = itemView.findViewById(R.id.lockIV)

    }

    fun setFilteredList(filteredList: ArrayList<NoteModel>) {
        this.arrayList = filteredList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_online_note_rv, parent, false)
        )
    }

    @SuppressLint("Range")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val model = arrayList[holder.adapterPosition]


        Glide.with(context)
            .load(arrayList[position].image)
            .into(holder.noteImageRV)

        holder.noteTitle.text = arrayList[position].title
        holder.noteDescription.text = arrayList[position].description

        holder.cardView.setCardBackgroundColor(Color.parseColor(arrayList[position].color))

        if (arrayList[position].isPrivate) {
            holder.lock.visibility = View.VISIBLE
        } else {
            holder.lock.visibility = View.INVISIBLE
        }

        if (arrayList[position].uid.toString() == FirebaseAuth.getInstance().currentUser!!.uid) {

            holder.itemView.setOnClickListener {
                val intent = Intent(context, CreateOnlineNoteActivity::class.java)
                intent.putExtra("noteCode", "Edit")
                intent.putExtra("noteTitle", arrayList[position].title)
                intent.putExtra("noteImageUri", arrayList[position].image)
                intent.putExtra("noteDesc", arrayList[position].description)
                context.startActivity(intent)
            }
        } else {
            holder.itemView.setOnClickListener {
                val intent = Intent(context, NoteDetailActivity::class.java)
                intent.putExtra("uid", arrayList[position].uid)
                intent.putExtra("noteTitle", arrayList[position].title)
                context.startActivity(intent)
            }
        }



        holder.itemView.setOnLongClickListener {

            val builder = MaterialAlertDialogBuilder(context)
            builder.setTitle("Are you sure you want to Delete?")
            builder.setCancelable(false)
                .setPositiveButton("Yes") { dialog, id ->

                    try {
                        FirebaseDatabase.getInstance().reference.child("notes")
                            .child(FirebaseAuth.getInstance().currentUser!!.uid)
                            .child(arrayList[position].title)
                            .removeValue().addOnCompleteListener {
                                if (it.isSuccessful) {
                                    Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_SHORT).show()
                                    try {
                                        if (model.image.isNotEmpty()) {
                                            val reference = FirebaseStorage.getInstance().getReferenceFromUrl(model.image)
                                            reference!!.delete()
                                        }
                                    } catch (e: Exception) {
                                    }

                                }
                            }
                    } catch (e: Exception) {
                    }
                }
                .setNegativeButton("No") { dialog, id ->
                    dialog.dismiss()
                }
            val alert = builder.create()
            alert.show()

            true

        }

    }

    override fun getItemCount(): Int {
        return arrayList.size
    }
}