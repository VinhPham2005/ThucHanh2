package com.example.thuchanh2.ui.admin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.thuchanh2.data.entity.Employee
import com.example.thuchanh2.databinding.ItemEmployeeBinding

class EmployeeAdapter(
    private var list: List<Employee>,
    private val onClick: (Employee) -> Unit
) : RecyclerView.Adapter<EmployeeAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemEmployeeBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemEmployeeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val emp = list[position]
        holder.binding.tvName.text = emp.fullname
        holder.binding.tvCodeRate.text = "Mã NV: ${emp.code} - Lương/giờ: $${emp.hourlyRate}"
        holder.itemView.setOnClickListener { onClick(emp) }
    }

    override fun getItemCount() = list.size

    fun updateData(newList: List<Employee>) {
        list = newList
        notifyDataSetChanged()
    }
}