package com.startup.notespace.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.startup.notespace.GroupChatActivity
import com.startup.notespace.R
import com.startup.notespace.models.Group
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import de.hdodenhof.circleimageview.CircleImageView
import kotlin.collections.ArrayList

// Adapter to show all the groups in group fragment
class GroupAdapter(val context: Context, var arrayList: ArrayList<Group>) :
    RecyclerView.Adapter<GroupAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val displayName: TextView = itemView.findViewById(R.id.group_name)
        val groupProfilePicture: CircleImageView = itemView.findViewById(R.id.group_profile_picture)
        var deleteDots: ImageView = itemView.findViewById(R.id.deleteThreeDotsGroupItem)
        val cardViewMsgBadgeGPItem : CardView = itemView.findViewById(R.id.cardViewMsgBadgeGPItem)
        val noOfNewMsgTVGPItem : TextView = itemView.findViewById(R.id.noOfNewMsgTVGPItem)

    }

    fun setFilteredList(filteredList: ArrayList<Group>) {
        this.arrayList = filteredList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.group_rv_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = arrayList[holder.adapterPosition]
        holder.displayName.text = model.displayName
        Glide.with(context)
            .load(model.profilePicture)
            .placeholder(R.drawable.profile_placeholder)
            .into(holder.groupProfilePicture)

        //Handling new message signal(dot)
        var arrayListNoOfMessages = ArrayList<String>()

        try {
            FirebaseDatabase.getInstance().reference.child("groupChat")
                .child(model.uid)
                .child("messages").addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists()){
                            arrayListNoOfMessages.clear()
                            for(item in snapshot.children){
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


        //Handling new message signal(dot)
        try {
            FirebaseDatabase.getInstance().reference.child("users").child(FirebaseAuth.getInstance().currentUser!!.uid).child("my_groups")
                .child(model.uid)
                .child("noOfMessages").addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()){

                            if(arrayListNoOfMessages.size > snapshot.value.toString().toLong()){
                                val numberOfNewMessages = arrayListNoOfMessages.size - snapshot.value.toString().toLong()
                                holder.cardViewMsgBadgeGPItem.visibility = View.VISIBLE
                                holder.noOfNewMsgTVGPItem.text = numberOfNewMessages.toString()
                            }
                            else{
                                holder.cardViewMsgBadgeGPItem.visibility = View.INVISIBLE
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
        } catch (e: Exception) {
        }


        // to delete the group
        holder.deleteDots.setOnClickListener {


            try {
                val builder = MaterialAlertDialogBuilder(context)
                builder.setTitle("Are you sure you want to Delete?")
                builder.setCancelable(false)
                    .setPositiveButton("Yes") { dialog, id ->
                        if (arrayList[holder.adapterPosition].createdBy == Firebase.auth.currentUser!!.uid) {
                            val ans = arrayList[holder.adapterPosition].createdBy == Firebase.auth.currentUser!!.uid
                            try {
                                FirebaseDatabase.getInstance().reference.child("users")
                                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                    .child("my_created_groups")
                                    .child(arrayList[holder.adapterPosition].uid).removeValue()

                                Firebase.database.reference.child("users")
                                    .child(Firebase.auth.currentUser!!.uid)
                                    .child("my_groups")
                                    .child(arrayList[holder.adapterPosition].uid)
                                    .removeValue()

                                Firebase.database.reference.child("users")
                                    .child(arrayList[holder.adapterPosition].uid)
                                    .removeValue()

                            } catch (e: Exception) {
                            }

                        }

                        else {
                            try {
                                Firebase.database.reference.child("users").child(arrayList[holder.adapterPosition].uid)
                                    .child("members").child(Firebase.auth.currentUser!!.uid)
                                    .removeValue().addOnSuccessListener {
                                        Firebase.database.reference.child("users")
                                            .child(Firebase.auth.currentUser!!.uid)
                                            .child("my_groups")
                                            .child(arrayList[holder.adapterPosition].uid)
                                            .removeValue()
                                    }
                            } catch (e: Exception) {
                            }

                        }
                    }
                    .setNegativeButton("No") { dialog, id ->
                        dialog.dismiss()
                    }
                val alert = builder.create()
                alert.show()
            } catch (e: Exception) {
            }

            notifyDataSetChanged()

        }

        holder.itemView.setOnClickListener {
            holder.cardViewMsgBadgeGPItem.visibility = View.INVISIBLE
            val intent = Intent(context, GroupChatActivity::class.java)
            intent.putExtra("usernameGroup", arrayList[holder.adapterPosition].username)
            intent.putExtra("groupDisplayName", arrayList[holder.adapterPosition].displayName)
            intent.putExtra("createdBy", arrayList[holder.adapterPosition].createdBy)
            intent.putExtra("profilePicture", arrayList[holder.adapterPosition].profilePicture)
            intent.putExtra("uid", arrayList[holder.adapterPosition].uid)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }
}