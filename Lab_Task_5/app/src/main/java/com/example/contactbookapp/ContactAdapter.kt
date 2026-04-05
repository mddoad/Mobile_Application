package com.example.contactbookapp

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import kotlin.math.abs

class ContactAdapter(
    context: Context,
    private val items: MutableList<Contact>
) : ArrayAdapter<Contact>(context, 0, items) {

    private data class ViewHolder(
        val tvAvatar: TextView,
        val tvName: TextView,
        val tvPhone: TextView,
        val ivCall: ImageView
    )

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        val holder: ViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_contact, parent, false)
            holder = ViewHolder(
                tvAvatar = view.findViewById(R.id.tvAvatar),
                tvName = view.findViewById(R.id.tvName),
                tvPhone = view.findViewById(R.id.tvPhone),
                ivCall = view.findViewById(R.id.ivCall)
            )
            view.tag = holder
        } else {
            view = convertView
            holder = convertView.tag as ViewHolder
        }

        val contact = getItem(position)!!

        holder.tvAvatar.text = contact.initial
        holder.tvName.text = contact.name
        holder.tvPhone.text = contact.phone

        // Dynamic avatar background color based on first letter
        holder.tvAvatar.background = circleBg(colorForInitial(contact.initial))

        return view
    }

    private fun circleBg(color: Int): GradientDrawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(color)
        }
    }

    private fun colorForInitial(initial: String): Int {
        val c = initial.firstOrNull() ?: 'A'
        val hue = abs(c.code * 37) % 360
        return Color.HSVToColor(floatArrayOf(hue.toFloat(), 0.55f, 0.85f))
    }
}