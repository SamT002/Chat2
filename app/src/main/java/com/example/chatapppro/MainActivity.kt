package com.example.chatapppro

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapppro.contact.ContactActivity
import com.example.chatapppro.model.Chat
import com.example.chatapppro.registration.NumberActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class MainActivity : AppCompatActivity() {

    private val recyclerView by lazy { findViewById<RecyclerView>(R.id.main_recycler) }
    private val adapter:MainAdapter by lazy { MainAdapter() }
    private var list: MutableList<Chat> = ArrayList<Chat>()
    private val myId = FirebaseAuth.getInstance().uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(FirebaseAuth.getInstance().currentUser == null){
            startActivity(Intent(this, NumberActivity::class.java))
        }

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            startActivity(Intent(this, ContactActivity::class.java))
        }

        init()
        getChats()
    }

    private fun getChats() {
        FirebaseFirestore.getInstance().collection("chats")
            .whereArrayContains("userIds", myId.toString())
            .addSnapshotListener { value, error ->
                for (change in value!!.documentChanges) {
                    when (change.type) {
                        DocumentChange.Type.ADDED -> {
                            val chat = change.document.toObject(Chat::class.java)
                            chat.id = change.document.id
                            list.add(chat)
                        }
                    }
                }
                adapter.notifyDataSetChanged()
            }
    }

    fun init(){
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        adapter.setUser(list)
    }




}