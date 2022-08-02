package com.example.notespace.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.notespace.R
import com.example.notespace.models.RequestModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import de.hdodenhof.circleimageview.CircleImageView

class RequestsAdapter(val context: Context,var arrayList: ArrayList<RequestModel>)  : RecyclerView.Adapter<RequestsAdapter.ViewHolder>(){

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val username: TextView = itemView.findViewById(R.id.usernameTVFollowRequest)
        val profilePicture: CircleImageView = itemView.findViewById(R.id.profilePictureFollowRequest)
        val acceptButton : ImageView = itemView.findViewById(R.id.acceptButton)
        val rejectButton : ImageView = itemView.findViewById(R.id.rejectButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestsAdapter.ViewHolder {
        return RequestsAdapter.ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_follow_request, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RequestsAdapter.ViewHolder, position: Int) {
        val model = arrayList[holder.adapterPosition]
        Glide.with(context)
            .load(model.profilePicture)
            .placeholder(R.drawable.profile_placeholder)
            .into(holder.profilePicture)

        holder.username.text = model.username

        holder.acceptButton.setOnClickListener {

            try {
                FirebaseDatabase.getInstance().reference.child("users")
                    .child(Firebase.auth.currentUser!!.uid)
                    .child("followers")
                    .child(model.uid)
                    .setValue(model)
                    .addOnCompleteListener {
                        Firebase.database.reference.child("users")
                            .child(Firebase.auth.currentUser!!.uid)
                            .child("request_list")
                            .child(model.uid)
                            .removeValue().addOnSuccessListener {
                                Toast.makeText(context,"Request accepted.",Toast.LENGTH_SHORT).show()
                            }
                    }
            } catch (e: Exception) {
            }

        }


        holder.rejectButton.setOnClickListener {

            try {
                Firebase.database.reference.child("users")
                    .child(Firebase.auth.currentUser!!.uid)
                    .child("request_list")
                    .child(model.uid)
                    .removeValue().addOnCompleteListener {
                        if(it.isSuccessful){
                            Toast.makeText(context, "Removed Successfully", Toast.LENGTH_SHORT).show()
                            FirebaseDatabase.getInstance().reference.child("users")
                                .child(model.uid)
                                .child("following")
                                .child(Firebase.auth.currentUser!!.uid)
                                .removeValue()
                        }
                    }
            } catch (e: Exception) {
            }
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }
}