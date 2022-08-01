package com.example.notespace.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.notespace.R
import com.example.notespace.ViewDocumentActivity
import com.example.notespace.models.MyLibrary3Model

class Library3Adapter(val context: Context, var arrayList: ArrayList<MyLibrary3Model>) :
    RecyclerView.Adapter<Library3Adapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var docName: TextView = itemView.findViewById(R.id.docNamePdfOnly)
        var threeDots : ImageView = itemView.findViewById(R.id.deleteThreeDotsItemPdfOnly)
    }

    fun setFilteredList(filteredList: ArrayList<MyLibrary3Model>) {
        this.arrayList = filteredList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return Library3Adapter.ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_pdf_only_rv, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.docName.text = arrayList[position].pdfName
        holder.threeDots.visibility = View.GONE

        Log.i("ironman", arrayList[position].pdfName)


        holder.itemView.setOnClickListener {
            val pdfPath = arrayList[position].pdfUrl
            val intent = Intent(context, ViewDocumentActivity::class.java)
            intent.putExtra("path", pdfPath)
            intent.putExtra("pdfName", arrayList[position].pdfName)
            context.startActivity(intent)

//            val intent = Intent(Intent.ACTION_VIEW)
//            intent.setDataAndType(Uri.parse(pdfPath),"application/pdf")
//            intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
//            val viewerIntent = Intent.createChooser(intent,"Open PDF")
//            try {
//                context.startActivity(viewerIntent)
//            }
//            catch (e : ActivityNotFoundException){
//                Toast.makeText(context,
//                    "No Application Available to View PDF",
//                    Toast.LENGTH_SHORT).show();
//            }

        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }
}