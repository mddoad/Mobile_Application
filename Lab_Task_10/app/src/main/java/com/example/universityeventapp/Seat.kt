package com.example.universityeventapp

data class Seat(
    val number: Int,
    var state: SeatState
)

enum class SeatState { AVAILABLE, BOOKED, SELECTED }