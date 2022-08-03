package com.example.notespace.adapters


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.notespace.R
import com.example.notespace.models.MessageModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat

// Adapter to load messages of chats
class MessageAdapter(val context: Context, private val messageModelList: ArrayList<MessageModel>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val ITEM_RECEIVE = 1
    private val ITEM_SENT = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 1) {
            val view: View = LayoutInflater.from(context).inflate(R.layout.received, parent, false)
            return ReceiveViewHolder(view)
        } else {
            val view: View = LayoutInflater.from(context).inflate(R.layout.sent, parent, false)
            return SentViewHolder(view)
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val currentMessage = messageModelList[holder.adapterPosition]

        holder.itemView.setOnLongClickListener {

            val builder = MaterialAlertDialogBuilder(context)
            builder.setTitle("Are you sure you want to Delete?")
            builder.setCancelable(false)
                .setPositiveButton("Yes") { dialog, id ->

                    try {// to check in person to person chat
                        FirebaseDatabase.getInstance().reference.child("chats")
                            .child(currentMessage.receiverId + currentMessage.senderId)
                            .child("messages")
                            .addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if(snapshot.exists()){
                                        for (item in snapshot.children) {
                                            var messageModell = item.getValue(MessageModel::class.java)
                                            val currentMessageId = item.key
                                            if (messageModell!!.time == currentMessage.time) {
                                                FirebaseDatabase.getInstance().reference.child("chats")
                                                    .child(currentMessage.receiverId + currentMessage.senderId)
                                                    .child("messages")
                                                    .child(currentMessageId.toString()).removeValue()
                                                    .addOnCompleteListener {
                                                        if (it.isSuccessful) {
                                                            FirebaseDatabase.getInstance().reference.child("chats")
                                                                .child(currentMessage.senderId + currentMessage.receiverId)
                                                                .child("messages")
                                                                .addValueEventListener(object : ValueEventListener {
                                                                    override fun onDataChange(snapshot: DataSnapshot) {
                                                                        if(snapshot.exists()){
                                                                            for (item in snapshot.children) {
                                                                                var messageModell = item.getValue(MessageModel::class.java)
                                                                                val currentMessageId = item.key
                                                                                if (messageModell!!.time == currentMessage.time) {
                                                                                    FirebaseDatabase.getInstance().reference.child("chats")
                                                                                        .child(currentMessage.senderId + currentMessage.receiverId)
                                                                                        .child("messages")
                                                                                        .child(currentMessageId.toString()).removeValue()
                                                                                }
                                                                            }
                                                                        }
                                                                    }

                                                                    override fun onCancelled(error: DatabaseError) {
                                                                        TODO("Not yet implemented")
                                                                    }

                                                                })

                                                        }
                                                    }
                                            }
                                        }
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    TODO("Not yet implemented")
                                }

                            })

                        if(currentMessage.createdBy == Firebase.auth.currentUser!!.uid){
                            // to check in group chat
                            FirebaseDatabase.getInstance().reference.child("groupChat")
                                .child(currentMessage.groupUid.toString())
                                .child("messages")
                                .addValueEventListener(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        if(snapshot.exists()){
                                            for (item in snapshot.children) {
                                                var messageModell = item.getValue(MessageModel::class.java)
                                                val currentMessageId = item.key
                                                if (messageModell!!.time == currentMessage.time) {
                                                    FirebaseDatabase.getInstance().reference.child("groupChat")
                                                        .child(currentMessage.groupUid.toString())
                                                        .child("messages")
                                                        .child(currentMessageId.toString())
                                                        .removeValue().addOnSuccessListener {
                                                            notifyDataSetChanged()
                                                        }
                                                }
                                            }
                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        TODO("Not yet implemented")
                                    }

                                })
                        }

                    } catch (e: Exception) {
                    }

                }
                .setNegativeButton("No") { dialog, id ->
                    dialog.dismiss()
                }
            val alert = builder.create()
            alert.show()

            false
        }


        if (holder.javaClass == SentViewHolder::class.java) {
            val viewHolder = holder as SentViewHolder
            holder.sentMessage.text = currentMessage.message
            val simpleDateFormat = SimpleDateFormat("hh:mm aa - dd/MM/yyyy")
            holder.messageTimeTVSent.text = simpleDateFormat.format(currentMessage.time)
        } else {
            val viewHolder = holder as ReceiveViewHolder
            holder.receiveMessage.text = currentMessage.message
            val simpleDateFormat = SimpleDateFormat("hh:mm aa - dd/MM/yyyy")
            holder.messageTimeTVReceived.text = simpleDateFormat.format(currentMessage.time)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val currentMessage = messageModelList[position]

        if (Firebase.auth.currentUser!!.uid == currentMessage.senderId) {
            return ITEM_SENT
        } else {
            return ITEM_RECEIVE
        }
    }

    override fun getItemCount(): Int {
        return messageModelList.size
    }

    class SentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sentMessage: TextView = itemView.findViewById<TextView>(R.id.text_sent_message)
        val messageTimeTVSent: TextView = itemView.findViewById(R.id.messageTimeTVSent)
    }

    class ReceiveViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val receiveMessage: TextView = itemView.findViewById<TextView>(R.id.text_receive_message)
        val messageTimeTVReceived: TextView = itemView.findViewById(R.id.messageTimeTVReceived)
    }
}