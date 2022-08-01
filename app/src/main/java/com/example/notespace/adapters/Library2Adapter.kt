package com.example.notespace.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.notespace.*
import com.example.notespace.models.MyLibrary2Model

// Adapter for folder in folder activity for other users
class Library2Adapter(val context: Context, var arrayList: ArrayList<MyLibrary2Model>) :
    RecyclerView.Adapter<Library2Adapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var docName: TextView = itemView.findViewById(R.id.docName)
        var folderName: TextView = itemView.findViewById(R.id.folderNamePdfItem)
        var threeDotsDeletePdf: ImageView = itemView.findViewById(R.id.deleteThreeDotsItemPdf)
        var rlforpdf: RelativeLayout = itemView.findViewById(R.id.itemPdfRL)
        var rlforfolder: RelativeLayout = itemView.findViewById(R.id.itemFolderRL)
        var deleteThreeDotsFolder: ImageView = itemView.findViewById(R.id.deleteThreeDotsFolder)

    }

    fun setFilteredList(filteredList: ArrayList<MyLibrary2Model>) {
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

        holder.docName.text = arrayList[position].pdfName

        Log.i("hororo",arrayList[position].folderName3)

        if (arrayList[position].folderName3 == "folderTrue" || arrayList[position].folderName3.isNotEmpty() || arrayList[position].pdfName.isEmpty()) {
            holder.rlforpdf.visibility = View.GONE
            holder.rlforfolder.visibility = View.VISIBLE
            holder.folderName.text = arrayList[position].folderName3
        } else {
            holder.rlforfolder.visibility = View.GONE
            holder.rlforpdf.visibility = View.VISIBLE
        }


        holder.rlforfolder.setOnClickListener {
            val intent = Intent(context, Library3::class.java)
            intent.putExtra("folderName3", arrayList[position].folderName3)
            intent.putExtra("folderName2", arrayList[position].folderName2)
            intent.putExtra("folderName1", arrayList[position].folderName1)
            intent.putExtra("uid", arrayList[position].uid)
            context.startActivity(intent)
        }

        holder.rlforpdf.setOnClickListener {
            val pdfPath = arrayList[position].pdfUrl
            val intent = Intent(context, ViewDocumentActivity::class.java)
            intent.putExtra("path", pdfPath)
            intent.putExtra("pdfName", arrayList[position].pdfName)
            context.startActivity(intent)

        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }
}