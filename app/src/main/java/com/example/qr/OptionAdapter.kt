package com.example.qr

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView

class OptionAdapter(
    private val options: List<Option>,
    private val onOptionClick: (Fragment) -> Unit
) : RecyclerView.Adapter<OptionAdapter.OptionViewHolder>() {

    inner class OptionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val optionName: TextView = itemView.findViewById(R.id.optionName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OptionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_option, parent, false)
        return OptionViewHolder(view)
    }

    override fun onBindViewHolder(holder: OptionViewHolder, position: Int) {
        val option = options[position]
        holder.optionName.text = option.name
        holder.itemView.setOnClickListener { onOptionClick(option.fragment) }
    }

    override fun getItemCount(): Int = options.size
}
