package com.example.universityeventapp

import java.io.Serializable

data class Event(
    val id: Long,
    val title: String,
    val date: String,
    val time: String,
    val venue: String,
    val category: String,
    val description: String,
    val price: Double,
    val totalSeats: Int,
    val availableSeats: Int,
    val imageRes: Int
) : Serializable