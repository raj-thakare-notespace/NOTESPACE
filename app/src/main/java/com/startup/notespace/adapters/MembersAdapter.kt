package com.startup.notespace.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.startup.notespace.OtherUsersProfileActivity
import com.startup.notespace.ProfileActivity
import com.startup.notespace.R
import com.startup.notespace.models.GroupMemberModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import de.hdodenhof.circleimageview.CircleImageView

// Adapter to list  down following users or followers of any profile
class MembersAdapter(val context: Context, val arrayList: ArrayList<GroupMemberModel>) :
    RecyclerView.Adapter<MembersAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val displayName: TextView = itemView.findViewById(R.id.displayNameAllChat)
        val username: TextView = itemView.findViewById(R.id.usernameAllChat)
        val profilePicture: CircleImageView = itemView.findViewById(R.id.profilePictureAllChat)
        val deleteIcon: ImageView = itemView.findViewById(R.id.deleteIconThreeDots)

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


        if(Firebase.auth.currentUser!!.uid != model.createdBy ){
            holder.deleteIcon.visibility = View.GONE
        }
        else{
            holder.deleteIcon.visibility = View.VISIBLE
        }

        if(model.uid == model.createdBy){
            holder.deleteIcon.visibility = View.GONE
        }

        holder.deleteIcon.setOnClickListener {

            try {
                val builder = MaterialAlertDialogBuilder(context)
                builder.setTitle("Are you sure you want to remove this user?")
                builder.setCancelable(false)
                    .setPositiveButton("Yes") { dialog, id ->
                        Firebase.database.reference.child("users")
                            .child(arrayList[holder.position].groupUid.toString())
                            .child("members")
                            .addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if(snapshot.exists()){
                                        for (item in snapshot.children) {
                                            if (item.key.toString() == arrayList[holder.position].uid) {
                                                Firebase.database.reference.child("users")
                                                    .child(arrayList[holder.position].groupUid.toString())
                                                    .child("members")
                                                    .child(arrayList[holder.position].uid)
                                                    .removeValue()
                                                    .addOnCompleteListener {
                                                        if (it.isSuccessful) {
                                                            Toast.makeText(
                                                                context,
                                                                "Member Removed",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
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
                    }
                    .setNegativeButton("No") { dialog, id ->
                        dialog.dismiss()
                    }
                val alert = builder.create()
                alert.show()
            } catch (e: Exception) {
            }

        }


        holder.itemView.setOnClickListener {
            if(model.uid == Firebase.auth.currentUser!!.uid){
                val intent = Intent(it.context, ProfileActivity::class.java)
                intent.putExtra("uid", model.uid)
                intent.putExtra("username", model.username)
                intent.putExtra("profilePicture", model.profilePicture)
                context.startActivity(intent)
            }
            else{
                val intent = Intent(it.context, OtherUsersProfileActivity::class.java)
                intent.putExtra("uid", model.uid)
                intent.putExtra("username", model.username)
                intent.putExtra("profilePicture", model.profilePicture)
                context.startActivity(intent)
            }

        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }
}