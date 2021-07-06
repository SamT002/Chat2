package com.example.chatapppro

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapppro.R
import com.example.chatapppro.chat.ChatActivity
import com.example.chatapppro.model.Chat
import com.example.chatapppro.model.Message
import com.example.chatapppro.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore

class MainAdapter : RecyclerView.Adapter<MainAdapter.MyViewHolder>() {

    private var myList = listOf<Chat>()
    private lateinit var context: Context

    fun setUser(myList: List<Chat>) {
        this.myList = myList
        notifyDataSetChanged()
    }


    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.name_contact)
        val new: TextView = itemView.findViewById(R.id.noti)
        val id = FirebaseAuth.getInstance().uid
        var chatName = ""
        var nameT = ""


        fun bind(chat: Chat) {

            val a = chat.UserIds as MutableList
            a.remove(id)
            chatName = a[0]

            FirebaseFirestore.getInstance().collection("users")
                    .get()
                    .addOnSuccessListener { snap ->
                        for (snapshot in snap) {
                            if (snapshot.id == chatName) {
                                name.text = snapshot["name"].toString()
                                nameT = snapshot["name"].toString()
                            }
                        }

                    }
        }

        fun isRead(chat: Chat) {
            val a = chat.UserIds as MutableList
            a.remove(id)
            chatName = a[0]
            var newMessage = 0
            FirebaseFirestore.getInstance().collection("chats")
                    .document(chat.id.toString())
                    .collection("messages")
                    .whereEqualTo("senderId", chatName)
                    .get()
                    .addOnSuccessListener { snap ->
                        for (snapshot in snap) {
                            val user: Message = snapshot.toObject(Message::class.java)
                            if (!user.isRead) {
                                newMessage += 1
                            }

                        }
                        if(newMessage > 0){
                            new.text = newMessage.toString()
                            new.visibility = View.VISIBLE
                        }

                    }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.product_item, parent, false)
        val vh = MyViewHolder(v)
        context = parent.context
        return vh
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(myList[position])
        holder.isRead(myList[position])

        holder.itemView.setOnClickListener {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("chat", myList[position])
            intent.putExtra("name", holder.nameT)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return myList.size
    }
}