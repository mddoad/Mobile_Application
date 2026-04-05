package com.example.photogalleryapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView

class PhotoAdapter(
    private val context: Context,
    private val items: MutableList<Photo>
) : BaseAdapter() {

    var selectionMode: Boolean = false
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private data class Holder(
        val iv: ImageView,
        val tv: TextView,
        val cb: CheckBox
    )

    override fun getCount(): Int = items.size
    override fun getItem(position: Int): Any = items[position]
    override fun getItemId(position: Int): Long = items[position].id

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        val holder: Holder

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_photo, parent, false)
            holder = Holder(
                iv = view.findViewById(R.id.ivPhoto),
                tv = view.findViewById(R.id.tvTitle),
                cb = view.findViewById(R.id.cbSelect)
            )
            view.tag = holder
        } else {
            view = convertView
            holder = convertView.tag as Holder
        }

        val photo = items[position]
        holder.iv.setImageResource(photo.resourceId)
        holder.tv.text = photo.title

        holder.cb.visibility = if (selectionMode) View.VISIBLE else View.GONE
        holder.cb.isChecked = photo.isSelected

        return view
    }

    fun setItems(newItems: List<Photo>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}