package com.example.universityeventapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class PhotoStripAdapter(private val images: List<Int>) : RecyclerView.Adapter<PhotoStripAdapter.VH>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_photo_strip, parent, false)
        return VH(v)
    }
    override fun getItemCount(): Int = images.size
    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(images[position])

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val iv: ImageView = itemView.findViewById(R.id.ivStrip)
        fun bind(res: Int) { iv.setImageResource(res) }
    }
}