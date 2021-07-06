package com.example.chatapppro.model

import java.io.Serializable

data class Chat(var id:String? = null, var UserIds:List<String> = listOf()) : Serializable
