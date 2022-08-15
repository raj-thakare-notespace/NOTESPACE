package com.startup.notespace.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.startup.notespace.R
import com.startup.notespace.ViewDocumentActivity
import com.startup.notespace.models.MyLibrary3Model
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

// Adapter to view pdf from library for current user
class MyLibrary3Adapter(val context: Context, var arrayList: ArrayList<MyLibrary3Model>) :
    RecyclerView.Adapter<MyLibrary3Adapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var docName: TextView = itemView.findViewById(R.id.docNamePdfOnly)
        var threeDotsDeletePdf: ImageView = itemView.findViewById(R.id.deleteThreeDotsItemPdfOnly)
    }

    fun setFilteredList(filteredList: ArrayList<MyLibrary3Model>) {
        this.arrayList = filteredList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return MyLibrary3Adapter.ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_pdf_only_rv, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.docName.text = arrayList[position].pdfName
        val url = arrayList[holder.adapterPosition].pdfUrl

        val model = arrayList[holder.adapterPosition]

        // To handle delete
        holder.threeDotsDeletePdf.setOnClickListener {
            val builder = MaterialAlertDialogBuilder(context)
            builder.setTitle("Are you sure you want to Delete?")
            builder.setCancelable(false)
                .setPositiveButton("Yes") { dialog, id ->

                    try {
                        Firebase.database.reference.child("Library")
                            .child(model.uid)
                            .child(model.folderName1)
                            .child(model.folderName2)
                            .child(model.folderName3)
                            .child(model.pdfName)
                            .removeValue().addOnSuccessListener {
                                try {
                                    val reference = FirebaseStorage.getInstance().getReferenceFromUrl(url)
                                    Log.i("Thakare", reference.toString())
                                    reference.delete().addOnSuccessListener {
                                        Toast.makeText(context, model.pdfName + " Deleted", Toast.LENGTH_SHORT).show()
                                    }
                                    Firebase.database.reference.child("libraryOfPdfUrls")
                                        .child(model.uid)
                                        .child(model.folderName1)
                                        .child(model.folderName2)
                                        .child(model.folderName3)
                                        .child(model.pdfName)
                                        .removeValue()
                                } catch (e: Exception) {
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
        }

        holder.itemView.setOnClickListener {
            val pdfPath = model.pdfUrl
            val intent = Intent(context, ViewDocumentActivity::class.java)
            intent.putExtra("path", pdfPath)
            intent.putExtra("pdfName", model.pdfName)
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