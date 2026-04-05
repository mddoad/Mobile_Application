package com.example.universityeventapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class EventAdapter(
    private var items: List<Event>,
    private val onClick: (Event) -> Unit
) : RecyclerView.Adapter<EventAdapter.VH>() {

    fun submitList(newItems: List<Event>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(items[position])
    override fun getItemCount(): Int = items.size

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val iv: ImageView = itemView.findViewById(R.id.ivBanner)
        private val title: TextView = itemView.findViewById(R.id.tvTitle)
        private val meta: TextView = itemView.findViewById(R.id.tvMeta)
        private val seats: TextView = itemView.findViewById(R.id.tvSeats)
        private val price: TextView = itemView.findViewById(R.id.tvPrice)

        fun bind(e: Event) {
            iv.setImageResource(e.imageRes)
            title.text = e.title
            meta.text = "${e.date} • ${e.venue} • ${e.category}"
            seats.text = "Available seats: ${e.availableSeats}/${e.totalSeats}"
            price.text = if (e.price == 0.0) "FREE" else "$" + String.format("%.2f", e.price)

            itemView.setOnClickListener { onClick(e) }
        }
    }
}