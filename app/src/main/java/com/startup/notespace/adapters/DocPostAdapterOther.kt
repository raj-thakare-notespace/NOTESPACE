package com.startup.notespace.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.startup.notespace.R
import com.startup.notespace.ViewDocumentActivityPost
import com.startup.notespace.models.DocPostModel

class DocPostAdapterOther(val context: Context, var arrayList: ArrayList<DocPostModel>) :
    RecyclerView.Adapter<DocPostAdapterOther.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var docName: TextView = itemView.findViewById(R.id.nameDocPostTV)
    }

    fun setFilteredList(filteredList: ArrayList<DocPostModel>) {
        this.arrayList = filteredList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return DocPostAdapterOther.ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.document_post_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.docName.text = arrayList[holder.adapterPosition].docName

        holder.itemView.setOnClickListener {
            val pdfPath = arrayList[holder.adapterPosition].docUrl
            val intent = Intent(context, ViewDocumentActivityPost::class.java)
            intent.putExtra("path", pdfPath)
            intent.putExtra("pdfName", arrayList[holder.adapterPosition].docName)
            context.startActivity(intent)
        }

    }


    override fun getItemCount(): Int {
        return arrayList.size
    }
}