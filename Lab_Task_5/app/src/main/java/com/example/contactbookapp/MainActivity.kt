package com.example.contactbookapp

import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var searchView: SearchView
    private lateinit var listView: ListView
    private lateinit var tvEmpty: TextView
    private lateinit var fabAdd: FloatingActionButton

    private val allContacts = mutableListOf<Contact>()
    private val filteredContacts = mutableListOf<Contact>()
    private lateinit var adapter: ContactAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        searchView = findViewById(R.id.searchView)
        listView = findViewById(R.id.listViewContacts)
        tvEmpty = findViewById(R.id.tvEmpty)
        fabAdd = findViewById(R.id.fabAdd)

        seedContacts()

        filteredContacts.addAll(allContacts)
        adapter = ContactAdapter(this, filteredContacts)
        listView.adapter = adapter

        updateEmptyState()

        fabAdd.setOnClickListener { showAddDialog() }

        listView.setOnItemClickListener { _, _, position, _ ->
            val c = filteredContacts[position]
            Toast.makeText(
                this,
                "Name: ${c.name}\nPhone: ${c.phone}\nEmail: ${c.email}",
                Toast.LENGTH_LONG
            ).show()
        }

        listView.setOnItemLongClickListener { _, _, position, _ ->
            val c = filteredContacts[position]
            confirmDelete(c)
            true
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                applyFilter(newText.orEmpty())
                return true
            }
        })
    }

    private fun seedContacts() {
        allContacts.addAll(
            listOf(
                Contact("Alice Johnson", "+1 555 0100", "alice@uni.edu"),
                Contact("Bilal Ahmed", "+1 555 0101", "bilal@uni.edu"),
                Contact("Charlie Khan", "+1 555 0102", "charlie@uni.edu"),
                Contact("Dua Noor", "+1 555 0103", "dua@uni.edu")
            )
        )
    }

    private fun showAddDialog() {
        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 24, 48, 0)
        }

        val etName = EditText(this).apply {
            hint = "Name"
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_WORDS
        }
        val etPhone = EditText(this).apply {
            hint = "Phone"
            inputType = InputType.TYPE_CLASS_PHONE
        }
        val etEmail = EditText(this).apply {
            hint = "Email"
            inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        }

        container.addView(etName)
        container.addView(etPhone)
        container.addView(etEmail)

        AlertDialog.Builder(this)
            .setTitle("Add Contact")
            .setView(container)
            .setPositiveButton("Add") { _, _ ->
                val name = etName.text.toString().trim()
                val phone = etPhone.text.toString().trim()
                val email = etEmail.text.toString().trim()

                if (name.isBlank() || phone.isBlank() || email.isBlank()) {
                    Toast.makeText(this, "Please fill all fields.", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val newContact = Contact(name, phone, email)
                allContacts.add(0, newContact) // add at top

                // re-apply filter so search results stay correct
                applyFilter(searchView.query?.toString().orEmpty())

                Toast.makeText(this, "Contact added.", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun confirmDelete(contact: Contact) {
        AlertDialog.Builder(this)
            .setTitle("Delete Contact")
            .setMessage("Delete ${contact.name}?")
            .setPositiveButton("Delete") { _, _ ->
                allContacts.remove(contact)
                applyFilter(searchView.query?.toString().orEmpty())
                Toast.makeText(this, "Deleted.", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun applyFilter(query: String) {
        val q = query.trim().lowercase()

        filteredContacts.clear()
        if (q.isEmpty()) {
            filteredContacts.addAll(allContacts)
        } else {
            filteredContacts.addAll(allContacts.filter { it.name.lowercase().contains(q) })
        }

        adapter.notifyDataSetChanged()
        updateEmptyState()
    }

    private fun updateEmptyState() {
        if (filteredContacts.isEmpty()) {
            tvEmpty.visibility = View.VISIBLE
            listView.visibility = View.GONE
        } else {
            tvEmpty.visibility = View.GONE
            listView.visibility = View.VISIBLE
        }
    }
}