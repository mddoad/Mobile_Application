package com.example.newsreaderapp

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.NestedScrollView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var scrollView: NestedScrollView

    private var bookmarked = false
    private lateinit var bookmarkMenuItem: MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        scrollView = findViewById(R.id.scrollView)

        // Quick nav buttons
        findViewById<Button>(R.id.btnIntro).setOnClickListener {
            scrollToView(R.id.headingIntro)
        }
        findViewById<Button>(R.id.btnKeyPoints).setOnClickListener {
            scrollToView(R.id.headingKeyPoints)
        }
        findViewById<Button>(R.id.btnAnalysis).setOnClickListener {
            scrollToView(R.id.headingAnalysis)
        }
        findViewById<Button>(R.id.btnConclusion).setOnClickListener {
            scrollToView(R.id.headingConclusion)
        }

        // Back to top
        findViewById<FloatingActionButton>(R.id.fabTop).setOnClickListener {
            scrollView.smoothScrollTo(0, 0)
        }

        // Handle toolbar menu clicks
        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_bookmark -> {
                    toggleBookmark(item)
                    true
                }
                R.id.action_share -> {
                    shareArticle()
                    true
                }
                else -> false
            }
        }

        // Get bookmark menu item reference after menu is inflated
        toolbar.post {
            bookmarkMenuItem = toolbar.menu.findItem(R.id.action_bookmark)
            updateBookmarkIcon()
        }
    }

    private fun scrollToView(viewId: Int) {
        val target = findViewById<android.view.View>(viewId)
        // scrollY relative to scrollView content
        val y = target.top
        scrollView.smoothScrollTo(0, y)
    }

    private fun toggleBookmark(item: MenuItem) {
        bookmarked = !bookmarked
        updateBookmarkIcon()

        Toast.makeText(
            this,
            if (bookmarked) getString(R.string.bookmark_added) else getString(R.string.bookmark_removed),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun updateBookmarkIcon() {
        // Using built-in star icons (outline/filled)
        if (::bookmarkMenuItem.isInitialized) {
            bookmarkMenuItem.setIcon(
                if (bookmarked) android.R.drawable.btn_star_big_on
                else android.R.drawable.btn_star_big_off
            )
        }
    }

    private fun shareArticle() {
        val title = findViewById<android.widget.TextView>(R.id.tvTitle).text.toString()

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "News Article")
            putExtra(Intent.EXTRA_TEXT, title)
        }
        startActivity(Intent.createChooser(intent, "Share via"))
    }
}