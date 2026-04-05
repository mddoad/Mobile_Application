package com.example.universityeventapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip

class EventsListActivity : AppCompatActivity() {

    private lateinit var adapter: EventAdapter
    private var query: String = ""
    private var category: String = "All"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_events_list)

        val toolbar = findViewById<Toolbar>(R.id.toolbarEvents)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val recycler = findViewById<RecyclerView>(R.id.recyclerEvents)
        recycler.layoutManager = LinearLayoutManager(this)

        adapter = EventAdapter(SampleData.events) { e ->
            val i = Intent(this, EventDetailActivity::class.java)
            i.putExtra(EventDetailActivity.EXTRA_EVENT, e)
            startActivity(i)
        }
        recycler.adapter = adapter

        val search = findViewById<SearchView>(R.id.searchEvents)
        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(q: String?) = false
            override fun onQueryTextChange(newText: String?): Boolean {
                query = newText.orEmpty()
                applyFilter()
                return true
            }
        })

        fun setCat(c: String) { category = c; applyFilter() }

        findViewById<Chip>(R.id.chipAll).setOnClickListener { setCat("All") }
        findViewById<Chip>(R.id.chipTech).setOnClickListener { setCat("Tech") }
        findViewById<Chip>(R.id.chipSports).setOnClickListener { setCat("Sports") }
        findViewById<Chip>(R.id.chipCultural).setOnClickListener { setCat("Cultural") }
        findViewById<Chip>(R.id.chipAcademic).setOnClickListener { setCat("Academic") }
        findViewById<Chip>(R.id.chipSocial).setOnClickListener { setCat("Social") }

        applyFilter()
    }

    private fun applyFilter() {
        val q = query.trim().lowercase()
        val list = SampleData.events.filter { e ->
            val matchCat = (category == "All" || e.category == category)
            val matchQuery = (q.isEmpty() || e.title.lowercase().contains(q) || e.venue.lowercase().contains(q))
            matchCat && matchQuery
        }
        adapter.submitList(list)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}