package com.example.pseudorex.app

import com.google.gson.annotations.SerializedName

//Get Version model

data class Version (
    val n : n
)

data class n (
    @SerializedName("champion") val championVersion : String,
    @SerializedName("item") val itemVersion: String
)

//Data champions

data class Champions (
    val key  : String,
    val name : String
)

//Items data

data class Items (
    val key : String,
    val name : String,
    val plaintext : String,
    val gold : String
)

