package com.example.notespace.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.notespace.*
import com.example.notespace.adapters.PostAdapter
import com.example.notespace.models.Post
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

class HomeFragment : Fragment() {

    companion object {
        fun newInstance() = HomeFragment()
    }

    var followingList: ArrayList<String> = ArrayList()

    private lateinit var adapter: PostAdapter

    private lateinit var viewModel: HomeViewModel

    lateinit var fab: FloatingActionButton

    lateinit var createNoteButton: LinearLayout
    lateinit var uploadDocumentButton: LinearLayout
    lateinit var postButton: LinearLayout

    lateinit var recyclerView: RecyclerView

    lateinit var profilePicture: CircleImageView
    lateinit var allChatButton: ImageView

    lateinit var auth: FirebaseAuth
    lateinit var database: FirebaseDatabase


    lateinit var progressBar: ProgressBar


    var postList = ArrayList<Post>()


    private fun checkFollow() {

        try {
            val reference = FirebaseDatabase.getInstance().reference.child("users")
                .child(Firebase.auth.currentUser!!.uid)
                .child("following")
            reference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        followingList.clear()
                        for (dataSnapshot in snapshot.children) {
                            followingList.add(dataSnapshot.key.toString())
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

    private fun getPosts() {
        val databaseReference = FirebaseDatabase.getInstance().reference.child("posts")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {


                    postList.clear()

                    var stack = Stack<Post>()

                    for (dataSnapshot in snapshot.children) {

                        val post = dataSnapshot.getValue(Post::class.java)
                        post!!.postId = dataSnapshot.key.toString()


                        try {
                            if (post.postedBy == Firebase.auth.currentUser!!.uid) {
                                stack.add(post)
                            }
                        } catch (e: Exception) {
                        }

                        if (followingList.contains(post.postedBy)) {
                            stack.add(post)
                        }
                    }

                    if (stack.isEmpty()) {
                        progressBar.visibility = View.GONE
                    }

                    for (i in 0 until stack.size) {
                        var i = stack.pop()
                        postList.add(i)
                        progressBar.visibility = View.GONE
                        adapter.notifyDataSetChanged()
                    }

                }
                else if(!snapshot.exists()){
                    progressBar.visibility = View.GONE
                }

            }


            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
                progressBar.visibility = View.GONE
            }

        })
    }

    override fun onStart() {
        super.onStart()
        getPosts()
        adapter.notifyDataSetChanged()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        progressBar = view.findViewById(R.id.homeFragmentProgressbar)


        progressBar.visibility = View.VISIBLE




        auth = FirebaseAuth.getInstance()
        database = Firebase.database

        profilePicture = view.findViewById<CircleImageView>(R.id.profilePicture)

        recyclerView = view.findViewById(R.id.recyclerViewPost)

        allChatButton = view.findViewById(R.id.allChatButtonIV)

        allChatButton.setOnClickListener {
            startActivity(Intent(view.context, AllChatsActivity::class.java))
        }


        try {
            database.reference.child("users").child(Firebase.auth.currentUser!!.uid)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val profilePictureL = snapshot.child("profilePicture").value

                            Glide.with(view.context)
                                .load(profilePictureL.toString())
                                .placeholder(R.drawable.profile_placeholder)
                                .into(profilePicture)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
        } catch (e: Exception) {
        }


        adapter = PostAdapter(view.context, postList)

        recyclerView.layoutManager = LinearLayoutManager(view.context)

        recyclerView.adapter = adapter

        checkFollow()

        getPosts()


        fab = view.findViewById(R.id.fab)


        profilePicture.setOnClickListener {
            val intent = Intent(context, ProfileActivity::class.java)
            startActivity(intent)

        }

        fab.setOnClickListener {

            val dialog = BottomSheetDialog(view.context)
            val view = layoutInflater.inflate(R.layout.bottom_sheet_dialog, null)

            createNoteButton = view.findViewById(R.id.createNoteLL)
            postButton = view.findViewById(R.id.uploadPostLL)
            uploadDocumentButton = view.findViewById(R.id.uploadDocLL)

            postButton.setOnClickListener {
                startActivity(Intent(view.context, MakePostActivity::class.java))
                dialog.dismiss()
            }

            createNoteButton.setOnClickListener {
                val intent = Intent(view.context,CreateOnlineNoteActivity::class.java)
                intent.putExtra("place","home")
                startActivity(intent)
                dialog.dismiss()
            }

            uploadDocumentButton.setOnClickListener {
                val intent = Intent(view.context, MyLibraryActivity::class.java)
                startActivity(intent)
                dialog.dismiss()
            }

            dialog?.setCancelable(true)
            dialog?.setContentView(view)
            dialog?.show()


        }

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        return inflater.inflate(R.layout.home_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        // TODO: Use the ViewModel


    }

}