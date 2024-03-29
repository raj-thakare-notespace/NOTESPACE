package com.startup.notespace.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.startup.notespace.MyLibrary3
import com.startup.notespace.R
import com.startup.notespace.ViewDocumentActivity
import com.startup.notespace.models.MyLibrary2Model
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

// Adapter for folder in folder activity
class MyLibrary2Adapter(val context: Context, var arrayList: ArrayList<MyLibrary2Model>) :
    RecyclerView.Adapter<MyLibrary2Adapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var docName: TextView = itemView.findViewById(R.id.docName)
        var folderName: TextView = itemView.findViewById(R.id.folderNamePdfItem)
        var threeDotsDeletePdf: ImageView = itemView.findViewById(R.id.deleteThreeDotsItemPdf)
        var deleteThreeDotsFolder: ImageView = itemView.findViewById(R.id.deleteThreeDotsFolder)
        var rlforpdf: RelativeLayout = itemView.findViewById(R.id.itemPdfRL)
        var rlforfolder: RelativeLayout = itemView.findViewById(R.id.itemFolderRL)
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
        holder.docName.text = arrayList[position].pdfName
        val url = arrayList[holder.adapterPosition].pdfUrl

        val model = arrayList[holder.adapterPosition]

        val reference = FirebaseStorage.getInstance()
        var uidArrayList = ArrayList<String>()
        if (!model.folderName1.isNullOrEmpty() && !model.folderName2.isNullOrEmpty() && !model.folderName3.isNullOrEmpty()) {

            try {
                Firebase.database.reference.child("libraryOfPdfUrls")
                    .child(model.uid)
                    .child(model.folderName1)
                    .child(model.folderName2)
                    .child(model.folderName3)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if(snapshot.exists()){
                                uidArrayList.clear()
                                for (item in snapshot.children) {
                                    var count = 0
                                    for (i in item.children) {
                                        Log.i("abcdefgh", i.value.toString())
                                        count++
                                    }
                                    if (count > 0 || item.value.toString() == "folderTrue") {
                                        continue
                                    } else {
                                        uidArrayList.add(item.value.toString())
                                    }
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                    })
            } catch (e: Exception) {
            }
        }

        if (model.folderName3 == "folderTrue" || model.folderName3.isNotEmpty() || model.pdfName.isEmpty()) {
            holder.rlforpdf.visibility = View.GONE
            holder.rlforfolder.visibility = View.VISIBLE
            holder.folderName.text = model.folderName3
        } else {
            holder.rlforfolder.visibility = View.GONE
            holder.rlforpdf.visibility = View.VISIBLE
        }

        // To handle delete pdf
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
                            .child(model.pdfName!!)
                            .removeValue().addOnSuccessListener {
                                try {
                                    val reference =
                                        FirebaseStorage.getInstance().getReferenceFromUrl(url)
                                    Log.i("Thakare", reference.toString())
                                    reference.delete().addOnSuccessListener {
                                        Toast.makeText(
                                            context,
                                            model.pdfName + " Deleted",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
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

        // To handle delete folder
        holder.deleteThreeDotsFolder.setOnClickListener {

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
                            .removeValue().addOnCompleteListener {
                                if (it.isSuccessful) {
                                    Toast.makeText(
                                        context,
                                        model.folderName3 + " deleted.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    try {
                                        for (item in uidArrayList) {
                                            val ref = reference.getReferenceFromUrl(item)
                                            ref.delete()
                                        }
                                        Firebase.database.reference.child("libraryOfPdfUrls")
                                            .child(model.uid)
                                            .child(model.folderName1)
                                            .child(model.folderName2)
                                            .child(model.folderName3)
                                            .removeValue()
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
        }

        holder.rlforfolder.setOnClickListener {
            val intent = Intent(context, MyLibrary3::class.java)
            intent.putExtra("folderName3", model.folderName3)
            intent.putExtra("folderName2", model.folderName2)
            intent.putExtra("folderName1", model.folderName1)
            intent.putExtra("uid", model.uid)
            context.startActivity(intent)
        }

        holder.rlforpdf.setOnClickListener {
            val pdfPath = model.pdfUrl
            val intent = Intent(context, ViewDocumentActivity::class.java)
            intent.putExtra("path", pdfPath)
            intent.putExtra("pdfName", model.pdfName)
            context.startActivity(intent)

        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }
}