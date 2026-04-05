package com.example.universityeventapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class EventDetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_EVENT = "extra_event"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_detail)

        val event = intent.getSerializableExtra(EXTRA_EVENT) as? Event
            ?: run { finish(); return }

        findViewById<ImageView>(R.id.ivHeader).setImageResource(event.imageRes)
        findViewById<TextView>(R.id.tvTitle).text = event.title
        findViewById<TextView>(R.id.tvMeta).text = "${event.date} • ${event.time} • ${event.venue} • ${event.category}"
        findViewById<TextView>(R.id.tvOrganizer).text = "Organizer: University Event Office"
        findViewById<TextView>(R.id.tvPriceSeats).text =
            (if (event.price == 0.0) "FREE" else "$" + String.format("%.2f", event.price)) +
                    " • Seats: ${event.availableSeats}/${event.totalSeats}"
        findViewById<TextView>(R.id.tvDesc).text = event.description

        val images = listOf(
            android.R.drawable.ic_menu_gallery,
            android.R.drawable.ic_menu_camera,
            android.R.drawable.ic_menu_compass,
            android.R.drawable.ic_menu_agenda,
            android.R.drawable.ic_menu_gallery
        )

        val rv = findViewById<RecyclerView>(R.id.recyclerPhotos)
        rv.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        rv.adapter = PhotoStripAdapter(images)

        findViewById<Button>(R.id.btnRegister).setOnClickListener {
            val i = Intent(this, SeatBookingActivity::class.java)
            i.putExtra(SeatBookingActivity.EXTRA_EVENT, event)
            startActivity(i)
        }
    }
}