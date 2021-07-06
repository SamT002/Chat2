package com.example.chatapppro.message

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapppro.R
import com.example.chatapppro.model.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class MessageAdapter : RecyclerView.Adapter<MessageAdapter.MyViewHolder>() {

    private var myList = listOf<Message>()
    private lateinit var context: Context

    fun setUser(myList: List<Message>){
        this.myList = myList
        notifyDataSetChanged()
    }


    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val text: TextView = itemView.findViewById(R.id.message_text)
        val name2: TextView = itemView.findViewById(R.id.message_name_2)
        val text2: TextView = itemView.findViewById(R.id.message_text_2)
        val time: TextView = itemView.findViewById(R.id.message_time)
        val time2: TextView = itemView.findViewById(R.id.message_time_2)
        val cardView:CardView = itemView.findViewById(R.id.guest_card)
        val cardViewMe:CardView = itemView.findViewById(R.id.sender_card)

        @SuppressLint("SimpleDateFormat")
        fun bind(user: Message) {
            val myId = FirebaseAuth.getInstance().uid
            val messageText = user.text
            val simple: DateFormat = SimpleDateFormat("h:mm a")
            val result = Date(user.time)
            val time = simple.format(result)

            if (user.senderId == myId){
                cardViewMe.visibility = View.VISIBLE
                text.text = messageText
                this.time.text = time
            }else{
                cardView.visibility = View.VISIBLE
                text2.text = user.text
                time2.text = time
            }


        }

        fun questName(message: Message){
            FirebaseFirestore.getInstance().collection("users")
                    .get()
                    .addOnSuccessListener { snap ->
                        for (snapshot in snap) {
                            if (snapshot.id == message.senderId){
                                name2.text = snapshot["name"].toString()
                            }
                        }

                    }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.message, parent, false)
        val vh = MyViewHolder(v)
        context = parent.context
        return vh
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(myList[position])
        holder.questName(myList[position])

    }

    override fun getItemCount(): Int {
        return myList.size
    }
}