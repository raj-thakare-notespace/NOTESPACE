package com.startup.notespace.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.startup.notespace.ChatActivity
import com.startup.notespace.R
import com.startup.notespace.models.AllChatModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import de.hdodenhof.circleimageview.CircleImageView

// Adapter for all chat activity, to get recently chatted users.
class AllChatAdapter(val context: Context, val arrayList: ArrayList<AllChatModel>) :
    RecyclerView.Adapter<AllChatAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val displayName: TextView = itemView.findViewById(R.id.displayNameAllChat)
        val username: TextView = itemView.findViewById(R.id.usernameAllChat)
        val profilePicture: CircleImageView = itemView.findViewById(R.id.profilePictureAllChat)
        val cardViewMsgBadge: CardView = itemView.findViewById(R.id.cardViewMsgBadge)
        val noOfNewMsgTV: TextView = itemView.findViewById(R.id.noOfNewMsgTV)
        val delete: ImageView = itemView.findViewById(R.id.deleteIconThreeDots)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.all_chat_item_rv, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val model = arrayList[holder.adapterPosition]
        holder.displayName.text = model.displayName
        holder.username.text = model.username
        Glide.with(context)
            .load(model.profilePicture)
            .placeholder(R.drawable.profile_placeholder)
            .into(holder.profilePicture)

        var arrayListNoOfMessages = ArrayList<String>()

        try {
            FirebaseDatabase.getInstance().reference.child("chats")
                .child(model.uid + Firebase.auth.currentUser!!.uid)
                .child("messages").addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            arrayListNoOfMessages.clear()
                            for (item in snapshot.children) {
                                arrayListNoOfMessages.add(item.key.toString())
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })
        } catch (e: Exception) {
        }

        try {
            FirebaseDatabase.getInstance().reference.child("chats")
                .child(model.uid + Firebase.auth.currentUser!!.uid)
                .child("noOfMessages").addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            if (arrayListNoOfMessages.size > snapshot.value.toString().toLong()) {
                                val numberOfNewMessages =
                                    arrayListNoOfMessages.size - snapshot.value.toString().toLong()
                                holder.cardViewMsgBadge.visibility = View.VISIBLE
                                holder.noOfNewMsgTV.text = numberOfNewMessages.toString()
                            } else {
                                holder.cardViewMsgBadge.visibility = View.INVISIBLE
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
        } catch (e: Exception) {
        }

        // To handle delete chat user
        holder.delete.setOnClickListener {
            val builder = MaterialAlertDialogBuilder(context)
            builder.setTitle("Are you sure you want to Delete?")
            builder.setCancelable(false)
                .setPositiveButton("Yes") { dialog, id ->

                    try {
                        Firebase.database.reference.child("users")
                            .child(Firebase.auth.currentUser!!.uid)
                            .child("chatted_list")
                            .child(model.uid)
                            .removeValue().addOnSuccessListener {
                                Toast.makeText(context, "deleted successfully.", Toast.LENGTH_SHORT)
                                    .show()

                            }
                    } catch (e: Exception) {
                    }

                }
                .setNegativeButton("No") { dialog, id ->
                    dialog.dismiss()
                }
            val alert = builder.create()
            alert.show()
            notifyDataSetChanged()
        }


        holder.itemView.setOnClickListener {
            holder.cardViewMsgBadge.visibility = View.INVISIBLE
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("username", model.username)
            intent.putExtra("userId", model.uid)
            intent.putExtra("profileImage", model.profilePicture)
            context.startActivity(intent)
        }


    }

    override fun getItemCount(): Int {
        return arrayList.size
    }
}