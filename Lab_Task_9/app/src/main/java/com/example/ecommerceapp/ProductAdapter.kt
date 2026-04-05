package com.example.ecommerceapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class ProductAdapter(
    private val onCartToggle: (Product) -> Unit
) : ListAdapter<Product, RecyclerView.ViewHolder>(Diff) {

    companion object {
        const val MODE_LIST = 0
        const val MODE_GRID = 1

        private object Diff : DiffUtil.ItemCallback<Product>() {
            override fun areItemsTheSame(oldItem: Product, newItem: Product) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Product, newItem: Product) = oldItem == newItem
        }
    }

    var viewMode: Int = MODE_LIST
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemViewType(position: Int): Int = viewMode

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == MODE_LIST) {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_product_list, parent, false)
            ListVH(v)
        } else {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_product_grid, parent, false)
            GridVH(v)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val product = getItem(position)
        if (holder is ListVH) holder.bind(product)
        if (holder is GridVH) holder.bind(product)
    }

    inner class ListVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val iv: ImageView = itemView.findViewById(R.id.ivProduct)
        private val tvName: TextView = itemView.findViewById(R.id.tvName)
        private val tvCat: TextView = itemView.findViewById(R.id.tvCategory)
        private val rating: RatingBar = itemView.findViewById(R.id.ratingBar)
        private val tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
        private val btn: Button = itemView.findViewById(R.id.btnCart)

        fun bind(p: Product) {
            iv.setImageResource(p.imageRes)
            tvName.text = p.name
            tvCat.text = p.category
            rating.rating = p.rating
            tvPrice.text = "$" + String.format("%.2f", p.price)

            btn.text = if (p.inCart) "Remove" else "Add"
            btn.setOnClickListener { onCartToggle(p) }
        }
    }

    inner class GridVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val iv: ImageView = itemView.findViewById(R.id.ivProduct)
        private val tvName: TextView = itemView.findViewById(R.id.tvName)
        private val tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
        private val btn: ImageButton = itemView.findViewById(R.id.btnCartIcon)

        fun bind(p: Product) {
            iv.setImageResource(p.imageRes)
            tvName.text = p.name
            tvPrice.text = "$" + String.format("%.2f", p.price)

            btn.setImageResource(
                if (p.inCart) android.R.drawable.ic_menu_delete
                else android.R.drawable.ic_menu_add
            )
            btn.setOnClickListener { onCartToggle(p) }
        }
    }
}