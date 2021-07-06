package com.example.chatapppro.contact

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapppro.R
import com.example.chatapppro.chat.ChatActivity
import com.example.chatapppro.model.User

class ContactAdapter : RecyclerView.Adapter<ContactAdapter.MyViewHolder>() {

    private var myList = listOf<User>()
    private lateinit var context: Context

    fun setUser(myList: List<User>){
        this.myList = myList
        notifyDataSetChanged()
    }


    inner class MyViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val name: TextView = itemView.findViewById(R.id.name_contact)

        fun bind(user: User) {
            name.text = user.name
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

        holder.itemView.setOnClickListener {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("user", myList[position])
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return myList.size
    }
}