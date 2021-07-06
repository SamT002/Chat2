package com.example.chatapppro.registration

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.chatapppro.MainActivity
import com.example.chatapppro.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import java.util.*

class ProfileActivity : AppCompatActivity() {
    private val name by lazy {findViewById<EditText>(R.id.profile_name)}
    private val image by lazy {findViewById<ImageView>(R.id.profile_image)}
    private val URL = "https://blog.mozilla.org/wp-content/blogs.dir/278/files/2017/10/FX_BLOG_PROFILE_US_1380x750.jpg"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        Picasso.with(this)
            .load(URL)
            .into(image)
    }

    fun onSave(view: View) {
        val profileName = name.text.toString().trim()
        if (TextUtils.isEmpty(profileName)) {
            name.error = "Fill this field"
            return
        }

        val map: MutableMap<String, Any> = HashMap()
        map["name"] = profileName
        val userID = FirebaseAuth.getInstance().uid!!
        FirebaseFirestore.getInstance().collection("users")
            .document(userID)
            .set(map)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    startActivity(Intent(this@ProfileActivity, MainActivity::class.java))
                }
            }

    }
}