package com.example.chatapppro.registration

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.chatapppro.R
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import com.squareup.picasso.Picasso
import java.util.concurrent.TimeUnit

class NumberActivity : AppCompatActivity() {

    private val number by lazy { findViewById<TextView>(R.id.phone) }
    private val code by lazy { findViewById<TextView>(R.id.code) }
    private lateinit var auth: FirebaseAuth
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private lateinit var id: String
    private val IMAGE = "https://neilpatel.com/wp-content/uploads/2017/04/chat.jpg"
    private val image by lazy {findViewById<ImageView>(R.id.numberImage)}


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_number)

        Picasso.with(this@NumberActivity)
            .load(IMAGE)
            .into(image)

        callbacks = object : OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                Log.i("MyTag", "onVerificationCompleted")
                signIn(phoneAuthCredential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Log.i("MyTag", "OnCodeSent")
            }

            override fun onCodeSent(s: String, forceResendingToken: ForceResendingToken) {
                super.onCodeSent(s, forceResendingToken)
                id = s
                code.visibility = View.VISIBLE
            }
        }
    }


    fun NumberClicked(view: View) {

        Snackbar.make(view, "Button Clicked", Snackbar.LENGTH_LONG).show()
        closeKeyboard()

        if (TextUtils.isEmpty(code.text.toString())) {
            val phone = "+996" + number.text.toString().trim { it <= ' ' }
            auth = FirebaseAuth.getInstance()
            val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phone)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this@NumberActivity)
                .setCallbacks(callbacks)
                .build()
            PhoneAuthProvider.verifyPhoneNumber(options)
    } else
    {
        singInCreditinal(code.text.toString())
    }


}

    private fun signIn(phoneAuthCredential: PhoneAuthCredential) {
        FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.i("MyTag", "onComplete")
                startActivity(Intent(this@NumberActivity, ProfileActivity::class.java))
                finish()
            } else {
                Snackbar.make(number, "Ошибка Авторизации", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    fun singInCreditinal(code: String?) {
        val phoneAuthCredential = PhoneAuthProvider.getCredential(id, code!!)
        auth.signInWithCredential(phoneAuthCredential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    startActivity(Intent(this@NumberActivity, ProfileActivity::class.java))
                    finish()
                }
            }
    }

    fun closeKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}