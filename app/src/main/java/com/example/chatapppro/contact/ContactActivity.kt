package com.example.chatapppro.contact

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapppro.R
import com.example.chatapppro.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class ContactActivity : AppCompatActivity() {

    private val recyclerView by lazy {findViewById<RecyclerView>(R.id.contact_recycler)}
    private val adapter by lazy {ContactAdapter()}
    private var list: MutableList<User> = ArrayList<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact)

        init()
        getContacts()

    }

    private fun getContacts() {
        val id = FirebaseAuth.getInstance().uid
        FirebaseFirestore.getInstance().collection("users").get()
            .addOnSuccessListener { snap ->
                for (snapshot in snap) {
                    val user: User? = snapshot.toObject(User::class.java)
                    if (user != null) {
                        user.id = snapshot.id
                    }
                    if (!user?.id.equals(id)) {
                        list.add(user!!)
                    }
                }
                adapter.notifyDataSetChanged()
            }
    }

    private fun init(){
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        adapter.setUser(list)

    }
}