package com.example.chatapppro.chat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapppro.MainActivity
import com.example.chatapppro.R
import com.example.chatapppro.message.MessageAdapter
import com.example.chatapppro.model.Chat
import com.example.chatapppro.model.Message
import com.example.chatapppro.model.User
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import java.util.*

class ChatActivity : AppCompatActivity() {

    private val recyclerView by lazy { findViewById<RecyclerView>(R.id.chat_recycler) }
    private val toolbar by lazy { findViewById<Toolbar>(R.id.toolbar) }
    private val text by lazy { findViewById<EditText>(R.id.chat_edit_text) }
    private val adapter by lazy { MessageAdapter() }
    private var user: User? = null
    private lateinit var name: String
    private var chat: Chat? = null
    private val myId = FirebaseAuth.getInstance().uid
    private var list: MutableList<Message> = ArrayList<Message>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        user = intent.getSerializableExtra("user") as User?
        chat = intent.getSerializableExtra("chat") as Chat?
        name = intent.getSerializableExtra("name").toString()
        if (chat == null) {
            chat = Chat()
            toolbar.title = user?.name
            val userIds = ArrayList<String>()
            userIds.add(FirebaseAuth.getInstance().uid.toString())
            userIds.add(user!!.id)
            chat?.UserIds = userIds
        } else {
            refleshIsRead()
            toolbar.title = name
            initList()
            getMessages()
        }

    }

    private fun refleshIsRead() {
        val a = chat?.UserIds as MutableList
        a.remove(myId)
        val chatName = a[0]

        FirebaseFirestore.getInstance().collection("chats")
                .document(chat?.id.toString())
                .collection("messages")
                .whereEqualTo("senderId", chatName)
                .get()
                .addOnCompleteListener(OnCompleteListener<QuerySnapshot> { task ->
                    if (task.isSuccessful) {
                        for (document in task.result!!) {
                            document.reference.update("isRead", true)
                        }
                    }
                })
    }


    private fun getMessages() {
        FirebaseFirestore.getInstance().collection("chats").document(chat?.id.toString())
                .collection("messages")
                .addSnapshotListener { value, error ->
                    for (change in value!!.documentChanges) {
                        when (change.type) {
                            DocumentChange.Type.ADDED -> {
                                list.add(change.document.toObject(Message::class.java))

                            }
                        }
                    }
                    adapter.notifyDataSetChanged()
                }
    }

    fun onSend(view: View) {
        Log.i("MyTag", "Onsend")
        val text:String = text.text.toString().trim()
        if (chat?.id == null){
            createChat(text)
        }else {
            sendMessage(text)
        }

    }

    private fun createChat(text: String) {
        Log.i("MyTag", "CreateChat")
        FirebaseFirestore.getInstance().collection("chats").add(chat!!)
            .addOnSuccessListener { documentReference ->
                chat?.id = documentReference.id
                sendMessage(text)
                startActivity(Intent(this, MainActivity::class.java))
            }

    }

    private fun sendMessage(text: String) {
        Log.i("MyTag", "SendMessage")
        var map: MutableMap<String, Any> = HashMap()
        map["text"] = text
        map["senderId"] = myId.toString()
        map["isRead"] = false
        map["time"] = Calendar.getInstance().timeInMillis

        FirebaseFirestore.getInstance().collection("chats").document(chat?.id!!)
            .collection("messages")
            .add(map)

        Snackbar.make(this.text, "Message Sent", Snackbar.LENGTH_SHORT).show()
    }
    fun initList(){
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        adapter.setUser(list)

    }
}