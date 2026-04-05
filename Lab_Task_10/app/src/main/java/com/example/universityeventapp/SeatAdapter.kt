package com.example.universityeventapp

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class SeatAdapter(
    private val context: Context,
    private val seats: List<Seat>
) : BaseAdapter() {

    override fun getCount(): Int = seats.size
    override fun getItem(position: Int): Any = seats[position]
    override fun getItemId(position: Int): Long = seats[position].number.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_seat, parent, false)
        val tv = view.findViewById<TextView>(R.id.tvSeat)
        val seat = seats[position]

        tv.text = seat.number.toString()

        when (seat.state) {
            SeatState.AVAILABLE -> tv.setBackgroundColor(Color.parseColor("#4CAF50")) // green
            SeatState.BOOKED -> tv.setBackgroundColor(Color.parseColor("#F44336")) // red
            SeatState.SELECTED -> tv.setBackgroundColor(Color.parseColor("#2196F3")) // blue
        }
        return view
    }
}