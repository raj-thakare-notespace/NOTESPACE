package com.startup.notespace.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.startup.notespace.R
import com.startup.notespace.Library2
import com.startup.notespace.ViewDocumentActivity
import com.startup.notespace.models.MyLibrary1Model

// Adapter for folder in folder activity for other users
class Library1Adapter(val context: Context, var arrayList: ArrayList<MyLibrary1Model>) :
    RecyclerView.Adapter<Library1Adapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var docName: TextView = itemView.findViewById(R.id.docName)
        var folderName: TextView = itemView.findViewById(R.id.folderNamePdfItem)
        var threeDotsDeletePdf: ImageView = itemView.findViewById(R.id.deleteThreeDotsItemPdf)
        var rlforpdf: RelativeLayout = itemView.findViewById(R.id.itemPdfRL)
        var rlforfolder: RelativeLayout = itemView.findViewById(R.id.itemFolderRL)
        var deleteThreeDotsFolder: ImageView = itemView.findViewById(R.id.deleteThreeDotsFolder)

    }

    fun setFilteredList(filteredList: ArrayList<MyLibrary1Model>) {
        this.arrayList = filteredList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_pdf_rv, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.threeDotsDeletePdf.visibility = View.GONE
        holder.deleteThreeDotsFolder.visibility = View.GONE

        if (arrayList[holder.adapterPosition].folderName2 == "folderTrue" || arrayList[holder.adapterPosition].folderName2.isNotEmpty()) {
            holder.rlforpdf.visibility = View.GONE
            holder.rlforfolder.visibility = View.VISIBLE
            holder.folderName.text = arrayList[holder.adapterPosition].folderName2
        } else {
            holder.docName.text = arrayList[holder.adapterPosition].pdfName
            holder.rlforfolder.visibility = View.GONE
            holder.rlforpdf.visibility = View.VISIBLE
        }

        holder.rlforfolder.setOnClickListener {
            val intent = Intent(context, Library2::class.java)
            intent.putExtra("folderName1", arrayList[holder.adapterPosition].folderName1)
            intent.putExtra("folderName2", arrayList[holder.adapterPosition].folderName2)
            intent.putExtra("uid", arrayList[holder.adapterPosition].uid)
            context.startActivity(intent)
        }

        holder.rlforpdf.setOnClickListener {
            val pdfPath = arrayList[holder.adapterPosition].pdfUrl
            val intent = Intent(context, ViewDocumentActivity::class.java)
            intent.putExtra("path", pdfPath)
            intent.putExtra("pdfName", arrayList[holder.adapterPosition].pdfName)
            context.startActivity(intent)

        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }
}