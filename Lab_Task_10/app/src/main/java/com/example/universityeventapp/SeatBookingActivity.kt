package com.example.universityeventapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.GridView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import kotlin.math.roundToInt
import kotlin.random.Random

class SeatBookingActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_EVENT = "extra_event"
    }

    private lateinit var grid: GridView
    private lateinit var tvSummary: TextView

    private val seats = MutableList(48) { i -> Seat(i + 1, SeatState.AVAILABLE) }
    private lateinit var adapter: SeatAdapter

    private lateinit var event: Event

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seat_booking)

        event = intent.getSerializableExtra(EXTRA_EVENT) as? Event
            ?: run { finish(); return }

        val toolbar = findViewById<Toolbar>(R.id.toolbarSeats)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        grid = findViewById(R.id.gridSeats)
        tvSummary = findViewById(R.id.tvSummary)

        // Randomly mark ~30% seats as booked
        val bookedCount = (seats.size * 0.30).roundToInt()
        val bookedSet = mutableSetOf<Int>()
        while (bookedSet.size < bookedCount) {
            bookedSet.add(Random.nextInt(1, seats.size + 1))
        }
        seats.forEach { if (bookedSet.contains(it.number)) it.state = SeatState.BOOKED }

        adapter = SeatAdapter(this, seats)
        grid.adapter = adapter

        grid.setOnItemClickListener { _, _, position, _ ->
            val seat = seats[position]
            when (seat.state) {
                SeatState.BOOKED -> Toast.makeText(this, "Seat ${seat.number} is already booked.", Toast.LENGTH_SHORT).show()
                SeatState.AVAILABLE -> {
                    seat.state = SeatState.SELECTED
                    adapter.notifyDataSetChanged()
                    updateSummary()
                }
                SeatState.SELECTED -> {
                    seat.state = SeatState.AVAILABLE
                    adapter.notifyDataSetChanged()
                    updateSummary()
                }
            }
        }

        findViewById<Button>(R.id.btnConfirm).setOnClickListener { confirmBooking() }

        updateSummary()
    }

    private fun selectedCount(): Int = seats.count { it.state == SeatState.SELECTED }

    private fun updateSummary() {
        val count = selectedCount()
        val total = count * event.price
        tvSummary.text = "$count seats selected • Total $" + String.format("%.2f", total)
    }

    private fun confirmBooking() {
        val count = selectedCount()
        if (count == 0) {
            Toast.makeText(this, "Select at least 1 seat.", Toast.LENGTH_SHORT).show()
            return
        }

        AlertDialog.Builder(this)
            .setTitle("Confirm Booking")
            .setMessage("Confirm $count seat(s) for ${event.title}?")
            .setPositiveButton("Confirm") { _, _ ->
                Toast.makeText(this, "Booking confirmed (simulated).", Toast.LENGTH_LONG).show()
                finish()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onSupportNavigateUp(): Boolean {
        handleLeave()
        return true
    }

    @SuppressLint("GestureBackNavigation")
    override fun onBackPressed() {
        super.onBackPressed()
        handleLeave()
    }

    private fun handleLeave() {
        if (selectedCount() > 0) {
            AlertDialog.Builder(this)
                .setTitle("Leave Booking?")
                .setMessage("You have selected seats. Do you want to leave without confirming?")
                .setPositiveButton("Leave") { _, _ -> finish() }
                .setNegativeButton("Stay", null)
                .show()
        } else {
            finish()
        }
    }
}