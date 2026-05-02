package com.example.linkflow.schedule

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.linkflow.R
import java.text.SimpleDateFormat
import java.util.*

class ScheduleAdapter(
    private val onItemClick: (Schedule) -> Unit   // ⭐ 点击回调
) : RecyclerView.Adapter<ScheduleAdapter.ViewHolder>() {

    private var list: List<Schedule> = emptyList()

    fun submitList(newList: List<Schedule>) {
        list = newList
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvContent: TextView = view.findViewById(R.id.tvContent)
        val tvTime: TextView = view.findViewById(R.id.tvTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_schedule, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        holder.tvContent.text = item.content

        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        holder.tvTime.text = sdf.format(Date(item.triggerTime))

        // ⭐ 点击整行触发（用于删除）
        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
    }
}