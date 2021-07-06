package com.example.chatapppro.model

data class Message(var text:String = "", var time:Long = 0, var isRead:Boolean = false, var senderId:String = "")
