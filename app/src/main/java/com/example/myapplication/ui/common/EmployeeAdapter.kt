package com.example.myapplication.ui.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.data.entity.Employee
import com.example.myapplication.databinding.ItemEmployeeBinding
import java.text.NumberFormat
import java.util.Locale

class EmployeeAdapter(
    private var employees: List<Employee>,
    private val onDeleteClick: (Employee) -> Unit
) : RecyclerView.Adapter<EmployeeAdapter.EmployeeViewHolder>() {

    class EmployeeViewHolder(val binding: ItemEmployeeBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeViewHolder {
        val binding = ItemEmployeeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return EmployeeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EmployeeViewHolder, position: Int) {
        val emp = employees[position]
        val currencyFormat = NumberFormat.getNumberInstance(Locale.US)

        with(holder.binding) {
            tvEmpCodeName.text = "${emp.code} - ${emp.fullName}"
            tvHourlyRate.text = "${currencyFormat.format(emp.hourlyRate)} đ/h"
            val emailText = emp.email.ifEmpty { "N/A" }
            val phoneText = emp.phone.ifEmpty { "N/A" }
            tvEmpContact.text = "Email: $emailText | SĐT: $phoneText"

            btnDeleteEmployee.setOnClickListener {
                onDeleteClick(emp)
            }
        }
    }

    override fun getItemCount(): Int = employees.size

    fun updateData(newList: List<Employee>) {
        this.employees = newList
        notifyDataSetChanged()
    }
}