package com.example.notespace.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.notespace.NoteDetailActivity
import com.example.notespace.R
import com.example.notespace.models.NoteModel
import com.google.android.material.card.MaterialCardView

class OnlineNoteAdapterOther(val context: Context, var arrayList: ArrayList<NoteModel>) :
    RecyclerView.Adapter<OnlineNoteAdapterOther.ViewHolder>() {

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

        holder.lock.visibility = View.GONE

        Glide.with(context)
            .load(arrayList[position].image)
            .into(holder.noteImageRV)

        holder.noteTitle.text = arrayList[position].title
        holder.noteDescription.text = arrayList[position].description

        holder.cardView.setCardBackgroundColor(Color.parseColor(arrayList[position].color))

            holder.itemView.setOnClickListener {
                val intent = Intent(context, NoteDetailActivity::class.java)
                intent.putExtra("uid",arrayList[position].uid)
                intent.putExtra("noteTitle",arrayList[position].title)
                context.startActivity(intent)
            }

    }

    override fun getItemCount(): Int {
        return arrayList.size
    }
}