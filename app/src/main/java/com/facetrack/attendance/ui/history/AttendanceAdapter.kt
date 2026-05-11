package com.facetrack.attendance.ui.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.facetrack.attendance.databinding.ItemAttendanceBinding
import com.facetrack.attendance.model.Attendance
import java.text.SimpleDateFormat
import java.util.*

class AttendanceAdapter : RecyclerView.Adapter<AttendanceAdapter.ViewHolder>() {

    private var items = emptyList<Attendance>()

    fun submitList(newItems: List<Attendance>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAttendanceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    class ViewHolder(private val binding: ItemAttendanceBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(attendance: Attendance) {
            binding.tvName.text = attendance.name
            binding.tvStatus.text = attendance.status
            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            binding.tvTime.text = sdf.format(Date(attendance.timestamp))
        }
    }
}