package com.startup.notespace.adapters

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
import com.startup.notespace.NoteDetailActivity
import com.startup.notespace.R
import com.startup.notespace.models.NoteModel
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

        val model = arrayList[holder.adapterPosition]

        holder.lock.visibility = View.GONE

        Glide.with(context)
            .load(model.image)
            .into(holder.noteImageRV)

        holder.noteTitle.text = model.title
        holder.noteDescription.text = model.description

        holder.cardView.setCardBackgroundColor(Color.parseColor(model.color))

            holder.itemView.setOnClickListener {
                val intent = Intent(context, NoteDetailActivity::class.java)
                intent.putExtra("uid",model.uid)
                intent.putExtra("noteTitle",model.title)
                context.startActivity(intent)
            }

    }

    override fun getItemCount(): Int {
        return arrayList.size
    }
}