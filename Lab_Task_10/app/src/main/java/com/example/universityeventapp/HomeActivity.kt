package com.example.universityeventapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        findViewById<TextView>(R.id.tvUpcomingCount).text = SampleData.events.size.toString()

        // Featured event = first event
        val featured = SampleData.events.first()
        findViewById<TextView>(R.id.tvFeaturedTitle).text = featured.title
        findViewById<TextView>(R.id.tvFeaturedDate).text = "${featured.date} • ${featured.venue}"

        findViewById<Button>(R.id.btnBrowse).setOnClickListener {
            startActivity(Intent(this, EventsListActivity::class.java))
        }
        findViewById<Button>(R.id.btnRegisterNow).setOnClickListener {
            val i = Intent(this, EventDetailActivity::class.java)
            i.putExtra(EventDetailActivity.EXTRA_EVENT, featured)
            startActivity(i)
        }

        // Other quick actions: show simple toasts via EventsList for now
        findViewById<Button>(R.id.btnBookings).setOnClickListener {
            startActivity(Intent(this, EventsListActivity::class.java))
        }
        findViewById<Button>(R.id.btnNotifications).setOnClickListener {
            startActivity(Intent(this, EventsListActivity::class.java))
        }
        findViewById<Button>(R.id.btnProfile).setOnClickListener {
            startActivity(Intent(this, EventsListActivity::class.java))
        }
    }
}