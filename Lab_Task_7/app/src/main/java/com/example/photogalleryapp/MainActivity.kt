package com.example.photogalleryapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.GridView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import android.widget.ToggleButton
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var gridView: GridView
    private lateinit var adapter: PhotoAdapter

    private lateinit var selectionBar: LinearLayout
    private lateinit var tvSelectedCount: TextView
    private lateinit var btnDelete: Button
    private lateinit var btnShare: Button
    private lateinit var fabAdd: FloatingActionButton

    private lateinit var tabAll: ToggleButton
    private lateinit var tabNature: ToggleButton
    private lateinit var tabCity: ToggleButton
    private lateinit var tabAnimals: ToggleButton
    private lateinit var tabFood: ToggleButton
    private lateinit var tabTravel: ToggleButton

    private val allPhotos = mutableListOf<Photo>()
    private var currentCategory: String = "All"

    // Adapter data list (filtered)
    private val shownPhotos = mutableListOf<Photo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gridView = findViewById(R.id.gridView)
        selectionBar = findViewById(R.id.selectionBar)
        tvSelectedCount = findViewById(R.id.tvSelectedCount)
        btnDelete = findViewById(R.id.btnDeleteSelected)
        btnShare = findViewById(R.id.btnShareSelected)
        fabAdd = findViewById(R.id.fabAdd)

        tabAll = findViewById(R.id.tabAll)
        tabNature = findViewById(R.id.tabNature)
        tabCity = findViewById(R.id.tabCity)
        tabAnimals = findViewById(R.id.tabAnimals)
        tabFood = findViewById(R.id.tabFood)
        tabTravel = findViewById(R.id.tabTravel)

        seedPhotos()

        shownPhotos.addAll(allPhotos)
        adapter = PhotoAdapter(this, shownPhotos)
        gridView.adapter = adapter

        setupTabs()

        gridView.setOnItemClickListener { _, _, position, _ ->
            val photo = shownPhotos[position]
            if (adapter.selectionMode) {
                toggleSelection(photo)
            } else {
                openFullscreen(photo.resourceId)
            }
        }

        gridView.setOnItemLongClickListener { _, _, position, _ ->
            val photo = shownPhotos[position]
            if (!adapter.selectionMode) {
                enterSelectionMode()
            }
            toggleSelection(photo)
            true
        }

        btnDelete.setOnClickListener { confirmDeleteSelected() }
        btnShare.setOnClickListener { shareSelected() }

        fabAdd.setOnClickListener { addRandomPhoto() }

        updateSelectionUI()
    }

    private fun seedPhotos() {
        // Requires p1..p12 drawables in res/drawable
        val resIds = listOf(
            R.drawable.p1, R.drawable.p2, R.drawable.p3, R.drawable.p4,
            R.drawable.p5, R.drawable.p6, R.drawable.p7, R.drawable.p8,
            R.drawable.p9, R.drawable.p10, R.drawable.p11, R.drawable.p12
        )

        val categories = listOf("Nature", "City", "Animals", "Food", "Travel")
        var idCounter = 1L

        // 12 photos distributed across categories
        for (i in resIds.indices) {
            val cat = categories[i % categories.size]
            allPhotos.add(
                Photo(
                    id = idCounter++,
                    resourceId = resIds[i],
                    title = "Photo ${i + 1} • $cat",
                    category = cat
                )
            )
        }
    }

    private fun setupTabs() {
        fun setSingleChecked(active: ToggleButton) {
            val all = listOf(tabAll, tabNature, tabCity, tabAnimals, tabFood, tabTravel)
            all.forEach { it.isChecked = (it == active) }
        }

        tabAll.setOnClickListener {
            setSingleChecked(tabAll)
            setCategory("All")
        }
        tabNature.setOnClickListener {
            setSingleChecked(tabNature)
            setCategory("Nature")
        }
        tabCity.setOnClickListener {
            setSingleChecked(tabCity)
            setCategory("City")
        }
        tabAnimals.setOnClickListener {
            setSingleChecked(tabAnimals)
            setCategory("Animals")
        }
        tabFood.setOnClickListener {
            setSingleChecked(tabFood)
            setCategory("Food")
        }
        tabTravel.setOnClickListener {
            setSingleChecked(tabTravel)
            setCategory("Travel")
        }
    }

    private fun setCategory(category: String) {
        currentCategory = category
        exitSelectionMode(clearSelection = true)

        val filtered = if (category == "All") {
            allPhotos
        } else {
            allPhotos.filter { it.category == category }
        }

        shownPhotos.clear()
        shownPhotos.addAll(filtered)
        adapter.notifyDataSetChanged()

        Toast.makeText(this, "Category: $category", Toast.LENGTH_SHORT).show()
    }

    private fun openFullscreen(resourceId: Int) {
        val intent = Intent(this, FullscreenActivity::class.java)
        intent.putExtra(FullscreenActivity.EXTRA_RES_ID, resourceId)
        startActivity(intent)
    }

    private fun enterSelectionMode() {
        adapter.selectionMode = true
        selectionBar.visibility = View.VISIBLE
        updateSelectionUI()
    }

    private fun exitSelectionMode(clearSelection: Boolean) {
        if (clearSelection) {
            allPhotos.forEach { it.isSelected = false }
            shownPhotos.forEach { it.isSelected = false }
        }
        adapter.selectionMode = false
        selectionBar.visibility = View.GONE
        updateSelectionUI()
    }

    private fun toggleSelection(photo: Photo) {
        photo.isSelected = !photo.isSelected
        adapter.notifyDataSetChanged()
        updateSelectionUI()

        // If none selected, exit selection mode
        if (selectedCount() == 0) {
            exitSelectionMode(clearSelection = false)
        }
    }

    private fun selectedCount(): Int = allPhotos.count { it.isSelected }

    private fun updateSelectionUI() {
        val count = selectedCount()
        tvSelectedCount.text = "$count selected"
    }

    private fun confirmDeleteSelected() {
        val count = selectedCount()
        if (count == 0) {
            Toast.makeText(this, "No photos selected.", Toast.LENGTH_SHORT).show()
            return
        }

        AlertDialog.Builder(this)
            .setTitle("Delete")
            .setMessage("Delete $count selected photos?")
            .setPositiveButton("Delete") { _, _ ->
                deleteSelected()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteSelected() {
        val before = allPhotos.size
        allPhotos.removeAll { it.isSelected }
        val deleted = before - allPhotos.size

        // Refresh current category list
        setCategory(currentCategory)

        Toast.makeText(this, "$deleted photos deleted", Toast.LENGTH_SHORT).show()
    }

    private fun shareSelected() {
        val selected = allPhotos.filter { it.isSelected }
        if (selected.isEmpty()) {
            Toast.makeText(this, "No photos selected.", Toast.LENGTH_SHORT).show()
            return
        }

        val text = selected.joinToString(separator = "\n") { it.title }

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Selected Photos")
            putExtra(Intent.EXTRA_TEXT, text)
        }
        startActivity(Intent.createChooser(intent, "Share via"))
    }

    private fun addRandomPhoto() {
        // Simulate adding a new photo using one random drawable from existing list
        val resIds = listOf(
            R.drawable.p1, R.drawable.p2, R.drawable.p3, R.drawable.p4,
            R.drawable.p5, R.drawable.p6, R.drawable.p7, R.drawable.p8,
            R.drawable.p9, R.drawable.p10, R.drawable.p11, R.drawable.p12
        )
        val categories = listOf("Nature", "City", "Animals", "Food", "Travel")

        val resId = resIds.random()
        val cat = categories.random()
        val newId = (allPhotos.maxOfOrNull { it.id } ?: 0L) + 1L

        allPhotos.add(
            0,
            Photo(
                id = newId,
                resourceId = resId,
                title = "New Photo • $cat",
                category = cat
            )
        )

        // Refresh current view
        setCategory(currentCategory)
        Toast.makeText(this, "Photo added.", Toast.LENGTH_SHORT).show()
    }

    override fun onBackPressed() {
        if (adapter.selectionMode) {
            exitSelectionMode(clearSelection = true)
        } else {
            super.onBackPressed()
        }
    }
}