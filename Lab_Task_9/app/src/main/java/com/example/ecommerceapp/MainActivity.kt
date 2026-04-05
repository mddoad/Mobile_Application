package com.example.ecommerceapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var recycler: RecyclerView
    private lateinit var skeleton: LinearLayout
    private lateinit var emptyState: View

    private lateinit var adapter: ProductAdapter

    private var viewMode = ProductAdapter.MODE_LIST
    private var selectedCategory = "All"
    private var searchQuery = ""

    private val allProducts = mutableListOf<Product>()
    private val currentList = mutableListOf<Product>() // shown list backing drag/reorder

    // For undo delete
    private var lastDeleted: Product? = null
    private var lastDeletedPos: Int = -1

    private var cartMenuItem: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar = findViewById(R.id.toolbar)
        recycler = findViewById(R.id.recycler)
        skeleton = findViewById(R.id.skeletonContainer)
        emptyState = findViewById(R.id.emptyState)

        setSupportActionBar(toolbar)

        adapter = ProductAdapter { p -> toggleCart(p.id) }

        recycler.adapter = adapter
        applyLayoutManager()

        setupChips()
        attachTouchHelpers()

        showLoading(true)
        Handler(Looper.getMainLooper()).postDelayed({
            seedProducts()
            applyFiltersAndSubmit()
            showLoading(false)
        }, 900)
    }

    private fun showLoading(loading: Boolean) {
        skeleton.visibility = if (loading) View.VISIBLE else View.GONE
        recycler.visibility = if (loading) View.GONE else View.VISIBLE
        emptyState.visibility = View.GONE
    }

    private fun seedProducts() {
        // Using built-in icons as images (no drawable files required)
        val img1 = android.R.drawable.ic_menu_camera
        val img2 = android.R.drawable.ic_menu_compass
        val img3 = android.R.drawable.ic_menu_gallery
        val img4 = android.R.drawable.ic_menu_agenda
        val img5 = android.R.drawable.ic_menu_slideshow

        allProducts.clear()
        allProducts.addAll(
            listOf(
                Product(1, "Bluetooth Headphones", 49.99, 4.5f, "Electronics", img1),
                Product(2, "Smart Watch", 79.99, 4.0f, "Electronics", img2),
                Product(3, "Cotton T-Shirt", 14.50, 4.2f, "Clothing", img3),
                Product(4, "Hoodie", 29.99, 4.6f, "Clothing", img4),
                Product(5, "Data Structures Book", 19.99, 4.8f, "Books", img5),
                Product(6, "Kotlin for Android", 24.99, 4.4f, "Books", img3),
                Product(7, "Chocolate Cookies", 5.99, 4.1f, "Food", img2),
                Product(8, "Organic Honey", 9.49, 4.7f, "Food", img1),
                Product(9, "Toy Car", 7.99, 4.0f, "Toys", img4),
                Product(10, "Building Blocks", 12.99, 4.3f, "Toys", img5)
            )
        )
    }

    private fun setupChips() {
        fun chip(id: Int) = findViewById<Chip>(id)

        chip(R.id.chipAll).setOnClickListener { selectedCategory = "All"; applyFiltersAndSubmit() }
        chip(R.id.chipElectronics).setOnClickListener { selectedCategory = "Electronics"; applyFiltersAndSubmit() }
        chip(R.id.chipClothing).setOnClickListener { selectedCategory = "Clothing"; applyFiltersAndSubmit() }
        chip(R.id.chipBooks).setOnClickListener { selectedCategory = "Books"; applyFiltersAndSubmit() }
        chip(R.id.chipFood).setOnClickListener { selectedCategory = "Food"; applyFiltersAndSubmit() }
        chip(R.id.chipToys).setOnClickListener { selectedCategory = "Toys"; applyFiltersAndSubmit() }
    }

    private fun applyLayoutManager() {
        recycler.layoutManager = if (viewMode == ProductAdapter.MODE_LIST) {
            LinearLayoutManager(this)
        } else {
            GridLayoutManager(this, 2)
        }
        adapter.viewMode = viewMode
    }

    private fun attachTouchHelpers() {
        val callback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                rv: RecyclerView,
                vh: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val from = vh.bindingAdapterPosition
                val to = target.bindingAdapterPosition
                if (from == RecyclerView.NO_POSITION || to == RecyclerView.NO_POSITION) return false

                // Swap in currentList (shown list)
                val tmp = currentList[from]
                currentList[from] = currentList[to]
                currentList[to] = tmp

                adapter.submitList(currentList.toList())
                return true
            }

            override fun onSwiped(vh: RecyclerView.ViewHolder, direction: Int) {
                val pos = vh.bindingAdapterPosition
                if (pos == RecyclerView.NO_POSITION) return

                lastDeleted = currentList[pos]
                lastDeletedPos = pos

                val removed = currentList.removeAt(pos)
                removeFromAllProducts(removed.id)

                adapter.submitList(currentList.toList())
                updateEmptyState()

                Snackbar.make(recycler, "Deleted: ${removed.name}", Snackbar.LENGTH_LONG)
                    .setAction("UNDO") {
                        undoDelete()
                    }.show()
            }
        }

        ItemTouchHelper(callback).attachToRecyclerView(recycler)
    }

    private fun undoDelete() {
        val item = lastDeleted ?: return
        val pos = lastDeletedPos.coerceIn(0, currentList.size)

        // Restore into master list first
        allProducts.add(pos, item)
        applyFiltersAndSubmit()

        lastDeleted = null
        lastDeletedPos = -1
    }

    private fun removeFromAllProducts(id: Long) {
        val idx = allProducts.indexOfFirst { it.id == id }
        if (idx >= 0) allProducts.removeAt(idx)
    }

    private fun toggleCart(id: Long) {
        val idx = allProducts.indexOfFirst { it.id == id }
        if (idx < 0) return

        val p = allProducts[idx]
        allProducts[idx] = p.copy(inCart = !p.inCart)

        applyFiltersAndSubmit()
        updateCartBadge()
    }

    private fun applyFiltersAndSubmit() {
        val q = searchQuery.trim().lowercase(Locale.getDefault())

        val filtered = allProducts.filter { p ->
            val matchCategory = (selectedCategory == "All" || p.category == selectedCategory)
            val matchSearch = (q.isEmpty() || p.name.lowercase(Locale.getDefault()).contains(q))
            matchCategory && matchSearch
        }

        currentList.clear()
        currentList.addAll(filtered)

        adapter.submitList(currentList.toList())
        updateEmptyState()
        updateCartBadge()
    }

    private fun updateEmptyState() {
        val isEmpty = currentList.isEmpty()
        emptyState.visibility = if (isEmpty) View.VISIBLE else View.GONE
        recycler.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        cartMenuItem = menu.findItem(R.id.action_cart)
        updateCartBadge()

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        searchView.queryHint = "Search products..."

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                searchQuery = newText.orEmpty()
                applyFiltersAndSubmit()
                return true
            }
        })

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_toggle -> {
                viewMode = if (viewMode == ProductAdapter.MODE_LIST) ProductAdapter.MODE_GRID else ProductAdapter.MODE_LIST
                applyLayoutManager()
                true
            }
            R.id.action_cart -> {
                openCart()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun openCart() {
        val cart = allProducts.filter { it.inCart }
        if (cart.isEmpty()) {
            Toast.makeText(this, "Cart is empty.", Toast.LENGTH_SHORT).show()
            return
        }
        CartStore.items = cart // simple shared store for lab
        startActivity(Intent(this, CartActivity::class.java))
    }

    private fun updateCartBadge() {
        val count = allProducts.count { it.inCart }
        cartMenuItem?.title = "Cart ($count)"
        // (Simple badge approach: updates title. If you need a real badge dot, tell me.)
    }
}