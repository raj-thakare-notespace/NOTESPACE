package com.startup.notespace.ui.group

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.startup.notespace.R
import com.startup.notespace.adapters.GroupAdapter
import com.startup.notespace.models.AllChatModel
import com.startup.notespace.models.Group
import com.startup.notespace.usefulClasses.RandomCodeGenerator
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class GroupFragment : Fragment() {

    companion object {
        fun newInstance() = GroupFragment()
    }

    private lateinit var groupNotFoundRL: RelativeLayout

    private lateinit var viewModel: GroupViewModel

    lateinit var searchView: SearchView

    lateinit var groupToolbar: MaterialToolbar
    lateinit var groupRecyclerView: RecyclerView
    lateinit var groupAdapter: GroupAdapter
    var arrayList = ArrayList<Group>()

    lateinit var dialog: AlertDialog

    var uniqueGroupId: String = ""

    var arrayListOfUid = ArrayList<String>()

    override fun onStart() {
        super.onStart()
        groupAdapter.notifyDataSetChanged()
        try {
            FirebaseDatabase.getInstance().reference.child("users")
                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                .child("my_groups").addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(!snapshot.exists()){
                            groupNotFoundRL.visibility = View.VISIBLE
                        }
                        else {
                            groupNotFoundRL.visibility = View.GONE
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
        } catch (e: Exception) {
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchView = view.findViewById(R.id.searchViewGroup)
        groupToolbar = view.findViewById(R.id.groupToolBar)
        groupRecyclerView = view.findViewById(R.id.group_recycler_view)
        groupAdapter = GroupAdapter(view.context, arrayList)

        groupNotFoundRL = view.findViewById(R.id.noGroupFoundRL)

        groupRecyclerView.layoutManager = LinearLayoutManager(view.context)
        groupRecyclerView.adapter = groupAdapter


        groupRecyclerView.setHasFixedSize(false)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchView.clearFocus()
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText)
                return true
            }

        })

        try {
            FirebaseDatabase.getInstance().reference.child("users")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            arrayListOfUid.clear()
                            for (item in snapshot.children) {
                                arrayListOfUid.add(item.key.toString())
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
        } catch (e: Exception) {
        }

        // ALERT DIALOG TO CREATE GROUP
        val builder = MaterialAlertDialogBuilder(view.context)
        builder.setTitle("Create group")

        var view = layoutInflater.inflate(R.layout.create_group_dialog, null)
        val groupUsernameEditText = view.findViewById<EditText>(R.id.groupUsername)
        val groupDisplayName = view.findViewById<EditText>(R.id.groupDisplayName)
        val groupJoinCode = view.findViewById<EditText>(R.id.groupJoinCode)
        val okButton = view.findViewById<Button>(R.id.okButtonGroup)
        val cancelButton = view.findViewById<Button>(R.id.cancelButtonGroup)

        builder.setView(view)
        dialog = builder.create()

        try {
            FirebaseDatabase.getInstance().reference.child("users")
                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                .child("my_groups")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            arrayList.clear()
                            for (dataSnapshot in snapshot.children) {
                                val group = dataSnapshot.getValue(Group::class.java)
                                val groupUid = group!!.uid
                                try {
                                    FirebaseDatabase.getInstance().reference.child("users")
                                        .child(groupUid)
                                        .addValueEventListener(object : ValueEventListener {
                                            override fun onDataChange(snapshot: DataSnapshot) {
                                                if (snapshot.exists()) {
                                                    val model = snapshot.getValue(Group::class.java)
                                                    arrayList.add(model!!)
                                                    groupAdapter.notifyDataSetChanged()
                                                    Log.i("tiger", model.toString())
                                                    if (arrayList.isNotEmpty()) {
                                                        searchView.clearFocus()
                                                        groupNotFoundRL.visibility = View.GONE
                                                        groupRecyclerView.visibility = View.VISIBLE
                                                    }
                                                } else {
                                                    FirebaseDatabase.getInstance().reference.child("users")
                                                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                                        .child("my_groups").child(groupUid)
                                                        .removeValue()
                                                }
                                            }
                                            override fun onCancelled(error: DatabaseError) {
                                                TODO("Not yet implemented")
                                            }

                                        })
                                } catch (e: Exception) {
                                }

                            }

                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })
        }
        catch (e: Exception) {
        }

        uniqueGroupId = RandomCodeGenerator().generateAlphaNumeric(10)

        groupToolbar.setOnMenuItemClickListener {
            when (it.itemId) {

                R.id.createGroup -> {

                    dialog.show()

                    okButton.setOnClickListener {

                        if (!arrayListOfUid.contains(uniqueGroupId)) {

                            try {
                                FirebaseDatabase.getInstance().reference.child("users")
                                    .addValueEventListener(object : ValueEventListener {
                                        override fun onDataChange(snapshot: DataSnapshot) {

                                            if (snapshot.exists()) {
                                                var ans = false
                                                for (i in snapshot.children) {
                                                    arrayListOfUid.add(i.key.toString())
                                                }
                                                for (item in snapshot.children) {
                                                    val username = item.child("username").value
                                                    if (username == groupUsernameEditText.text.toString()) {
                                                        ans = true
                                                        break
                                                    }
                                                }
                                                if (ans) {
                                                    groupUsernameEditText.error = "Username already exists"
                                                } else if (groupUsernameEditText.text.toString()
                                                        .contains(" ")
                                                ) {
                                                    groupUsernameEditText.error =
                                                        "Empty spaces not allowed."
                                                } else if (groupJoinCode.text.toString().length <= 7) {
                                                    groupJoinCode.error =
                                                        "Minimum 8 characters long."
                                                } else {
                                                    if (groupUsernameEditText.text.isNotEmpty() && groupDisplayName.text.isNotEmpty() && groupJoinCode.text.isNotEmpty() && !arrayListOfUid.contains(
                                                            uniqueGroupId
                                                        )
                                                    ) {


                                                        var group = Group()
                                                        group.createdBy =
                                                            FirebaseAuth.getInstance().currentUser!!.uid
                                                        group.username =
                                                            groupUsernameEditText.text.toString()
                                                        group.uid = uniqueGroupId
                                                        group.displayName =
                                                            groupDisplayName.text.toString()
                                                        group.joinCode =
                                                            groupJoinCode.text.toString()

                                                        val groupUsername =
                                                            groupUsernameEditText.text.toString()

                                                        try {
                                                            FirebaseDatabase.getInstance().reference.child(
                                                                "users"
                                                            )
                                                                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                                                .child("my_groups")
                                                                .child(uniqueGroupId)
                                                                .setValue(group)
                                                                .addOnCompleteListener {
                                                                    if (it.isSuccessful) {
                                                                        FirebaseDatabase.getInstance().reference.child("users")
                                                                            .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                                                            .child("my_created_groups")
                                                                            .child(uniqueGroupId)
                                                                            .setValue(group)
                                                                    }
                                                                }
                                                        } catch (e: Exception) {
                                                        }

                                                        lateinit var currentUserModel: AllChatModel

                                                        try {
                                                            FirebaseDatabase.getInstance().reference.child("users")
                                                                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                                                .addValueEventListener(object :
                                                                    ValueEventListener {
                                                                    override fun onDataChange(
                                                                        snapshot: DataSnapshot
                                                                    ) {
                                                                        if (snapshot.exists()) {
                                                                            currentUserModel =
                                                                                snapshot.getValue(
                                                                                    AllChatModel::class.java
                                                                                )!!
                                                                        }
                                                                    }

                                                                    override fun onCancelled(error: DatabaseError) {
                                                                        TODO("Not yet implemented")
                                                                    }

                                                                })
                                                        } catch (e: Exception) {
                                                        }

                                                        try {
                                                            FirebaseDatabase.getInstance().reference.child(
                                                                "users"
                                                            )
                                                                .child(uniqueGroupId)
                                                                .setValue(group)
                                                                .addOnCompleteListener {
                                                                    FirebaseDatabase.getInstance().reference.child(
                                                                        "users"
                                                                    )
                                                                        .child(uniqueGroupId)
                                                                        .child("members")
                                                                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                                                        .setValue(currentUserModel)
                                                                        .addOnCompleteListener {
                                                                            if (it.isSuccessful) {
                                                                                groupDisplayName.text.clear()
                                                                                groupJoinCode.text.clear()
                                                                                groupUsernameEditText.text.clear()
                                                                                Toast.makeText(view.context, "Group Created Successfully.", Toast.LENGTH_SHORT).show()
                                                                                groupAdapter.notifyDataSetChanged()
                                                                            }
                                                                        }

                                                                }
                                                        } catch (e: Exception) {
                                                        }

                                                        dialog.dismiss()
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

                    }
                    cancelButton.setOnClickListener {
                        dialog.dismiss()
                    }


                    true
                }

                else -> true
            }
        }
    }

    private fun filterList(text: String?) {
        var filteredList = java.util.ArrayList<Group>()
        for (item in arrayList) {
            if (text != null) {
                if (item.displayName.toLowerCase().contains(text.toLowerCase())) {
                    filteredList.add(item)
                    groupNotFoundRL.visibility = View.GONE
                    groupRecyclerView.visibility = View.VISIBLE
                }
            }
        }

        if (filteredList.isEmpty()) {
            groupNotFoundRL.visibility = View.VISIBLE
            groupRecyclerView.visibility = View.GONE
        } else {
            groupAdapter.setFilteredList(filteredList)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.group_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(GroupViewModel::class.java)
        // TODO: Use the ViewModel
    }

}