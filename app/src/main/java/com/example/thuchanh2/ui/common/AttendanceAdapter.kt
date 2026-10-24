package com.example.thuchanh2.ui.common

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.thuchanh2.data.entity.WorkLog
import com.example.thuchanh2.databinding.ItemAttendanceBinding
import java.util.Locale

class AttendanceAdapter(
    private var logList: List<WorkLog>
) : RecyclerView.Adapter<AttendanceAdapter.AttendanceViewHolder>() {

    inner class AttendanceViewHolder(val binding: ItemAttendanceBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttendanceViewHolder {
        val binding = ItemAttendanceBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AttendanceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AttendanceViewHolder, position: Int) {
        val log = logList[position]

        with(holder.binding) {
            tvWorkDate.text = "Ngày: ${log.workDate}"

            if (log.status == "COMPLETED") {
                tvStatus.text = "HOÀN THÀNH"
                tvStatus.setTextColor(Color.parseColor("#2E7D32"))
                tvStatus.setBackgroundColor(Color.parseColor("#E8F5E9"))
            } else {
                tvStatus.text = "CHƯA TAN CA"
                tvStatus.setTextColor(Color.parseColor("#EF6C00"))
                tvStatus.setBackgroundColor(Color.parseColor("#FFF3E0"))
            }

            tvCheckIn.text = "Vào ca: ${log.checkInTime}"
            tvCheckOut.text = "Tan ca: ${log.checkOutTime ?: "--:--"}"

            tvTotalHours.text = String.format(Locale.getDefault(), "Tổng: %.2fh", log.totalHours)

            if (log.totalHours > 8.0) {
                val ot = log.totalHours - 8.0
                tvOvertimeHours.visibility = View.VISIBLE
                tvOvertimeHours.text = String.format(Locale.getDefault(), "Tăng ca: %.2fh", ot)
            } else {
                tvOvertimeHours.visibility = View.GONE
            }
        }
    }

    override fun getItemCount(): Int = logList.size

    fun updateData(newLogs: List<WorkLog>) {
        this.logList = newLogs
        notifyDataSetChanged()
    }
}
